package com.codesupreme.couriersub.subscription.repo;

import com.codesupreme.couriersub.common.enums.SubscriptionStatus;
import com.codesupreme.couriersub.subscription.entity.Subscription;
import com.codesupreme.couriersub.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {
    Optional<Subscription> findByUser(User user);
    List<Subscription> findByStatusAndCurrentPeriodEndBefore(SubscriptionStatus status, LocalDateTime time);

}

