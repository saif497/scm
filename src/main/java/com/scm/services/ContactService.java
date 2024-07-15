package com.scm.services;

import com.scm.entities.Contact;
import com.scm.entities.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ContactService {

    // save contacts
    Contact save(Contact contact);

    //update contacts
    Contact update(Contact contact);

    //get contact
    List<Contact> getAll();

    // get contact by id
    Contact getById(String id);

    // delete contact
    void delete(String id);

    //search contact
    Page<Contact> searchByName(String nameKeyword,int page, int size,String sortBy, String direction,User user);
    Page<Contact> searchByEmail(String emailKeyword,int page, int size,String sortBy, String direction,User user);
    Page<Contact> searchByPhoneNumber(String phoneNumberKeyword,int page, int size,String sortBy, String direction,User user);

    // get contact by user id
    List<Contact> getByUserId(String userId);

    Page<Contact> getByUser(User user, int page, int size,String sortBy, String direction);


}
