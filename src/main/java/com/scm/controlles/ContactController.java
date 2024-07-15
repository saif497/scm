package com.scm.controlles;

import com.scm.entities.Contact;
import com.scm.entities.User;
import com.scm.forms.ContactForm;
import com.scm.forms.ContactSearchForm;
import com.scm.helpers.AppConstants;
import com.scm.helpers.Helper;
import com.scm.helpers.Message;
import com.scm.helpers.MessageType;
import com.scm.services.ContactService;
import com.scm.services.ImageService;
import com.scm.services.UserService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.View;

import java.util.UUID;

@Controller
@RequestMapping("/user/contacts")
public class ContactController {

    private Logger logger = org.slf4j.LoggerFactory.getLogger(ContactController.class);

    @Autowired
    private ContactService contactService;

    @Autowired
    private ImageService imageService;

    @Autowired
    private UserService userService;
    @Autowired
    private View error;

    @RequestMapping("/add")
    //add contat page handler
    public String addContactView (Model model) {

        ContactForm contactForm = new ContactForm();
        model.addAttribute("contactForm", contactForm);
        contactForm.setFavorite(true);
        return "user/add_contact";
    }

    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public String saveContact(@Valid @ModelAttribute ContactForm contactForm, BindingResult result, Authentication authentication, HttpSession session){
        //process the form data
        //1. validate the form data
        if (result.hasErrors()){
            result.getAllErrors().forEach(error -> logger.info(error.toString()));
            session.setAttribute("message", Message.builder()
                    .content("Please correct the errors").type(MessageType.red)
                    .build());
            return "user/add_contact";
        }
        String username= Helper.getEmailOfLoggedInUser(authentication);
        // form --> Contact
        User user = userService.getUserByEmail(username);
        //2.  process the contact profile image
        //  logger.info("File info: {}", contactForm.getContactImage().getOriginalFilename());
        // image processing
        // image upload krne ka code

        Contact contact = new Contact();
        contact.setName(contactForm.getName());
        contact.setFavorite(contactForm.isFavorite());
        contact.setEmail(contactForm.getEmail());
        contact.setPhoneNumber(contactForm.getPhoneNumber());
        contact.setAddress(contactForm.getAddress());
        contact.setDescription(contactForm.getDescription());
        contact.setUser(user);
        contact.setLinkedInLink(contactForm.getLinkedInLink());
        contact.setWebsiteLink(contactForm.getWebsiteLink());

        if(contactForm.getContactImage() != null && !contactForm.getContactImage().isEmpty()){
            String filename = UUID.randomUUID().toString();
            String fileURL = imageService.uploadImage(contactForm.getContactImage(),filename);

            contact.setPicture(fileURL);
            contact.setCloudinaryImagePublicId(filename);
        }

        contactService.save(contact);
        System.out.println(contactForm);

        //3. set the contact picture url

        //4. set message to be displayed on the view
        session.setAttribute("message", Message.builder()
                .content("New Contact Added Successfully").type(MessageType.green)
                .build());
        return "redirect:/user/contacts/add";
    }

    //view contacts
    @GetMapping
    public String viewContacts(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size",defaultValue = AppConstants.PAGE_SIZE+"") int size,
            @RequestParam(value = "sortBy",defaultValue = "name") String sortBy,
            @RequestParam(value = "direction",defaultValue = "asc") String direction,Model model, Authentication authentication){

        //load all the user contacts
        String username =  Helper.getEmailOfLoggedInUser(authentication);
        User user = userService.getUserByEmail(username);
        Page<Contact> pageContact = contactService.getByUser(user,page,size,sortBy,direction);
        model.addAttribute("pageContact", pageContact);
        model.addAttribute("pageSize", AppConstants.PAGE_SIZE);

        model.addAttribute("contactSearchForm", new ContactSearchForm());

        return "user/contacts";
    }

