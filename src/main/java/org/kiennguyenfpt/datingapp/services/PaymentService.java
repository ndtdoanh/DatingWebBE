package org.kiennguyenfpt.datingapp.services;

import org.kiennguyenfpt.datingapp.dtos.responses.PaymentResponse;

import java.util.List;

public interface PaymentService {
    void processPayment(Long userId, Double amount);

    List<PaymentResponse> getPaymentHistory(Long userId);
}
