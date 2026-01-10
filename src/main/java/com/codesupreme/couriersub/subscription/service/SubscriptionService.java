package com.codesupreme.couriersub.subscription.service;

import com.codesupreme.couriersub.common.enums.SubscriptionStatus;
import com.codesupreme.couriersub.subscription.entity.Subscription;
import com.codesupreme.couriersub.subscription.repo.SubscriptionRepository;
import com.codesupreme.couriersub.user.entity.User;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class SubscriptionService {

    private final SubscriptionRepository subs;

    public SubscriptionService(SubscriptionRepository subs) {
        this.subs = subs;
    }

    public Subscription getOrCreate(User user) {
        return subs.findByUser(user).orElseGet(() -> {
            Subscription s = new Subscription();
            s.setUser(user);
            s.setStatus(SubscriptionStatus.INACTIVE);
            s.setPlan("MONTHLY_5");
            return subs.save(s);
        });
    }

    public Subscription activateMonthly(User user) {
        Subscription s = getOrCreate(user);
        LocalDateTime now = LocalDateTime.now();

        s.setStatus(SubscriptionStatus.ACTIVE);
        s.setCurrentPeriodStart(now);
        s.setCurrentPeriodEnd(now.plusDays(30)); // istəsən 1 ay məntiqi də edə bilərik
        return subs.save(s);
    }
}
