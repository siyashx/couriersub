package com.codesupreme.couriersub.referral.repo;

import com.codesupreme.couriersub.referral.entity.ReferralReward;
import com.codesupreme.couriersub.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ReferralRewardRepository extends JpaRepository<ReferralReward, Long> {

    // invitee bir dəfə reward ala bilər (unique)
    Optional<ReferralReward> findByInvitee(User invitee);

    // inviter neçə nəfər dəvət edib (reward sayı)
    int countByInviter(User inviter);

    // (istəsən) balans üçün cəmi
    int sumAmountIntByInviter(User inviter);
}
