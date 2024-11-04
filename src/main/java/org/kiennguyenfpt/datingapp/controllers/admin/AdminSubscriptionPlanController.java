package org.kiennguyenfpt.datingapp.controllers.admin;

import org.kiennguyenfpt.datingapp.entities.SubscriptionPlan;
import org.kiennguyenfpt.datingapp.responses.CommonResponse;
import org.kiennguyenfpt.datingapp.services.SubscriptionPlanService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin/subscriptionPlan")
@CrossOrigin
public class AdminSubscriptionPlanController {
    private final SubscriptionPlanService subscriptionPlanService;

    public AdminSubscriptionPlanController(
            SubscriptionPlanService subscriptionPlanService
    ) {
        this.subscriptionPlanService = subscriptionPlanService;
    }

    @GetMapping
    public ResponseEntity getAllSubscriptionPlans() {
        CommonResponse response = new CommonResponse<>();
        try {
            List<SubscriptionPlan> responses = subscriptionPlanService.getAllSubscriptionPlans();
            response.setStatus(HttpStatus.OK.value());
            response.setMessage("Get list subscriptionPlan successfully!");
            response.setData(responses);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            response.setMessage("Error: " + e.getMessage());

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PutMapping("/{planId}")
    public ResponseEntity<CommonResponse<SubscriptionPlan>> updateSubscriptionPlan(
            @PathVariable Long planId,
            @RequestBody SubscriptionPlan updatedSubscriptionPlan
    ) {
        CommonResponse<SubscriptionPlan> response = new CommonResponse<>();
        try {
            SubscriptionPlan updatedPlan = subscriptionPlanService.updateSubscriptionPlan(planId, updatedSubscriptionPlan);
            response.setStatus(HttpStatus.OK.value());
            response.setMessage("Subscription plan updated successfully!");
            response.setData(updatedPlan);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            response.setMessage("Error: " + e.getMessage());

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}
