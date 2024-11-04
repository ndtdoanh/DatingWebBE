package org.kiennguyenfpt.datingapp.controllers.admin;

import org.kiennguyenfpt.datingapp.dtos.responses.AdminUserSubscriptionResponse;
import org.kiennguyenfpt.datingapp.dtos.responses.AdminUserWithSubscriptionDetails;
import org.kiennguyenfpt.datingapp.dtos.responses.RevenueStatsResponse;
import org.kiennguyenfpt.datingapp.dtos.responses.SubscriptionStatsResponse;
import org.kiennguyenfpt.datingapp.responses.CommonResponse;
import org.kiennguyenfpt.datingapp.services.UserSubscriptionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin/user-subscriptions")
@CrossOrigin
public class AdminUserSubscriptionController {

    private final UserSubscriptionService userSubscriptionService;

    public AdminUserSubscriptionController(UserSubscriptionService userSubscriptionService) {
        this.userSubscriptionService = userSubscriptionService;
    }

    // 1. Xem danh sách gói đăng ký của người dùng
    @GetMapping
    public ResponseEntity<?> getAllUserSubscriptions() {
        CommonResponse<List<AdminUserSubscriptionResponse>> response = new CommonResponse<>();
        try {
            List<AdminUserSubscriptionResponse> subscriptions = userSubscriptionService.getAllUserSubscriptions();
            response.setStatus(HttpStatus.OK.value());
            response.setMessage("Fetched all user subscriptions successfully!");
            response.setData(subscriptions);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            response.setMessage("Error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // 2. Lấy thông tin chi tiết của một gói đăng ký theo ID
    @GetMapping("/{id}")
    public ResponseEntity getUserSubscriptionById(@PathVariable Long id) {
        CommonResponse response = new CommonResponse<>();
        try {
            AdminUserWithSubscriptionDetails subscription = userSubscriptionService.getUserSubscriptionById(id);
            response.setStatus(HttpStatus.OK.value());
            response.setMessage("Fetched user subscription successfully!");
            response.setData(subscription);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            response.setMessage("Error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/stats/monthly")
    public ResponseEntity<List<SubscriptionStatsResponse>> getMonthlyPackageStats() {
        List<SubscriptionStatsResponse> stats = userSubscriptionService.getPackageCountByMonth();
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/stats/quarterly")
    public ResponseEntity<List<SubscriptionStatsResponse>> getQuarterlyPackageStats() {
        List<SubscriptionStatsResponse> stats = userSubscriptionService.getPackageCountByQuarter();
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/stats/yearly")
    public ResponseEntity<List<SubscriptionStatsResponse>> getYearlyPackageStats() {
        List<SubscriptionStatsResponse> stats = userSubscriptionService.getPackageCountByYear();
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/revenue/monthly")
    public List<RevenueStatsResponse> getMonthlyRevenue() {
        return userSubscriptionService.getMonthlyRevenue();
    }

    @GetMapping("/revenue/quarterly")
    public List<RevenueStatsResponse> getQuarterlyRevenue() {
        return userSubscriptionService.getQuarterlyRevenue();
    }

    @GetMapping("/revenue/yearly")
    public List<RevenueStatsResponse> getYearlyRevenue() {
        return userSubscriptionService.getYearlyRevenue();
    }
}
