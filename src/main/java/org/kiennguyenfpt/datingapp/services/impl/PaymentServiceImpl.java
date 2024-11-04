package org.kiennguyenfpt.datingapp.services.impl;

import org.kiennguyenfpt.datingapp.dtos.responses.PaymentResponse;
import org.kiennguyenfpt.datingapp.entities.User;
import org.kiennguyenfpt.datingapp.repositories.PaymentRepository;
import org.kiennguyenfpt.datingapp.repositories.UserRepository;
import org.kiennguyenfpt.datingapp.services.PaymentService;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PaymentServiceImpl implements PaymentService {
    private final UserRepository userRepository;

    private final PaymentRepository paymentRepository;

    public PaymentServiceImpl(
            final UserRepository userRepository,
            final PaymentRepository paymentRepository
    ) {
        this.userRepository = userRepository;
        this.paymentRepository = paymentRepository;
    }

    @Override
    public void processPayment(Long userId, Double amount) {
        User user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("User not found"));
        // Logic to process payment (e.g., integrate with payment gateway)
        System.out.println("Processing payment of " + amount + " for user " + user.getEmail());
    }

    public List<PaymentResponse> getPaymentHistory(Long userId) {
        List<Object[]> rawPayments;

        // If userId is provided, fetch payments by userId, otherwise fetch all
        if (userId != null) {
            rawPayments = paymentRepository.findPaymentsByUserId(userId);
        } else {
            rawPayments = paymentRepository.findAllPayments();
        }

        return rawPayments.stream()
                .map(result -> new PaymentResponse(
                        (Long) result[0],    // payment_id
                        (Double) result[1],  // amount
                        (Timestamp)(result[2]),  // date
                        (Long) result[3],    // user_id
                        (String) result[4],   // address
                        (String) result[5]   // address
                ))
                .collect(Collectors.toList());
    }

}
