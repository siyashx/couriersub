package com.codesupreme.couriersub.referral.service;

import com.codesupreme.couriersub.common.util.ReferralUtil;
import com.codesupreme.couriersub.referral.entity.ReferralReward;
import com.codesupreme.couriersub.referral.entity.ReferralWallet;
import com.codesupreme.couriersub.referral.repo.ReferralRewardRepository;
import com.codesupreme.couriersub.referral.repo.ReferralWalletRepository;
import com.codesupreme.couriersub.user.entity.User;
import com.codesupreme.couriersub.user.repo.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class ReferralService {

    private final UserRepository users;
    private final ReferralWalletRepository wallets;
    private final ReferralRewardRepository rewards;

    public ReferralService(UserRepository users, ReferralWalletRepository wallets, ReferralRewardRepository rewards) {
        this.users = users;
        this.wallets = wallets;
        this.rewards = rewards;
    }

    public String ensureReferralCode(User u) {
        if (u.getReferralCode() != null && !u.getReferralCode().isBlank()) return u.getReferralCode();

        // unique code tapana qədər
        String code;
        do { code = ReferralUtil.generateCode(8); }
        while (users.findByReferralCode(code).isPresent());

        u.setReferralCode(code);
        users.save(u);
        return code;
    }

    public ReferralWallet getOrCreateWallet(User u) {
        return wallets.findByUser(u).orElseGet(() -> {
            ReferralWallet w = new ReferralWallet();
            w.setUser(u);
            w.setBalanceInt(0);
            return wallets.save(w);
        });
    }

    public long invitedCount(User inviter) {
        return users.countByReferredBy(inviter);
    }

    // ✅ ödəmə SUCCESS olanda çağıracağıq (idempotent)
    public void rewardIfEligible(User invitee) {
        User inviter = invitee.getReferredBy();
        if (inviter == null) return;

        // 1 dəfə
        if (rewards.findByInvitee(invitee).isPresent()) return;

        // reward yaz
        ReferralReward rr = new ReferralReward();
        rr.setInviter(inviter);
        rr.setInvitee(invitee);
        rr.setAmountInt(100); // 1 AZN
        rewards.save(rr);

        // balans artır
        ReferralWallet w = getOrCreateWallet(inviter);
        w.setBalanceInt(w.getBalanceInt() + 100);
        wallets.save(w);
    }
}
