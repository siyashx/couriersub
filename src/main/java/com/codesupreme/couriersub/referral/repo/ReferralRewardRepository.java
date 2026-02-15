package com.codesupreme.couriersub.referral.repo;

import com.codesupreme.couriersub.referral.entity.ReferralReward;
import com.codesupreme.couriersub.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ReferralRewardRepository extends JpaRepository<ReferralReward, Long> {
    Optional<ReferralReward> findByInvitee(User invitee);
    int countByReferrer(User referrer);
}
