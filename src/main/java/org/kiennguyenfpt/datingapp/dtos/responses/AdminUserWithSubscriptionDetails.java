package org.kiennguyenfpt.datingapp.dtos.responses;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AdminUserWithSubscriptionDetails {

    private Long userId;

    private String profileName;

    private String email;

    private String address;

    private String status;

    private String roleName;

    private String bio;

    private String gender;

    private String phone;

    private String subscriptionPlanName;

    private Timestamp subscriptionStartDate;

    private Timestamp subscriptionEndDate;

    private String subscriptionStatus;

    private List<String> photoUrls;
}
