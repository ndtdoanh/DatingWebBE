package org.kiennguyenfpt.datingapp.services.impl;

import org.kiennguyenfpt.datingapp.dtos.requests.ContactUsRequest;
import org.kiennguyenfpt.datingapp.entities.ContactForm;
import org.kiennguyenfpt.datingapp.repositories.ContactFormRepository;
import org.kiennguyenfpt.datingapp.services.ContactFormService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ContactFormServiceImpl implements ContactFormService {
    private final ContactFormRepository contactFormRepository;

    private final EmailServiceImpl emailService;

    public ContactFormServiceImpl(
            ContactFormRepository contactFormRepository,
            EmailServiceImpl emailService
    ) {
        this.contactFormRepository = contactFormRepository;
        this.emailService = emailService;
    }

    @Override
    public List<ContactForm> getAllContactForms() {
        // Fetch all contact forms from the database
        return contactFormRepository.findAll();
    }

    @Override
    public int replyToContactForm(Long id, String replyMessage) {
        // Fetch contact form by ID
        Optional<ContactForm> contactFormOpt = contactFormRepository.findById(id);
        if (contactFormOpt.isEmpty()) {
            return 0;
        }
        ContactForm contactForm = contactFormOpt.get();

        // Update the contact form's reply details
        contactForm.setResponseDate(LocalDateTime.now());  // Set current date as reply date
        contactForm.setResponseStatus("Responded"); // Mark the form as responded

        // Save the updated contact form back to the database
        contactFormRepository.save(contactForm);
        sendEmail(contactFormOpt.get().getEmail(), replyMessage);
        return 1; // Success
    }

    private void sendEmail(String email, String htmlContent) {
        String subject = "Problem Solving Support";

        emailService.sendEmail(email, subject, htmlContent);
    }

    @Override
    public void sendContactForm(ContactUsRequest contactUsRequest) {
        // Tạo một entity ContactForm từ ContactUsRequest
        ContactForm contactForm = new ContactForm();
        contactForm.setFullName(contactUsRequest.getFullName());
        contactForm.setEmail(contactUsRequest.getEmail());
        contactForm.setPhoneNumber(contactUsRequest.getPhoneNumber());
        contactForm.setMessage(contactUsRequest.getMessage());
        contactForm.setSubmissionDate(LocalDateTime.now());
        contactForm.setResponseStatus("Not Responded");

        // Lưu contactForm vào database
        contactFormRepository.save(contactForm);
        System.out.println("Contact form has been successfully saved.");
    }

}
