package com.codesupreme.couriersub.jobs;

import com.codesupreme.couriersub.common.enums.SubscriptionStatus;
import com.codesupreme.couriersub.subscription.entity.Subscription;
import com.codesupreme.couriersub.subscription.repo.SubscriptionRepository;
import com.codesupreme.couriersub.whatsapp.group.WhatsAppGroupService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class SubscriptionCheckJob {

    private final SubscriptionRepository subs;
    private final WhatsAppGroupService groupService;

    public SubscriptionCheckJob(SubscriptionRepository subs, WhatsAppGroupService groupService) {
        this.subs = subs;
        this.groupService = groupService;
    }

    // hər 30 dəqiqə
    @Scheduled(fixedDelay = 30 * 60 * 1000L)
    public void markPastDueAndRemove() {
        LocalDateTime now = LocalDateTime.now();

        List<Subscription> expired = subs.findByStatusAndCurrentPeriodEndBefore(SubscriptionStatus.ACTIVE, now);

        for (Subscription s : expired) {
            s.setStatus(SubscriptionStatus.PAST_DUE);
            subs.save(s);

            // qrupdan çıxar
            groupService.removeFromGroup(s.getUser().getPhone());
        }
    }
}
