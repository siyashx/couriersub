package com.codesupreme.couriersub.user.controller;

import com.codesupreme.couriersub.common.ApiResponse;
import com.codesupreme.couriersub.common.util.PhoneUtil;
import com.codesupreme.couriersub.user.dto.LoginRequest;
import com.codesupreme.couriersub.user.dto.RegisterRequest;
import com.codesupreme.couriersub.user.entity.User;
import com.codesupreme.couriersub.user.service.PasswordResetService;
import com.codesupreme.couriersub.user.dto.ResetPasswordRequest;
import com.codesupreme.couriersub.user.service.UserService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
@CrossOrigin(origins = "https://mototaksi.az")
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;
    private final PasswordResetService passwordResetService;

    public AuthController(UserService userService, PasswordResetService passwordResetService) {
        this.userService = userService;
        this.passwordResetService = passwordResetService;
    }

    @PostMapping("/register")
    public ApiResponse<User> register(@Valid @RequestBody RegisterRequest req) {
        User u = userService.register(req);
        return ApiResponse.ok("Qeydiyyat uğurludur", u);
    }

    @PostMapping("/login")
    public ApiResponse<User> login(@Valid @RequestBody LoginRequest req) {
        User u = userService.login(req);
        return ApiResponse.ok("Giriş uğurludur", u);
    }

    // Postman test üçün: phone ilə user məlumatını görmək
    @GetMapping("/by-phone")
    public ApiResponse<User> byPhone(@RequestParam String phone) {
        String normalized = PhoneUtil.normalize(phone); // 055... -> 994...
        return ApiResponse.ok("OK", userService.getByPhone(normalized));
    }

    // ✅ NEW: reset password
    @PostMapping("/reset-password")
    public ApiResponse<Void> resetPassword(@Valid @RequestBody ResetPasswordRequest req) {
        String normalized = PhoneUtil.normalize(req.getPhone());
        passwordResetService.resetPassword(normalized, req.getNewPassword());
        return ApiResponse.ok("Şifrə dəyişdirildi", null);
    }
}
