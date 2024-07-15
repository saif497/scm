package com.scm.services.impl;

import com.scm.entities.Contact;
import com.scm.entities.User;
import com.scm.helpers.ResourceNotFoundException;
import com.scm.repositiories.ContactRepo;
import com.scm.services.ContactService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
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
        var contactOld = contactRepo.findById(contact.getContactId()).orElseThrow(()->
                new ResourceNotFoundException("Contact not found with id: "));
        contactOld.setName(contact.getName());
        contactOld.setEmail(contact.getEmail());
        contactOld.setPhoneNumber(contact.getPhoneNumber());
        contactOld.setAddress(contact.getAddress());
        contactOld.setDescription(contact.getDescription());
        contactOld.setFavorite(contact.isFavorite());
        contactOld.setWebsiteLink(contact.getWebsiteLink());
        contactOld.setLinkedInLink(contact.getLinkedInLink());
        contactOld.setPicture(contact.getPicture());
        contactOld.setCloudinaryImagePublicId(contact.getCloudinaryImagePublicId());
//        contactOld.setLinks(contact.getLinks());
        return contactRepo.save(contactOld);
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

    // get contact by user id impl
    @Override
    public List<Contact> getByUserId(String userId) {

        return contactRepo.findByUserId(userId);
    }

    @Override
    public Page<Contact> getByUser(User user,int page, int size,String sortBy, String direction) {

        Sort sort = direction.equals("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        var pageable = PageRequest.of(page, size, sort);

       return contactRepo.findByUser(user, pageable);
    }

    // search contact impl
    @Override
    public Page<Contact> searchByName(String nameKeyword, int size, int page, String sortBy, String direction, User user) {

        Sort sort = direction.equals("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        var pageable = PageRequest.of(page, size, sort);
        return contactRepo.findByUserAndNameContaining(user, nameKeyword, pageable);
    }

    @Override
    public Page<Contact> searchByEmail(String emailKeyword, int size, int page, String sortBy, String direction, User user) {
        Sort sort = direction.equals("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        var pageable = PageRequest.of(page, size, sort);
        return contactRepo.findByUserAndEmailContaining(user, emailKeyword, pageable);
    }

    @Override
    public Page<Contact> searchByPhoneNumber(String phoneNumberKeyword, int size, int page, String sortBy, String direction, User user) {
        Sort sort = direction.equals("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        var pageable = PageRequest.of(page, size, sort);
        return contactRepo.findByUserAndPhoneNumberContaining(user, phoneNumberKeyword, pageable);
    }



}
