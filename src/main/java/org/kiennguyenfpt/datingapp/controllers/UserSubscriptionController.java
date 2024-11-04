package org.kiennguyenfpt.datingapp.controllers;

import org.kiennguyenfpt.datingapp.dtos.responses.SubscriptionPlanResponse;
import org.kiennguyenfpt.datingapp.responses.CommonResponse;
import org.kiennguyenfpt.datingapp.services.UserService;
import org.kiennguyenfpt.datingapp.services.UserSubscriptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/subscriptions")
@CrossOrigin
public class UserSubscriptionController {

    private final UserSubscriptionService userSubscriptionService;
    private final UserService userService;

    @Autowired
    public UserSubscriptionController(UserSubscriptionService userSubscriptionService, UserService userService) {
        this.userSubscriptionService = userSubscriptionService;
        this.userService = userService;
    }

    @GetMapping("/current")
    public ResponseEntity<CommonResponse<SubscriptionPlanResponse>> getCurrentSubscriptionPlan(Authentication authentication) {
        CommonResponse<SubscriptionPlanResponse> response = new CommonResponse<>();
        try {
            // Get email from Authentication (JWT token)
            String email = authentication.getName();

            // Find user by email from JWT
            Long userId = userService.findByEmail(email).getUserId();

            // Get the user's current subscription plan
            SubscriptionPlanResponse subscriptionPlan = userSubscriptionService.getActiveSubscriptionByUserId(userId);

            if (subscriptionPlan == null) {
                response.setStatus(HttpStatus.NOT_FOUND.value());
                response.setMessage("User does not have an active subscription.");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }

            response.setStatus(HttpStatus.OK.value());
            response.setMessage("Subscription plan retrieved successfully.");
            response.setData(subscriptionPlan);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            response.setMessage("Error retrieving subscription plan: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}
