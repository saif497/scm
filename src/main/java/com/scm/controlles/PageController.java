package com.scm.controlles;

import com.scm.entities.User;
import com.scm.forms.UserForm;
import com.scm.helpers.Message;
import com.scm.helpers.MessageType;
import com.scm.services.UserService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class PageController {

    @Autowired
    private UserService userService;

    // home page
    @RequestMapping("/")
    public String index(Model model) {
        return "redirect:/home";
    }

    @RequestMapping("/home")
    public String home(Model model) {
        System.out.println("home page handler");
        // sending the data to the view
        model.addAttribute("title", "SCM - Home");
        model.addAttribute("greeting", "Welcome to SCM");
        model.addAttribute("tagline", "The one and only SCM");
        model.addAttribute("google", "https://www.google.co.uk/");
        return "home";
    }

    // about page
    @RequestMapping("/about")
    public String aboutPage(Model model) {
        System.out.println("about page handler");
        // sending the data to the view
        model.addAttribute("isLogin", true);
        // model.addAttribute("greeting", "Welcome to SCM-About page");
        return "about";
    }

    // services page
    @RequestMapping("/services")
    public String servicesPage(Model model) {
        System.out.println("services page handler");
        // sending the data to the view
        model.addAttribute("title", "SCM - Services");
        return "services";
    }

    // contact page
    @RequestMapping("/contact")
    public String contact(Model model) {
        System.out.println("contact page handler");
        return "contact";
    }

    // login pag
    @RequestMapping("/login")
    public String login() {
        System.out.println("login page handler");
        return "login";
    }

    // register page
    @RequestMapping("/register")
    public String register(Model model) {
        UserForm userForm = new UserForm();
        // default data bhi daal sakte hai
        // userForm.setName("MD SAIF ALAM");
        // userForm.setEmail("msas@gmail.com");

        model.addAttribute("userForm", userForm);
        return "register";
    }

    // processing register
    @RequestMapping(value = "/do-register", method = RequestMethod.POST)
    public String processingRegister(@Valid @ModelAttribute UserForm userForm, BindingResult rBindingResult, HttpSession session) {
        System.out.println("Processing registration");
        // User form name se class banayenge
        System.out.println(userForm);
        // fetch form data
        // validate form data
        if (rBindingResult.hasErrors()){
            return "register"; // agar error hai to register page pe hi rahega
        }

        // save to database ==> crete user service class
//        User user = User.builder()
//                .name(userForm.getName())
//                .email(userForm.getEmail())
//                .password(userForm.getPassword())
//                .about(userForm.getAbout())
//                .phoneNumber(userForm.getPhoneNumber())
//                .build();

        User user = new User();
        user.setName(userForm.getName());
        user.setEmail(userForm.getEmail());
        user.setPassword(userForm.getPassword());
        user.setAbout(userForm.getAbout());
        user.setPhoneNumber(userForm.getPhoneNumber());
        user.setProfilePic_link("https://www.freepik.com/free-photos-vectors/default-profile");

        User savedUser = userService.saveUser(user);
        System.out.println("User Saved : ");

        // message = "Registration Successful"
        Message message = Message.builder().content("Registration Successful").type(MessageType.green).build();
        session.setAttribute("message", message);

        // redirect to login page
        return "redirect:/register";
    }
}
