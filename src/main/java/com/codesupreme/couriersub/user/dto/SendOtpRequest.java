package com.codesupreme.couriersub.user.dto;

import jakarta.validation.constraints.NotBlank;

public class SendOtpRequest {
    @NotBlank
    private String phone;

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
}
