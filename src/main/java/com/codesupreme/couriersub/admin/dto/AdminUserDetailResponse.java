package com.codesupreme.couriersub.admin.dto;

import com.codesupreme.couriersub.common.enums.VerifyStatus;

import java.time.LocalDateTime;

public class AdminUserDetailResponse {
    public Long id;
    public String firstName;
    public String lastName;
    public String phone;
    public boolean active;
    public VerifyStatus verifyStatus;
    public LocalDateTime createdAt;
    public String password;

    // docs
    public String profileImageUrl;
    public String idFrontUrl;
    public String idBackUrl;
    public LocalDateTime phoneVerifiedAt;
    public LocalDateTime docsSubmittedAt;

    public String adminNote;

    // ✅ REFERRAL
    public Integer invitedCount; // neçə nəfər dəvət edib
    public Integer balanceInt;   // qəpiklə (100 = 1.00 AZN)
}
