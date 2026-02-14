package com.codesupreme.couriersub.referral.repo;

import com.codesupreme.couriersub.referral.entity.ReferralWallet;
import com.codesupreme.couriersub.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ReferralWalletRepository extends JpaRepository<ReferralWallet, Long> {
    Optional<ReferralWallet> findByUser(User user);
}
