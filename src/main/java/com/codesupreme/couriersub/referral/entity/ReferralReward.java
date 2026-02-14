package com.codesupreme.couriersub.referral.entity;

import com.codesupreme.couriersub.user.entity.User;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name="referral_rewards", indexes = {
        @Index(name="idx_reward_invitee", columnList="invitee_user_id", unique = true),
        @Index(name="idx_reward_inviter", columnList="inviter_user_id,createdAt")
})
public class ReferralReward {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name="inviter_user_id", nullable = false)
    private User inviter;

    @OneToOne(optional = false)
    @JoinColumn(name="invitee_user_id", nullable = false, unique = true)
    private User invitee;

    @Column(nullable = false)
    private int amountInt = 100; // 1 AZN

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public Long getId() { return id; }
    public User getInviter() { return inviter; }
    public void setInviter(User inviter) { this.inviter = inviter; }
    public User getInvitee() { return invitee; }
    public void setInvitee(User invitee) { this.invitee = invitee; }
    public int getAmountInt() { return amountInt; }
    public void setAmountInt(int amountInt) { this.amountInt = amountInt; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
