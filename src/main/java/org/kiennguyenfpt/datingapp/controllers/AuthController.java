package org.kiennguyenfpt.datingapp.controllers;

import org.kiennguyenfpt.datingapp.dtos.mapper.UserMapper;
import org.kiennguyenfpt.datingapp.dtos.requests.ChangePasswordRequest;
import org.kiennguyenfpt.datingapp.dtos.requests.ForgotPasswordRequest;
import org.kiennguyenfpt.datingapp.dtos.requests.UserLoginRequest;
import org.kiennguyenfpt.datingapp.dtos.requests.UserRegistrationRequest;
import org.kiennguyenfpt.datingapp.dtos.responses.UserResponse;
import org.kiennguyenfpt.datingapp.entities.User;
import org.kiennguyenfpt.datingapp.exceptions.InvalidEmailException;
import org.kiennguyenfpt.datingapp.responses.CommonResponse;
import org.kiennguyenfpt.datingapp.security.JwtUtil;
import org.kiennguyenfpt.datingapp.services.AuthService;
import org.kiennguyenfpt.datingapp.validation.EmailValidator;
import org.kiennguyenfpt.datingapp.validation.ValidationResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin("*")
@RestController
@RequestMapping("api/v1/auth")
public class AuthController {
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    private final AuthService authService;
    private final UserMapper userMapper;
    private final JwtUtil jwtUtil;

    public AuthController(AuthService authService, UserMapper userMapper, JwtUtil jwtUtil) {
        this.authService = authService;
        this.userMapper = userMapper;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/register")
    public ResponseEntity<CommonResponse<UserResponse>> register(@RequestBody UserRegistrationRequest userReq) {
        CommonResponse<UserResponse> response = new CommonResponse<>();
        try {
            ValidationResult emailValidationResult = EmailValidator.validate(userReq.getEmail());
            if (!emailValidationResult.valid()) {
                throw new InvalidEmailException(emailValidationResult.message());
            }

            User user = authService.register(userReq.getEmail());
            if (user != null) {
                UserResponse userResponse = userMapper.userToUserResponse(user);
                response.setStatus(HttpStatus.OK.value());
                response.setMessage("User registered successfully!");
                response.setData(userResponse);
                return ResponseEntity.ok(response);
            }

            response.setStatus(HttpStatus.BAD_REQUEST.value());
            response.setMessage("Error registering user.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        } catch (InvalidEmailException | IllegalArgumentException e) {
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            response.setMessage(e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        } catch (Exception e) {
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            response.setMessage("Error during registration: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<CommonResponse<String>> login(@RequestBody UserLoginRequest loginRequest) {
        authService.validateEmail(loginRequest.getEmail());
        return authService.login(loginRequest.getEmail(), loginRequest.getPassword());
    }

    @PostMapping("/logout")
    public ResponseEntity<CommonResponse<String>> logout(@RequestBody String token) {
        CommonResponse<String> response = new CommonResponse<>();
        try {
            jwtUtil.invalidateToken(token);
            response.setStatus(HttpStatus.OK.value());
            response.setMessage("Logout successful.");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            response.setMessage("Error during logout: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PostMapping("/change-password")
    public ResponseEntity<CommonResponse<String>> changePassword(
            @RequestBody ChangePasswordRequest changePasswordRequest) {
        CommonResponse<String> response = new CommonResponse<>();
        try {
            authService.validateEmail(changePasswordRequest.getEmail());
            logger.info("Received change password request for email: {}", changePasswordRequest.getEmail());

            User user = authService.changePassword(changePasswordRequest.getEmail(),
                    changePasswordRequest.getOldPassword(), changePasswordRequest.getNewPassword());

            if (user != null) {
                logger.info("Password changed successfully for email: {}", changePasswordRequest.getEmail());
                response.setStatus(HttpStatus.OK.value());
                response.setMessage("Password changed successfully!");
                return ResponseEntity.ok(response);
            }

            logger.warn("Invalid email or password for email: {}", changePasswordRequest.getEmail());
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            response.setMessage("Invalid email or password.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        } catch (InvalidEmailException e) {
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            response.setMessage(e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        } catch (Exception e) {
            logger.error("Error during password change for email: {}", changePasswordRequest.getEmail(), e);
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            response.setMessage("Error during password change: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<CommonResponse<User>> forgotPassword(
            @RequestBody ForgotPasswordRequest forgotPasswordRequest) {
        CommonResponse<User> response = new CommonResponse<>();

        try {
            authService.validateEmail(forgotPasswordRequest.getEmail());
            User user = authService.forgotPassword(forgotPasswordRequest.getEmail());
            if (user != null) {
                response.setStatus(HttpStatus.OK.value());
                response.setMessage("Password reset successfully. Please check your email for the new password.");
                response.setData(user);
                return ResponseEntity.ok(response);
            } else {
                response.setStatus(HttpStatus.BAD_REQUEST.value());
                response.setMessage("Invalid email");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }
        } catch (InvalidEmailException | IllegalArgumentException e) {
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            response.setMessage(e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        } catch (Exception e) {
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            response.setMessage("Error resetting password: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

}
