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
        if (users.existsByPhone(phone)) {
            throw new IllegalArgumentException("Bu telefon artıq qeydiyyatdan keçib");
        }

        User u = new User();
        u.setFirstName(req.getFirstName().trim());
        u.setLastName(req.getLastName().trim());
        u.setPhone(phone);
        u.setPassword(req.getPassword()); // plain text
        u.setRole(UserRole.COURIER);

        return users.save(u);
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
