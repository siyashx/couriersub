package com.codesupreme.couriersub.referral.entity;

import com.codesupreme.couriersub.user.entity.User;
import jakarta.persistence.*;

@Entity
@Table(name = "referral_wallets", indexes = {
        @Index(name="idx_wallet_user", columnList="user_id", unique = true)
})
public class ReferralWallet {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(optional = false)
    @JoinColumn(name="user_id", nullable = false, unique = true)
    private User user;

    @Column(nullable = false)
    private int balanceInt = 0; // qəpiklə: 100 = 1 AZN

    public Long getId() { return id; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public int getBalanceInt() { return balanceInt; }
    public void setBalanceInt(int balanceInt) { this.balanceInt = balanceInt; }
}
