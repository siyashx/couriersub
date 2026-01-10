package com.codesupreme.couriersub.admin.controller;

import com.codesupreme.couriersub.admin.dto.AdminDecisionRequest;
import com.codesupreme.couriersub.admin.dto.AdminUserDetailResponse;
import com.codesupreme.couriersub.admin.dto.AdminUserListItem;
import com.codesupreme.couriersub.common.ApiResponse;
import com.codesupreme.couriersub.admin.service.AdminService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final AdminService admin;

    public AdminController(AdminService admin) {
        this.admin = admin;
    }

    @GetMapping("/users")
    public ApiResponse<List<AdminUserListItem>> list(@RequestParam(required = false) String status) {
        return ApiResponse.ok("OK", admin.listUsers(status));
    }

    @GetMapping("/users/{id}")
    public ApiResponse<AdminUserDetailResponse> detail(@PathVariable Long id) {
        return ApiResponse.ok("OK", admin.getUserDetail(id));
    }

    @PostMapping("/users/{id}/approve")
    public ApiResponse<Void> approve(@PathVariable Long id, @RequestBody(required = false) AdminDecisionRequest req) {
        admin.approve(id, req);
        return ApiResponse.ok("Təsdiqləndi", null);
    }

    @PostMapping("/users/{id}/reject")
    public ApiResponse<Void> reject(@PathVariable Long id, @RequestBody(required = false) AdminDecisionRequest req) {
        admin.reject(id, req);
        return ApiResponse.ok("Rədd edildi", null);
    }

    @PostMapping("/users/{id}/activate")
    public ApiResponse<Void> activate(@PathVariable Long id) {
        admin.setActive(id, true);
        return ApiResponse.ok("Aktiv edildi", null);
    }

    @PostMapping("/users/{id}/deactivate")
    public ApiResponse<Void> deactivate(@PathVariable Long id) {
        admin.setActive(id, false);
        return ApiResponse.ok("Deaktiv edildi", null);
    }

    @PostMapping("/users/{id}/force-add-group")
    public ApiResponse<Void> forceAdd(@PathVariable Long id) {
        admin.forceAddToGroup(id);
        return ApiResponse.ok("Qrupa əlavə olundu", null);
    }

    @PostMapping("/users/{id}/force-remove-group")
    public ApiResponse<Void> forceRemove(@PathVariable Long id) {
        admin.forceRemoveFromGroup(id);
        return ApiResponse.ok("Qrupdan çıxarıldı", null);
    }

}
