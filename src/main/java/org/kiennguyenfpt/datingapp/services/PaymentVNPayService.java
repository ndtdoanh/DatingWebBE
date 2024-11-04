package org.kiennguyenfpt.datingapp.services;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.kiennguyenfpt.datingapp.configs.VNPAYConfig;
import org.kiennguyenfpt.datingapp.dtos.PaymentDTO;
import org.kiennguyenfpt.datingapp.utils.VNPayUtil;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class PaymentVNPayService {

    private final VNPAYConfig vnPayConfig;

    public PaymentDTO.VNPayResponse createVnPayPayment(HttpServletRequest request) {
        String amountParam = request.getParameter("amount");
        if (amountParam == null || amountParam.isEmpty()) {
            throw new IllegalArgumentException("Amount parameter is missing or empty");
        }

        long amount;
        try {
            amount = Integer.parseInt(amountParam) * 100L;
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid amount format", e);
        }

        String bankCode = request.getParameter("bankCode");
        Map<String, String> vnpParamsMap = vnPayConfig.getVNPayConfig();
        vnpParamsMap.put("vnp_Amount", String.valueOf(amount));
        if (bankCode != null && !bankCode.isEmpty()) {
            vnpParamsMap.put("vnp_BankCode", bankCode);
        }
        vnpParamsMap.put("vnp_IpAddr", VNPayUtil.getIpAddress(request));
        //build query url
        String queryUrl = VNPayUtil.getPaymentURL(vnpParamsMap, true);
        String hashData = VNPayUtil.getPaymentURL(vnpParamsMap, false);
        String vnpSecureHash = VNPayUtil.hmacSHA512(vnPayConfig.getSecretKey(), hashData);
        queryUrl += "&vnp_SecureHash=" + vnpSecureHash;
        String paymentUrl = vnPayConfig.getVnp_PayUrl() + "?" + queryUrl;
        return PaymentDTO.VNPayResponse.builder()
                .code("ok")
                .message("success")
                .paymentUrl(paymentUrl).build();
    }
    
}
