package org.kiennguyenfpt.datingapp.dtos.responses;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AdminUserSubscriptionResponse {
    private Long id;

    private Long userId;

    private String email;

    private String planName;

    private Timestamp startDate;

    private Timestamp endDate;

    private String status;
}
