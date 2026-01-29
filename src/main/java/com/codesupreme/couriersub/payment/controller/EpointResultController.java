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

    @PostMapping(value = "/result", consumes = "application/json")
    public ApiResponse<Void> resultJson(@RequestBody EpointResultCallback cb) {
        if (cb == null || cb.data == null || cb.signature == null) {
            throw new IllegalArgumentException("Result body yanlışdır");
        }
        String d = cb.data.replace(" ", "+");
        String s = cb.signature.replace(" ", "+");
        paymentService.handleEpointResult(d, s);
        return ApiResponse.ok("RESULT OK", null);
    }

    @PostMapping(value = "/result", consumes = "application/x-www-form-urlencoded")
    public ApiResponse<Void> resultForm(
            @RequestParam("data") String data,
            @RequestParam("signature") String signature
    ) {
        String d = data.replace(" ", "+");
        String s = signature.replace(" ", "+");
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
