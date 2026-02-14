package com.codesupreme.couriersub.referral.controller;

import com.codesupreme.couriersub.common.ApiResponse;
import com.codesupreme.couriersub.common.util.PhoneUtil;
import com.codesupreme.couriersub.referral.dto.ReferralResponse;
import com.codesupreme.couriersub.referral.service.ReferralService;
import com.codesupreme.couriersub.user.entity.User;
import com.codesupreme.couriersub.user.repo.UserRepository;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "https://mototaksi.az")
@RestController
@RequestMapping("/api/me")
public class ReferralController {

    private final UserRepository users;
    private final ReferralService referral;

    public ReferralController(UserRepository users, ReferralService referral) {
        this.users = users;
        this.referral = referral;
    }

    @GetMapping("/referral")
    public ApiResponse<ReferralResponse> myReferral(@RequestParam String phone) {
        String p = PhoneUtil.normalize(phone);
        User u = users.findByPhone(p).orElseThrow(() -> new IllegalArgumentException("User tapılmadı"));

        String code = referral.ensureReferralCode(u);
        var wallet = referral.getOrCreateWallet(u);

        ReferralResponse r = new ReferralResponse();
        r.referralCode = code;
        r.referralUrl = "https://mototaksi.az/devet/" + code;
        r.invitedCount = referral.invitedCount(u);
        r.balanceInt = wallet.getBalanceInt();

        return ApiResponse.ok("OK", r);
    }
}
