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

        // Növbəti ayın 1-i, saat 23:59
        LocalDateTime nextMonthFirstDayEnd = now.toLocalDate()
                .withDayOfMonth(1)
                .plusMonths(1)
                .atTime(23, 59, 0);

        s.setStatus(SubscriptionStatus.ACTIVE);

        // Ödəniş edildiyi an
        s.setCurrentPeriodStart(now);

        // Güzəştli son vaxt: gələn ayın 1-i 23:59
        s.setCurrentPeriodEnd(nextMonthFirstDayEnd);

        return subs.save(s);
    }
}
