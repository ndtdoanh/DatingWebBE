package org.kiennguyenfpt.datingapp.services;

import org.kiennguyenfpt.datingapp.entities.User;
import org.kiennguyenfpt.datingapp.responses.CommonResponse;
import org.springframework.http.ResponseEntity;

public interface AuthService {
    User register(String email);

    ResponseEntity<CommonResponse<String>> login(String email, String password);

    User changePassword(String email, String oldPassword, String newPassword);

    User forgotPassword(String email);

    void validateEmail(String email);

}
