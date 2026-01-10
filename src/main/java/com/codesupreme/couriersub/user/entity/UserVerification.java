package com.codesupreme.couriersub.user.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_verifications", indexes = {
        @Index(name = "idx_uv_user", columnList = "user_id", unique = true)
})
public class UserVerification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(optional = false)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(length = 10)
    private String otpCode;

    private LocalDateTime otpExpiresAt;

    private LocalDateTime phoneVerifiedAt;

    @Column(length = 500)
    private String profileImageUrl;

    @Column(length = 500)
    private String idFrontUrl;

    @Column(length = 500)
    private String idBackUrl;

    private LocalDateTime docsSubmittedAt;

    @Column(columnDefinition = "TEXT")
    private String adminNote;

    private LocalDateTime updatedAt = LocalDateTime.now();

    @PreUpdate
    public void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // getters/setters
    public Long getId() { return id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public String getOtpCode() { return otpCode; }
    public void setOtpCode(String otpCode) { this.otpCode = otpCode; }

    public LocalDateTime getOtpExpiresAt() { return otpExpiresAt; }
    public void setOtpExpiresAt(LocalDateTime otpExpiresAt) { this.otpExpiresAt = otpExpiresAt; }

    public LocalDateTime getPhoneVerifiedAt() { return phoneVerifiedAt; }
    public void setPhoneVerifiedAt(LocalDateTime phoneVerifiedAt) { this.phoneVerifiedAt = phoneVerifiedAt; }

    public String getProfileImageUrl() { return profileImageUrl; }
    public void setProfileImageUrl(String profileImageUrl) { this.profileImageUrl = profileImageUrl; }

    public String getIdFrontUrl() { return idFrontUrl; }
    public void setIdFrontUrl(String idFrontUrl) { this.idFrontUrl = idFrontUrl; }

    public String getIdBackUrl() { return idBackUrl; }
    public void setIdBackUrl(String idBackUrl) { this.idBackUrl = idBackUrl; }

    public LocalDateTime getDocsSubmittedAt() { return docsSubmittedAt; }
    public void setDocsSubmittedAt(LocalDateTime docsSubmittedAt) { this.docsSubmittedAt = docsSubmittedAt; }

    public String getAdminNote() { return adminNote; }
    public void setAdminNote(String adminNote) { this.adminNote = adminNote; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
