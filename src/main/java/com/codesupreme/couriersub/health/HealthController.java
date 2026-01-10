package com.codesupreme.couriersub.health;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin(origins = "https://mototaksi.az")
@RestController
public class HealthController {
    @GetMapping("/api/health")
    public String health() {
        return "OK";
    }
}
