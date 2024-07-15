package com.scm.controlles;

import com.scm.entities.Contact;
import com.scm.entities.User;
import com.scm.forms.ContactForm;
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
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
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
    public String saveContact(@Valid @ModelAttribute ContactForm contactForm, BindingResult result,
                              Authentication authentication, HttpSession session){
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
//        logger.info("File info: {}", contactForm.getContactImage().getOriginalFilename());
        // image processing
        // image upload krne ka code

        String filename = UUID.randomUUID().toString();
        String fileURL = imageService.uploadImage(contactForm.getContactImage(),filename);


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
        contact.setPicture(fileURL);
        contact.setCloudinaryImagePublicId(filename);
        contactService.save(contact);
        System.out.println(contactForm);

        //3. set the contact picture url

        //4. set message to be displayed on the view
        session.setAttribute("message", Message.builder()
                .content("New Contact Added Successfully").type(MessageType.green)
                .build());
        return "redirect:/user/contacts/add";
    }

}
