package com.codesupreme.couriersub.payment.controller;

import com.codesupreme.couriersub.common.ApiResponse;
import com.codesupreme.couriersub.payment.dto.EpointResultCallback;
import com.codesupreme.couriersub.payment.service.PaymentService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;


@CrossOrigin(origins = "https://mototaksi.az")
@RestController
@RequestMapping("/api/payments/epoint")
public class EpointResultController {

    private final PaymentService paymentService;

    public EpointResultController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping(value = "/result", consumes = {"application/x-www-form-urlencoded", "application/json"})
    public ApiResponse<Void> result(
            @RequestParam(required = false) String data,
            @RequestParam(required = false) String signature,
            @RequestBody(required = false) EpointResultCallback cb
    ) {
        String d = (data != null) ? data : (cb != null ? cb.data : null);
        String s = (signature != null) ? signature : (cb != null ? cb.signature : null);

        if (d == null || s == null) throw new IllegalArgumentException("Result body yanlışdır (data/signature boşdur)");

        paymentService.handleEpointResult(d, s);
        return ApiResponse.ok("RESULT OK", null);
    }


    // ✅ user bura yönlənəcək (GET)
    @GetMapping("/success")
    public ResponseEntity<Void> success(@RequestParam(required = false) String order_id) {
        // couriersub UI route-a göndər
        String to = "https://mototaksi.az/couriersub/success"
                + (order_id != null ? "?orderId=" + URLEncoder.encode(order_id, StandardCharsets.UTF_8) : "");
        return ResponseEntity.status(302).header(HttpHeaders.LOCATION, to).build();
    }

    // ✅ user bura yönlənəcək (GET)
    @GetMapping("/error")
    public ResponseEntity<Void> error(@RequestParam(required = false) String order_id) {
        String to = "https://mototaksi.az/couriersub/error"
                + (order_id != null ? "?orderId=" + URLEncoder.encode(order_id, StandardCharsets.UTF_8) : "");
        return ResponseEntity.status(302).header(HttpHeaders.LOCATION, to).build();
    }
}