    // search contacts handler
    @GetMapping("/search")
    public String searchHandler(@ModelAttribute ContactSearchForm contactSearchForm,
                                @RequestParam(value = "page", defaultValue = "0") int page,
                                @RequestParam(value = "size", defaultValue = AppConstants.PAGE_SIZE+"") int size,
                                @RequestParam(value = "sortBy", defaultValue = "name") String sortBy,
                                @RequestParam(value = "direction", defaultValue = "asc") String direction,
                                Model model,
                                Authentication authentication){

        logger.info("Field: {}, Keyword: {}", contactSearchForm.getField(),contactSearchForm.getValue());

        var user = userService.getUserByEmail(Helper.getEmailOfLoggedInUser(authentication));


        Page<Contact> pageContact = null;
        if (contactSearchForm.getField().equalsIgnoreCase("name")){
            pageContact = contactService.searchByName(contactSearchForm.getValue(), size, page, sortBy, direction,user);
        }
        else if (contactSearchForm.getField().equalsIgnoreCase("email")) {
            pageContact = contactService.searchByEmail(contactSearchForm.getValue(), size, page, sortBy, direction,user);
        }
        else if (contactSearchForm.getField().equalsIgnoreCase("phone")) {
            pageContact = contactService.searchByPhoneNumber(contactSearchForm.getValue(), size, page, sortBy, direction,user);
        }

        logger.info("pageContact: {}", pageContact);

        model.addAttribute("contactSearchForm", contactSearchForm);

        model.addAttribute("pageContact", pageContact);

        model.addAttribute("pageSize", AppConstants.PAGE_SIZE);


        return "user/search";
    }



    //dalete contact handler
    @RequestMapping("/delete/{contactId}")
    public String daleteContact(@PathVariable("contactId") String contactId, HttpSession session){
        contactService.delete(contactId);
        logger.info("contactId: {} deleted", contactId);
        session.setAttribute("message",
                Message.builder()
                .content("Contact Deleted Successfully !!").type(MessageType.green)
                .build());
        return "redirect:/user/contacts";
    }


    //update contact form view handler
    @GetMapping("/view/{contactId}")
    public String updateContactFormView(@PathVariable("contactId") String contactId, Model model){

       var contact = contactService.getById(contactId);

       ContactForm contactForm = new ContactForm();
       contactForm.setName(contact.getName());
       contactForm.setEmail(contact.getEmail());
       contactForm.setPhoneNumber(contact.getPhoneNumber());
       contactForm.setAddress(contact.getAddress());
       contactForm.setDescription(contact.getDescription());
       contactForm.setFavorite(contact.isFavorite());
       contactForm.setLinkedInLink(contact.getLinkedInLink());
       contactForm.setWebsiteLink(contact.getWebsiteLink());
       contactForm.setPicture(contact.getPicture());
       model.addAttribute("contactForm", contactForm);
       model.addAttribute("contactId", contactId);

        return "user/update_contact_view";
    }


    //update contact handler
    @PostMapping("/update/{contactId}")
    public String updateContact(@PathVariable("contactId") String contactId, @Valid @ModelAttribute ContactForm contactForm,
                                BindingResult bindingResult,
                                Model model){
        // update contact
        if (bindingResult.hasErrors()){
            return "user/update_contact_view";
        }

        var con = contactService.getById(contactId);
        con.setContactId(contactId);
        con.setName(contactForm.getName());
        con.setEmail(contactForm.getEmail());
        con.setAddress(contactForm.getAddress());
        con.setPhoneNumber(contactForm.getPhoneNumber());
        con.setDescription(contactForm.getDescription());
        con.setFavorite(contactForm.isFavorite());
        con.setLinkedInLink(contactForm.getLinkedInLink());
        con.setWebsiteLink(contactForm.getWebsiteLink());
//        con.setPicture(contactForm.getPicture());
        //image process krege
        if (contactForm.getContactImage() != null && !contactForm.getContactImage().isEmpty()){
            //image update
            logger.info("file is not empty");
            String filename = UUID.randomUUID().toString();
            String imageUrl = imageService.uploadImage(contactForm.getContactImage(), filename);
            con.setCloudinaryImagePublicId(filename);
            con.setPicture(imageUrl);
            contactForm.setPicture(imageUrl);
        }else{
            logger.info("file is empty");
        }

        var updateCon =  contactService.update(con);
        logger.info("Contact Updated: {}", updateCon);

        model.addAttribute("message", Message.builder()
                .content("Contact Updated Successfully !!").type(MessageType.green)
                .build());
        return "redirect:/user/contacts/view/" + contactId;
    }
}
