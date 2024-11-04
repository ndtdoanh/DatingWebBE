package org.kiennguyenfpt.datingapp.dtos.requests;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ContactUsRequest {

    private String fullName;

    private String email;

    private String phoneNumber;

    private String message;

    private LocalDateTime submissionDate = LocalDateTime.now(); // Auto-generated when a form is submitted

    private String responseStatus = "Not Responded"; // Default status

}
