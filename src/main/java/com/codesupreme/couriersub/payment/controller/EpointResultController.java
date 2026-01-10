package com.codesupreme.couriersub.payment.controller;

import com.codesupreme.couriersub.common.ApiResponse;
import com.codesupreme.couriersub.payment.dto.EpointResultCallback;
import com.codesupreme.couriersub.payment.service.PaymentService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@CrossOrigin(origins = "https://mototaksi.az")
@RestController
@RequestMapping("/api/payments/epoint")
public class EpointResultController {

    private final PaymentService paymentService;

    public EpointResultController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping("/result")
    public ApiResponse<Void> result(@RequestBody EpointResultCallback cb) {
        if (cb == null || cb.data == null || cb.signature == null) {
            throw new IllegalArgumentException("Result body yanlışdır");
        }
        paymentService.handleEpointResult(cb.data, cb.signature);
        return ApiResponse.ok("RESULT OK", null);
    }

    // ✅ user bura yönlənəcək (GET)
    @GetMapping("/success")
    public ResponseEntity<Void> success(@RequestParam(required = false) String order_id) {
        // couriersub UI route-a göndər
        String to = "https://mototaksi.az/couriersub/success"
                + (order_id != null ? "?orderId=" + order_id : "");
        return ResponseEntity.status(302).header(HttpHeaders.LOCATION, to).build();
    }

    // ✅ user bura yönlənəcək (GET)
    @GetMapping("/error")
    public ResponseEntity<Void> error(@RequestParam(required = false) String order_id) {
        String to = "https://mototaksi.az/couriersub/error"
                + (order_id != null ? "?orderId=" + order_id : "");
        return ResponseEntity.status(302).header(HttpHeaders.LOCATION, to).build();
    }
}
