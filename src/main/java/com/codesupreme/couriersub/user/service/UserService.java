package com.codesupreme.couriersub.user.service;

import com.codesupreme.couriersub.common.enums.UserRole;
import com.codesupreme.couriersub.common.util.PhoneUtil;
import com.codesupreme.couriersub.user.dto.LoginRequest;
import com.codesupreme.couriersub.user.dto.RegisterRequest;
import com.codesupreme.couriersub.user.entity.User;
import com.codesupreme.couriersub.user.repo.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository users;

    public UserService(UserRepository users) {
        this.users = users;
    }

    public User register(RegisterRequest req) {
        String phone = PhoneUtil.normalize(req.getPhone());
        if (users.existsByPhone(phone)) throw new IllegalArgumentException("Bu telefon artıq qeydiyyatdan keçib");

        User u = new User();
        u.setFirstName(req.getFirstName().trim());
        u.setLastName(req.getLastName().trim());
        u.setPhone(phone);
        u.setPassword(req.getPassword());
        u.setRole(UserRole.COURIER);

        // ✅ referredBy set et
        if (req.getReferralCode() != null && !req.getReferralCode().isBlank()) {
            users.findByReferralCode(req.getReferralCode().trim().toUpperCase())
                    .ifPresent(u::setReferredBy);
        }

        // save first
        u = users.save(u);

        // ✅ referralCode (öz dəvət kodu) ver
        // bunu burada da edə bilərsən, ya ReferralService içində
        // sadə: burada manual:
        if (u.getReferralCode() == null) {
            // unique code tap
            String code;
            do { code = com.codesupreme.couriersub.common.util.ReferralUtil.generateCode(8); }
            while (users.findByReferralCode(code).isPresent());
            u.setReferralCode(code);
            u = users.save(u);
        }

        return u;
    }

    public User login(LoginRequest req) {
        String phone = PhoneUtil.normalize(req.getPhone());
        User u = users.findByPhone(phone)
                .orElseThrow(() -> new IllegalArgumentException("Telefon və ya şifrə yanlışdır"));

        if (!u.isActive()) {
            throw new IllegalArgumentException("Hesab deaktiv edilib");
        }

        if (!u.getPassword().equals(req.getPassword())) {
            throw new IllegalArgumentException("Telefon və ya şifrə yanlışdır");
        }

        return u;
    }

    public User getByPhone(String phone) {
        return users.findByPhone(phone.trim())
                .orElseThrow(() -> new IllegalArgumentException("İstifadəçi tapılmadı"));
    }
}
