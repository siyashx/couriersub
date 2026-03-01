package com.codesupreme.couriersub.jobs;

import com.codesupreme.couriersub.common.enums.SubscriptionStatus;
import com.codesupreme.couriersub.subscription.entity.Subscription;
import com.codesupreme.couriersub.subscription.repo.SubscriptionRepository;
import com.codesupreme.couriersub.whatsapp.group.WhatsAppGroupService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
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
    @Scheduled(cron = "0 */30 * * * *", zone = "Asia/Baku")
    public void markPastDueAndRemove() {
        ZoneId BAKU = ZoneId.of("Asia/Baku");

        LocalDateTime nowBaku = ZonedDateTime.now(BAKU).toLocalDateTime();

        List<Subscription> expired =
                subs.findByStatusAndCurrentPeriodEndBefore(SubscriptionStatus.ACTIVE, nowBaku);

        for (Subscription s : expired) {
            s.setStatus(SubscriptionStatus.PAST_DUE);
            subs.save(s);
            groupService.removeFromGroup(s.getUser().getPhone());
        }
    }
}
