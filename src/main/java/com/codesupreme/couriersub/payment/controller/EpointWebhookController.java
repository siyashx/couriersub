package com.codesupreme.couriersub.payment.controller;

import com.codesupreme.couriersub.common.ApiResponse;
import com.codesupreme.couriersub.payment.dto.EpointWebhookRequest;
import com.codesupreme.couriersub.payment.service.PaymentService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments/epoint")
public class EpointWebhookController {

    private final PaymentService paymentService;

    public EpointWebhookController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping("/webhook")
    public ApiResponse<Void> webhook(@RequestBody EpointWebhookRequest req) {
        if (req == null || req.providerOrderId == null || req.status == null) {
            throw new IllegalArgumentException("Webhook body yanlışdır");
        }

        paymentService.handleWebhook(req.providerOrderId, req.transactionId, req.status);
        return ApiResponse.ok("WEBHOOK OK", null);
    }
}
