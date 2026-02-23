package com.codesupreme.couriersub.admin.service;

import com.codesupreme.couriersub.admin.dto.AdjustBalanceRequest;
import com.codesupreme.couriersub.admin.dto.AdminDecisionRequest;
import com.codesupreme.couriersub.admin.dto.AdminUserDetailResponse;
import com.codesupreme.couriersub.admin.dto.AdminUserListItem;
import com.codesupreme.couriersub.common.enums.VerifyStatus;
import com.codesupreme.couriersub.referral.entity.ReferralWallet;
import com.codesupreme.couriersub.referral.repo.ReferralRewardRepository;
import com.codesupreme.couriersub.referral.repo.ReferralWalletRepository;
import com.codesupreme.couriersub.user.entity.User;
import com.codesupreme.couriersub.user.entity.UserVerification;
import com.codesupreme.couriersub.user.repo.UserRepository;
import com.codesupreme.couriersub.user.repo.UserVerificationRepository;
import com.codesupreme.couriersub.whatsapp.group.WhatsAppGroupService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdminService {

    private final UserRepository users;
    private final UserVerificationRepository verifications;

    private final WhatsAppGroupService groupService;

    // ✅ əlavə et
    private final ReferralWalletRepository referralWallets;
    private final ReferralRewardRepository referralRewards;

    public AdminService(UserRepository users, UserVerificationRepository verifications, WhatsAppGroupService groupService, ReferralWalletRepository referralWallets, ReferralRewardRepository referralRewards) {
        this.users = users;
        this.verifications = verifications;
        this.groupService = groupService;
        this.referralWallets = referralWallets;
        this.referralRewards = referralRewards;
    }


    public List<AdminUserListItem> listUsers(String status) {
        VerifyStatus st;
        if (status != null && !status.isBlank()) {
            st = VerifyStatus.valueOf(status.trim());
        } else {
            st = null;
        }

        List<User> list = (st == null) ? users.findAll() : users.findByVerifyStatus(st);

        return list.stream().map(u -> {
            AdminUserListItem item = new AdminUserListItem();
            item.id = u.getId();
            item.firstName = u.getFirstName();
            item.lastName = u.getLastName();
            item.phone = u.getPhone();
            item.active = u.isActive();
            item.verifyStatus = u.getVerifyStatus();
            item.createdAt = u.getCreatedAt();
            return item;
        }).toList();
    }

    public AdminUserDetailResponse getUserDetail(Long userId) {
        User u = users.findById(userId).orElseThrow(() -> new IllegalArgumentException("User tapılmadı"));

        UserVerification uv = verifications.findByUser(u).orElse(null);

        AdminUserDetailResponse d = new AdminUserDetailResponse();
        d.id = u.getId();
        d.firstName = u.getFirstName();
        d.lastName = u.getLastName();
        d.phone = u.getPhone();
        d.active = u.isActive();
        d.password = u.getPassword();
        d.verifyStatus = u.getVerifyStatus();
        d.createdAt = u.getCreatedAt();

        if (uv != null) {
            d.profileImageUrl = uv.getProfileImageUrl();
            d.idFrontUrl = uv.getIdFrontUrl();
            d.idBackUrl = uv.getIdBackUrl();
            d.phoneVerifiedAt = uv.getPhoneVerifiedAt();
            d.docsSubmittedAt = uv.getDocsSubmittedAt();
            d.adminNote = uv.getAdminNote();
        }

        // ✅ REFERRAL: balans + dəvət sayı
        d.balanceInt = referralWallets.findByUser(u).map(ReferralWallet::getBalanceInt).orElse(0);
        d.invitedCount = referralRewards.countByInviter(u);

        return d;
    }

    public void approve(Long userId, AdminDecisionRequest req) {
        User u = users.findById(userId).orElseThrow(() -> new IllegalArgumentException("User tapılmadı"));
        UserVerification uv = verifications.findByUser(u)
                .orElseThrow(() -> new IllegalArgumentException("Verification tapılmadı"));

        // minimum şərt: docs göndərilmiş olsun
        if (u.getVerifyStatus() != VerifyStatus.DOCS_SUBMITTED) {
            throw new IllegalArgumentException("Sənədlər göndərilməyib");
        }

        u.setVerifyStatus(VerifyStatus.APPROVED);
        users.save(u);

        if (req != null && req.note != null) {
            uv.setAdminNote(req.note);
            verifications.save(uv);
        }
    }

    public void reject(Long userId, AdminDecisionRequest req) {
        User u = users.findById(userId).orElseThrow(() -> new IllegalArgumentException("User tapılmadı"));
        UserVerification uv = verifications.findByUser(u)
                .orElseThrow(() -> new IllegalArgumentException("Verification tapılmadı"));

        u.setVerifyStatus(VerifyStatus.REJECTED);
        users.save(u);

        String note = (req == null || req.note == null || req.note.isBlank())
                ? "Rədd edildi"
                : req.note.trim();

        uv.setAdminNote(note);
        verifications.save(uv);
    }

    public void setActive(Long userId, boolean active) {
        User u = users.findById(userId).orElseThrow(() -> new IllegalArgumentException("User tapılmadı"));
        u.setActive(active);
        users.save(u);
    }

    public void forceAddToGroup(Long userId) {
        User u = users.findById(userId).orElseThrow(() -> new IllegalArgumentException("User tapılmadı"));
        groupService.addToGroup(u.getPhone());
    }

    public void forceRemoveFromGroup(Long userId) {
        User u = users.findById(userId).orElseThrow(() -> new IllegalArgumentException("User tapılmadı"));
        groupService.removeFromGroup(u.getPhone());
    }

    public void adjustBalance(Long userId, AdjustBalanceRequest req) {
        if (req == null || req.amountInt == null) {
            throw new IllegalArgumentException("Məbləğ göndərilməlidir");
        }

        User u = users.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User tapılmadı"));

        ReferralWallet wallet = referralWallets.findByUser(u)
                .orElseGet(() -> {
                    ReferralWallet w = new ReferralWallet();
                    w.setUser(u);
                    w.setBalanceInt(0);
                    return referralWallets.save(w);
                });

        int newBalance = wallet.getBalanceInt() + req.amountInt;

        if (newBalance < 0) {
            throw new IllegalArgumentException("Balans mənfi ola bilməz");
        }

        wallet.setBalanceInt(newBalance);
        referralWallets.save(wallet);
    }


}
