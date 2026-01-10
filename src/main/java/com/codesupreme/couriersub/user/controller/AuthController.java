package com.codesupreme.couriersub.user.controller;

import com.codesupreme.couriersub.common.ApiResponse;
import com.codesupreme.couriersub.user.dto.LoginRequest;
import com.codesupreme.couriersub.user.dto.RegisterRequest;
import com.codesupreme.couriersub.user.entity.User;
import com.codesupreme.couriersub.user.service.UserService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
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
        return ApiResponse.ok("OK", userService.getByPhone(phone));
    }
}
