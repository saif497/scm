package com.scm.services.impl;

import com.scm.entities.Contact;
import com.scm.helpers.ResourceNotFoundException;
import com.scm.repositiories.ContactRepo;
import com.scm.services.ContactService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class ContactServiceImpl implements ContactService{

    @Autowired
    private ContactRepo contactRepo;

    // save contact impl
    @Override
    public Contact save(Contact contact) {
        String contactId = UUID.randomUUID().toString();
        contact.setContactId(contactId);
        return contactRepo.save(contact);
    }

    // update contact impl
    @Override
    public Contact update(Contact contact) {
        return null;
    }

    // get all contacts impl
    @Override
    public List<Contact> getAll() {
        return contactRepo.findAll();
    }

    // get contact by id impl
    @Override
    public Contact getById(String id) {
        return contactRepo.findById(id).orElseThrow( ()->
                new ResourceNotFoundException("Contact not found with id: "+id) );
    }

    // delete contact by id impl
    @Override
    public void delete(String id) {
        var contact = contactRepo.findById(id).orElseThrow( ()->
                new ResourceNotFoundException("Contact not found with id: "+id) );
        contactRepo.delete(contact);
    }

    // search contact impl
    @Override
    public List<Contact> search(String name, String email, String phoneNumber) {
        return List.of();
    }

    // get contact by user id impl
    @Override
    public List<Contact> getByUserId(String userId) {

        return contactRepo.findByUserId(userId);
    }
}
