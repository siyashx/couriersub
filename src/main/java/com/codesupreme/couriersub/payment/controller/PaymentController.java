package com.codesupreme.couriersub.payment.controller;

import com.codesupreme.couriersub.common.ApiResponse;
import com.codesupreme.couriersub.payment.dto.InitPaymentRequest;
import com.codesupreme.couriersub.payment.dto.InitPaymentResponse;
import com.codesupreme.couriersub.payment.dto.PaymentResponse;
import com.codesupreme.couriersub.payment.service.PaymentService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping("/payments/epoint/init")
    public ApiResponse<InitPaymentResponse> init(@Valid @RequestBody InitPaymentRequest req) {
        return ApiResponse.ok("INIT OK", paymentService.initEpoint(req));
    }

    // user panel: phone query ilə
    @GetMapping("/me/payments")
    public ApiResponse<List<PaymentResponse>> myPayments(@RequestParam String phone) {
        return ApiResponse.ok("OK", paymentService.listMyPayments(phone));
    }
}
