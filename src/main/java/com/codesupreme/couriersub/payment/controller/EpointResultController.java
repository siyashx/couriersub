package com.codesupreme.couriersub.payment.controller;

import com.codesupreme.couriersub.common.ApiResponse;
import com.codesupreme.couriersub.payment.dto.EpointResultCallback;
import com.codesupreme.couriersub.payment.service.PaymentService;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "https://mototaksi.az")
@RestController
@RequestMapping("/api/payments/epoint")
public class EpointResultController {

    private final PaymentService paymentService;

    public EpointResultController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    // ePoint server-to-server nəticə göndərir
    @PostMapping("/result")
    public ApiResponse<Void> result(@RequestBody EpointResultCallback cb) {
        if (cb == null || cb.data == null || cb.signature == null) {
            throw new IllegalArgumentException("Result body yanlışdır");
        }
        paymentService.handleEpointResult(cb.data, cb.signature);
        return ApiResponse.ok("RESULT OK", null);
    }
}
