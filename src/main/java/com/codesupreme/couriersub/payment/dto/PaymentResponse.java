package com.codesupreme.couriersub.payment.dto;

import com.codesupreme.couriersub.common.enums.PaymentStatus;

import java.time.LocalDateTime;

public class PaymentResponse {
    public Long id;
    public int amountInt;
    public String currency;
    public PaymentStatus status;
    public String providerOrderId;
    public String transactionId;
    public LocalDateTime createdAt;
    public LocalDateTime paidAt;
}
