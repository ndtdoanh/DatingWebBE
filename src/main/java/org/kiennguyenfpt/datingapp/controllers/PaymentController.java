package org.kiennguyenfpt.datingapp.controllers;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.kiennguyenfpt.datingapp.dtos.PaymentDTO;
import org.kiennguyenfpt.datingapp.dtos.responses.ResponseObject;
import org.kiennguyenfpt.datingapp.services.PaymentVNPayService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/payment")
@Slf4j
@RequiredArgsConstructor
public class PaymentController {
    private final PaymentVNPayService paymentVNPayService;
    @GetMapping("/vn-pay")
    public ResponseObject<PaymentDTO.VNPayResponse> pay(HttpServletRequest request) {
        try {
            return new ResponseObject<>(HttpStatus.OK, "Success", paymentVNPayService.createVnPayPayment(request));
        } catch (Exception e) {
            log.error("Error processing payment request", e);
            return new ResponseObject<>(HttpStatus.INTERNAL_SERVER_ERROR, "Error processing payment", null);
        }
    }

    @GetMapping("/vn-pay-callback")
    public ResponseObject<PaymentDTO.VNPayResponse> payCallbackHandler(HttpServletRequest request) {
        String status = request.getParameter("vnp_ResponseCode");
        if (status.equals("00")) {
            return new ResponseObject<>(HttpStatus.OK, "Success", PaymentDTO.VNPayResponse.builder()
                    .code("00")
                    .message("Success")
                    .paymentUrl("")
                    .build());
        } else {
            return new ResponseObject<>(HttpStatus.BAD_REQUEST, "Failed", null);
        }
    }
}
