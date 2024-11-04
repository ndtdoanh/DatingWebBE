package org.kiennguyenfpt.datingapp.controllers.admin;

import org.kiennguyenfpt.datingapp.dtos.responses.PaymentResponse;
import org.kiennguyenfpt.datingapp.responses.CommonResponse;
import org.kiennguyenfpt.datingapp.services.PaymentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin/payment")
@CrossOrigin
public class AdminPaymentController {
    private final PaymentService paymentService; // Inject PaymentService

    public AdminPaymentController(
            PaymentService paymentService
    ) {
        this.paymentService = paymentService;
    }

    // 1. Xem lịch sử các giao dịch
    @GetMapping
    public ResponseEntity getPaymentHistory(@RequestParam(value = "userId", required = false) Long userId) {
        CommonResponse response = new CommonResponse<>();
        try {
            List<PaymentResponse> paymentResponses = paymentService.getPaymentHistory(userId);
            response.setStatus(HttpStatus.OK.value());
            response.setMessage("Get payment history successfully!");
            response.setData(paymentResponses);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            response.setMessage("Error: " + e.getMessage());

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}

