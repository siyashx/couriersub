package com.codesupreme.couriersub.user.controller;

import com.codesupreme.couriersub.common.ApiResponse;
import com.codesupreme.couriersub.user.dto.SendOtpRequest;
import com.codesupreme.couriersub.user.dto.SubmitDocsRequest;
import com.codesupreme.couriersub.user.dto.VerifyOtpRequest;
import com.codesupreme.couriersub.user.service.VerificationService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "https://mototaksi.az")
@RestController
@RequestMapping("/api/verify")
public class VerificationController {

    private final VerificationService verification;

    public VerificationController(VerificationService verification) {
        this.verification = verification;
    }

    @PostMapping("/send-otp")
    public ApiResponse<Void> sendOtp(@Valid @RequestBody SendOtpRequest req) {
        verification.sendOtp(req.getPhone());
        return ApiResponse.ok("OTP göndərildi", null);
    }

    @PostMapping("/confirm-otp")
    public ApiResponse<Void> confirmOtp(@Valid @RequestBody VerifyOtpRequest req) {
        verification.confirmOtp(req.getPhone(), req.getOtp());
        return ApiResponse.ok("Telefon doğrulandı", null);
    }

    @PostMapping("/submit-docs")
    public ApiResponse<Void> submitDocs(@Valid @RequestBody SubmitDocsRequest req) {
        verification.submitDocs(req.getPhone(),
                req.getProfileImageUrl(),
                req.getIdFrontUrl(),
                req.getIdBackUrl());
        return ApiResponse.ok("Sənədlər göndərildi", null);
    }
}
