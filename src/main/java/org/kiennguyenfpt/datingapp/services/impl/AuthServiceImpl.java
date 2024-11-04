package org.kiennguyenfpt.datingapp.services.impl;

import org.kiennguyenfpt.datingapp.dtos.responses.LoginSuccessfulResponse;
import org.kiennguyenfpt.datingapp.entities.Profile;
import org.kiennguyenfpt.datingapp.entities.Role;
import org.kiennguyenfpt.datingapp.entities.SubscriptionPlan;
import org.kiennguyenfpt.datingapp.entities.User;
import org.kiennguyenfpt.datingapp.entities.UserRole;
import org.kiennguyenfpt.datingapp.entities.UserSubscription;
import org.kiennguyenfpt.datingapp.enums.ROLE;
import org.kiennguyenfpt.datingapp.enums.SubscriptionStatus;
import org.kiennguyenfpt.datingapp.enums.UserStatus;
import org.kiennguyenfpt.datingapp.exceptions.InvalidEmailException;
import org.kiennguyenfpt.datingapp.repositories.ProfileRepository;
import org.kiennguyenfpt.datingapp.repositories.RoleRepository;
import org.kiennguyenfpt.datingapp.repositories.SubscriptionPlanRepository;
import org.kiennguyenfpt.datingapp.repositories.UserRoleRepository;
import org.kiennguyenfpt.datingapp.repositories.UserSubscriptionRepository;
import org.kiennguyenfpt.datingapp.responses.CommonResponse;
import org.kiennguyenfpt.datingapp.security.JwtUtil;
import org.kiennguyenfpt.datingapp.services.AuthService;
import org.kiennguyenfpt.datingapp.services.UserService;
import org.kiennguyenfpt.datingapp.utils.PasswordUtil;
import org.kiennguyenfpt.datingapp.validation.EmailValidator;
import org.kiennguyenfpt.datingapp.validation.PasswordValidator;
import org.kiennguyenfpt.datingapp.validation.ValidationResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;


@Service
public class AuthServiceImpl implements AuthService {
    private static final Logger logger = LoggerFactory.getLogger(AuthServiceImpl.class);

    private final BCryptPasswordEncoder passwordEncoder;

    private final EmailServiceImpl emailService;

    private final UserService userService;

    private final RoleRepository roleRepository;

    private final UserRoleRepository userRoleRepository;

    private final ProfileRepository profileRepository;

    private final JwtUtil jwtUtil;
    private final SubscriptionPlanRepository subscriptionPlanRepository;
    private final UserSubscriptionRepository userSubscriptionRepository;

    public AuthServiceImpl(
            BCryptPasswordEncoder passwordEncoder,
            EmailServiceImpl emailService,
            UserService userService,
            JwtUtil jwtUtil,
            RoleRepository roleRepository,
            UserRoleRepository userRoleRepository,
            SubscriptionPlanRepository subscriptionPlanRepository,
            UserSubscriptionRepository userSubscriptionRepository,
            ProfileRepository profileRepository
    ) {
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
        this.userService = userService;
        this.jwtUtil = jwtUtil;
        this.roleRepository = roleRepository;
        this.userRoleRepository = userRoleRepository;
        this.subscriptionPlanRepository = subscriptionPlanRepository;
        this.userSubscriptionRepository = userSubscriptionRepository;
        this.profileRepository = profileRepository;
    }

    @Override
    public User register(String email) {
        validateEmail(email);
        checkEmailExists(email);

        User user = createUser(email);
        String randomPassword = PasswordUtil.generateRandomPassword();
        user.setPasswordHash(passwordEncoder.encode(randomPassword));

        try {
            // Lưu user
            User savedUser = userService.save(user);

            // Lưu UserRole
            UserRole userRole = new UserRole();
            userRole.setRole(getUserRole());
            userRole.setUser(savedUser);
            userRoleRepository.save(userRole);

            // ** Tự động gán gói Free cho user **
            assignFreePlanToUser(savedUser);

            // Gửi email xác nhận với mật khẩu ngẫu nhiên
            sendEmail(savedUser, randomPassword);
            return savedUser;
        } catch (Exception e) {
            logger.error("Error during registration: {}", e.getMessage(), e);
            throw new RuntimeException("Error during registration: " + e.getMessage(), e);
        }
    }

