package org.kiennguyenfpt.datingapp.services;

import org.kiennguyenfpt.datingapp.dtos.requests.ContactUsRequest;
import org.kiennguyenfpt.datingapp.entities.ContactForm;

import java.util.List;

public interface ContactFormService {
    List<ContactForm> getAllContactForms(); // Fetch all contact forms

    int replyToContactForm(Long id, String replyMessage); // Handle replying to a contact form

    void sendContactForm(ContactUsRequest contactUsRequest);
}
