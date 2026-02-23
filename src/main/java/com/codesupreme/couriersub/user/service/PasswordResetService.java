package com.codesupreme.couriersub.user.service;

import com.codesupreme.couriersub.common.enums.VerifyStatus;
import com.codesupreme.couriersub.common.util.PhoneUtil;
import com.codesupreme.couriersub.user.entity.User;
import com.codesupreme.couriersub.user.entity.UserVerification;
import com.codesupreme.couriersub.user.repo.UserRepository;
import com.codesupreme.couriersub.user.repo.UserVerificationRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class PasswordResetService {

    private final UserRepository users;
    private final UserVerificationRepository verifications;

    // OTP təsdiqindən sonra nə qədər müddət icazə verək (dəqiqə)
    private static final int WINDOW_MINUTES = 10;

    public PasswordResetService(UserRepository users, UserVerificationRepository verifications) {
        this.users = users;
        this.verifications = verifications;
    }

    public void resetPassword(String phone, String newPassword) {
        String p = PhoneUtil.normalize(phone);

        User u = users.findByPhone(p)
                .orElseThrow(() -> new IllegalArgumentException("İstifadəçi tapılmadı"));

        UserVerification uv = verifications.findByUser(u)
                .orElseThrow(() -> new IllegalArgumentException("OTP təsdiqlənməyib"));

        LocalDateTime verifiedAt = uv.getPhoneVerifiedAt();
        if (verifiedAt == null) {
            throw new IllegalArgumentException("OTP təsdiqlənməyib");
        }

        // ✅ OTP təsdiqi son 10 dəqiqə içində olmalıdır
        if (LocalDateTime.now().isAfter(verifiedAt.plusMinutes(WINDOW_MINUTES))) {
            throw new IllegalArgumentException("OTP təsdiqi vaxtı bitib (yenidən OTP alın)");
        }

        // minimal qayda
        if (newPassword == null || newPassword.trim().length() < 6) {
            throw new IllegalArgumentException("Şifrə minimum 6 simvol olmalıdır");
        }

        u.setPassword(newPassword.trim());
        // statusu sənin biznes məntiqinə görə toxunmaya bilərik, amma istəsən:
        // u.setVerifyStatus(VerifyStatus.PHONE_VERIFIED);

        users.save(u);

        // ✅ Təhlükəsizlik: eyni təsdiqlə ikinci dəfə reset olmasın deyə "consume" et
        uv.setPhoneVerifiedAt(null);
        verifications.save(uv);
    }
}