    @Override
    public ResponseEntity login(String email, String password) {
        CommonResponse response = new CommonResponse<>();
        User user = userService.findByEmail(email);
        String message;
        Profile profile = null;
        Optional<UserSubscription> optionalUserSubscription = null;
        if (user.getStatus() == UserStatus.INACTIVE) {
            message = "User has been locked";
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            response.setMessage(message);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
        }
        if (user != null && passwordEncoder.matches(password, user.getPasswordHash())) {
            String token = jwtUtil.generateToken(email, user.getUserId());
            logger.info("User logged in: {}", email);

            // Tạo thông điệp dựa trên số lần đăng nhập
            if (user.isFirstLogin()) {
                user.setFirstLogin(false);
                message = "First login";
            } else if (user.getLoginCount() == 1) {
                message = "Second login";
            } else {
                message = "Login successful";
                profile = profileRepository.findByUser_UserId(user.getUserId());
                optionalUserSubscription = userSubscriptionRepository.findActiveSubscriptionByUserId(user.getUserId());
            }
            user.setLoginCount(user.getLoginCount() + 1);
            userService.save(user);

            response.setStatus(HttpStatus.OK.value());
            response.setMessage(message);

            LoginSuccessfulResponse successfulResponse = new LoginSuccessfulResponse(
                    user.getEmail(),
                    profile != null ? profile.getName() : "-",
                    token,
                    user.getUserRoles().get(0).getRole().getRoleName(),
                    (optionalUserSubscription != null && optionalUserSubscription.isPresent())
                            ? optionalUserSubscription.get().getSubscriptionPlan().getName() : "FREE"
            );

            response.setData(successfulResponse);
            return ResponseEntity.ok(response);
        }
        response.setStatus(HttpStatus.BAD_REQUEST.value());
        response.setMessage("Invalid email or password");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @Override
    public User forgotPassword(String email) {
        User user = userService.findByEmail(email);
        if (user == null) {
            throw new InvalidEmailException("Email not found.");
        }
        String randomPassword = PasswordUtil.generateRandomPassword();
        user.setPasswordHash(passwordEncoder.encode(randomPassword));
        userService.save(user);
        emailService.sendEmail(user.getEmail(), "Password Reset", "Your new password is: " + randomPassword);
        return user;
    }

    @Override
    public User changePassword(String email, String oldPassword, String newPassword) {
        logger.info("Attempting to change password for email: {}", email);

        ValidationResult emailValidationResult = EmailValidator.validate(email);
        if (!emailValidationResult.valid()) {
            logger.warn("Invalid email format for email: {}", email);
            throw new IllegalArgumentException(emailValidationResult.message());
        }

        ValidationResult passwordValidationResult = PasswordValidator.validate(newPassword);
        if (!passwordValidationResult.valid()) {
            logger.warn("Invalid new password format for email: {}", email);
            throw new IllegalArgumentException(passwordValidationResult.message());
        }

        User user = userService.findByEmail(email);
        if (user == null) {
            throw new InvalidEmailException("Email not found.");
        }
        if (!passwordEncoder.matches(oldPassword, user.getPasswordHash())) {
            throw new IllegalArgumentException("Invalid old password.");
        }

        user.setPasswordHash(passwordEncoder.encode(newPassword));
        user.setFirstLogin(false);
        User updatedUser = userService.save(user);
        logger.info("Password changed successfully for email: {}", email);
        return updatedUser;
    }

    @Override
    public void validateEmail(String email) {
        ValidationResult result = EmailValidator.validate(email);
        if (!result.valid()) {
            throw new IllegalArgumentException(result.message());
        }
    }

    private void checkEmailExists(String email) {
        if (userService.findByEmail(email) != null) {
            logger.error("Email already exists: {}", email);
            throw new IllegalArgumentException("Email already exists!");
        }
    }

    private User createUser(String email) {
        User user = new User();
        user.setEmail(email);
        user.setStatus(UserStatus.ACTIVE);
        user.setFirstLogin(true);
        return user;
    }

    private void sendEmail(User user, String randomPassword) {
        String subject = "Your Temporary Password from our dating system";
        String htmlContent = "<html>" +
                "<body>" +
                "<div style='font-family: Arial, sans-serif; max-width: 600px; margin: auto; padding: 20px; border: 1px solid #ccc; border-radius: 10px;'>" +
                "<h2 style='color: #333;'>Hello, " + user.getEmail() + "!</h2>" +
                "<p style='font-size: 16px; color: #555;'>You have requested a temporary password. Here is your temporary password:</p>" +
                "<p style='font-size: 18px; font-weight: bold; color: #333;'> " + randomPassword + " </p>" +
                "<p style='font-size: 14px; color: #777;'>Please use this password to log in and don't forget to change it after logging in.</p>" +
                "<br>" +
                "<p style='font-size: 14px; color: #777;'>Regards,<br>The Dating App Team</p>" +
                "</div>" +
                "</body>" +
                "</html>";

        emailService.sendEmail(user.getEmail(), subject, htmlContent);
    }

    // Phương thức để gán gói Free cho user
    private void assignFreePlanToUser(User user) {
        // Lấy thông tin gói Free từ database
        SubscriptionPlan freePlan = subscriptionPlanRepository.findByName("FREE").orElseThrow(
                () -> new IllegalStateException("Free plan not found")
        );

        // Tạo một bản ghi UserSubscription cho người dùng
        UserSubscription userSubscription = new UserSubscription();
        userSubscription.setUser(user); // Sử dụng đối tượng User thay vì userId
        userSubscription.setSubscriptionPlan(freePlan);
        userSubscription.setStatus(SubscriptionStatus.ACTIVE); // Sử dụng Enum thay vì chuỗi
        userSubscription.setStartDate(LocalDateTime.now());
        userSubscription.setEndDate(null);
        // Lưu UserSubscription vào database
        userSubscriptionRepository.save(userSubscription);
    }

    private Role getUserRole() {
        return roleRepository.findByRoleName(ROLE.USER.value());
    }
}
