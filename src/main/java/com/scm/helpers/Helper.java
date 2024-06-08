package com.scm.helpers;

import org.springframework.security.core.AuthenticatedPrincipal;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.OAuth2AuthenticatedPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;


public class Helper {

    public static String getEmailOfLoggedInUser(Authentication authentication) {

        // ager email id password se login kiya hai to : email kaise nikalenge
        if (authentication instanceof OAuth2AuthenticationToken) {

            var aOAuth2AuthenticationToken = (OAuth2AuthenticationToken) authentication;
            var clientId = aOAuth2AuthenticationToken.getAuthorizedClientRegistrationId();
            var oauth2User = (OAuth2User) authentication.getPrincipal();
            String username= "";

            if (clientId.equalsIgnoreCase("google")){

                // sing with google
                System.out.println("Getting Email from google account");
                username=oauth2User.getAttribute("email").toString();

            } else if (clientId.equalsIgnoreCase("github")) {

                // sign with github
                System.out.println("Getting Email from github account");
                username = oauth2User.getAttribute("email") != null ? oauth2User.getAttribute("email").toString() :
                        oauth2User.getAttribute("login").toString() + "@gmail.com";

            }
            // sign with facebook
            return username;

        }else {
            // sign with email and password
            System.out.println("Getting data from local database");
            return authentication.getName();
        }

    }

}
