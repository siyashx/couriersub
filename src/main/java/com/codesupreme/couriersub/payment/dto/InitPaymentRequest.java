package com.codesupreme.couriersub.payment.dto;

import jakarta.validation.constraints.NotBlank;

public class InitPaymentRequest {
    @NotBlank
    public String phone;

    @NotBlank
    public String successUrl;

    @NotBlank
    public String errorUrl;

    public String description; // opsional
}
