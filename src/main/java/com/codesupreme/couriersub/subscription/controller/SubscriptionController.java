package com.codesupreme.couriersub.subscription.controller;

import com.codesupreme.couriersub.common.ApiResponse;
import com.codesupreme.couriersub.common.util.PhoneUtil;
import com.codesupreme.couriersub.subscription.dto.SubscriptionResponse;
import com.codesupreme.couriersub.subscription.entity.Subscription;
import com.codesupreme.couriersub.subscription.service.SubscriptionService;
import com.codesupreme.couriersub.user.entity.User;
import com.codesupreme.couriersub.user.repo.UserRepository;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/me")
public class SubscriptionController {

    private final UserRepository users;
    private final SubscriptionService subscriptionService;

    public SubscriptionController(UserRepository users, SubscriptionService subscriptionService) {
        this.users = users;
        this.subscriptionService = subscriptionService;
    }

    // Sadə: phone query ilə (JWT yoxdur deyə)
    @GetMapping("/subscription")
    public ApiResponse<SubscriptionResponse> mySubscription(@RequestParam String phone) {
        String p = PhoneUtil.normalize(phone);
        User u = users.findByPhone(p).orElseThrow(() -> new IllegalArgumentException("User tapılmadı"));

        Subscription s = subscriptionService.getOrCreate(u);

        SubscriptionResponse r = new SubscriptionResponse();
        r.status = s.getStatus();
        r.plan = s.getPlan();
        r.currentPeriodStart = s.getCurrentPeriodStart();
        r.currentPeriodEnd = s.getCurrentPeriodEnd();

        return ApiResponse.ok("OK", r);
    }
}
