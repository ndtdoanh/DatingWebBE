package org.kiennguyenfpt.datingapp.controllers.admin;

import org.kiennguyenfpt.datingapp.dtos.requests.ContactFormRequest;
import org.kiennguyenfpt.datingapp.responses.CommonResponse;
import org.kiennguyenfpt.datingapp.services.ContactFormService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/admin/contact-form")
@CrossOrigin
@PreAuthorize("hasRole('Admin')")
public class AdminContactFormController {
    private final ContactFormService contactFormService; // Use ContactFormService instead of CafeService

    public AdminContactFormController(ContactFormService contactFormService) {
        this.contactFormService = contactFormService;
    }

    @GetMapping
    public ResponseEntity getAllContactForms() {
        CommonResponse response = new CommonResponse<>();
        try {
            response.setStatus(HttpStatus.OK.value());
            response.setMessage("Get list of contact forms successfully!");
            response.setData(contactFormService.getAllContactForms()); // Fetch contact forms instead of cafes
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            response.setMessage("Error: " + e.getMessage());

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PutMapping("/{id}/reply")
    public ResponseEntity replyToContactForm(@PathVariable Long id, @RequestBody ContactFormRequest contactFormRequest) { // Add a replyMessage
        CommonResponse response = new CommonResponse<>();
        try {
            int result = contactFormService.replyToContactForm(id, contactFormRequest.getReplyMessage()); // Updated service method
            if (result == 1) {
                response.setStatus(HttpStatus.OK.value());
                response.setMessage("Reply sent successfully!");
                return ResponseEntity.ok(response);
            } else {
                response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
                response.setMessage("Failed to send reply");
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
            }
        } catch (Exception e) {
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            response.setMessage("Error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

}
