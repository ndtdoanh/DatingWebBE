package org.kiennguyenfpt.datingapp.dtos.responses;

import java.sql.Timestamp;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {
    private long userId;
    private String email;
    //private String name;
    //private String password;
    //private String passwordHash;
    private Timestamp createdAt;
    private boolean firstLogin;
    private Timestamp lastLogin;
    private String status;
}
