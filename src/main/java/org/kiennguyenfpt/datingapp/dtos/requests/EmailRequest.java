package org.kiennguyenfpt.datingapp.dtos.requests;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class EmailRequest {
    private String to;
    private String subject;
    private String text;
}
