package com.codesupreme.couriersub.user.service;

import com.codesupreme.couriersub.common.enums.VerifyStatus;
import com.codesupreme.couriersub.common.util.OtpUtil;
import com.codesupreme.couriersub.common.util.PhoneUtil;
import com.codesupreme.couriersub.user.entity.User;
import com.codesupreme.couriersub.user.entity.UserVerification;
import com.codesupreme.couriersub.user.repo.UserRepository;
import com.codesupreme.couriersub.user.repo.UserVerificationRepository;
import com.codesupreme.couriersub.whatsapp.evolution.EvolutionService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class VerificationService {

    private final UserRepository users;
    private final UserVerificationRepository verifications;
    private final EvolutionService evolution;

    public VerificationService(UserRepository users,
                               UserVerificationRepository verifications,
                               EvolutionService evolution) {
        this.users = users;
        this.verifications = verifications;
        this.evolution = evolution;
    }

    private User getUserByPhoneOrThrow(String phone) {
        String p = PhoneUtil.normalize(phone);
        return users.findByPhone(p)
                .orElseThrow(() -> new IllegalArgumentException("İstifadəçi tapılmadı"));
    }

    private UserVerification getOrCreate(User u) {
        return verifications.findByUser(u).orElseGet(() -> {
            UserVerification uv = new UserVerification();
            uv.setUser(u);
            return verifications.save(uv);
        });
    }

    public void sendOtp(String phone) {
        User u = getUserByPhoneOrThrow(phone);

        String otp = OtpUtil.generate6();
        LocalDateTime expires = LocalDateTime.now().plusMinutes(5);

        UserVerification uv = getOrCreate(u);
        uv.setOtpCode(otp);
        uv.setOtpExpiresAt(expires);
        verifications.save(uv);

        // status
        u.setVerifyStatus(VerifyStatus.PHONE_PENDING);
        users.save(u);

        // WhatsApp-a göndər
        evolution.sendOtp(u.getPhone(), otp);
    }

    public void confirmOtp(String phone, String otp) {
        User u = getUserByPhoneOrThrow(phone);
        UserVerification uv = verifications.findByUser(u)
                .orElseThrow(() -> new IllegalArgumentException("OTP göndərilməyib"));

        if (uv.getOtpCode() == null || uv.getOtpExpiresAt() == null) {
            throw new IllegalArgumentException("OTP göndərilməyib");
        }

        if (LocalDateTime.now().isAfter(uv.getOtpExpiresAt())) {
            throw new IllegalArgumentException("OTP vaxtı bitib (yenidən göndərin)");
        }

        if (!uv.getOtpCode().equals(otp.trim())) {
            throw new IllegalArgumentException("OTP yanlışdır");
        }

        uv.setPhoneVerifiedAt(LocalDateTime.now());
        // OTP-ni sıfırlayaq ki, təkrar istifadə olunmasın
        uv.setOtpCode(null);
        uv.setOtpExpiresAt(null);
        verifications.save(uv);

        u.setVerifyStatus(VerifyStatus.PHONE_VERIFIED);
        users.save(u);
    }

    public void submitDocs(String phone, String profileUrl, String frontUrl, String backUrl) {
        User u = getUserByPhoneOrThrow(phone);

        if (u.getVerifyStatus() != VerifyStatus.PHONE_VERIFIED
                && u.getVerifyStatus() != VerifyStatus.DOCS_PENDING
                && u.getVerifyStatus() != VerifyStatus.DOCS_SUBMITTED) {
            throw new IllegalArgumentException("Əvvəl telefon doğrulanmalıdır");
        }

        UserVerification uv = getOrCreate(u);
        uv.setProfileImageUrl(profileUrl);
        uv.setIdFrontUrl(frontUrl);
        uv.setIdBackUrl(backUrl);
        uv.setDocsSubmittedAt(LocalDateTime.now());
        verifications.save(uv);

        u.setVerifyStatus(VerifyStatus.DOCS_SUBMITTED);
        users.save(u);
    }
}
