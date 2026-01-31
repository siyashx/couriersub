package com.codesupreme.couriersub.whatsapp.controller;

import com.codesupreme.couriersub.whatsapp.group.WhatsAppGroupService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/whatsapp/group")
@RequiredArgsConstructor
public class WhatsAppGroupController {

    private final WhatsAppGroupService whatsAppGroupService;

    @GetMapping("/participant")
    public Map<String, Object> isParticipant(@RequestParam String phone) {
        boolean inGroup = whatsAppGroupService.isParticipantInGroup(phone);
        return Map.of("inGroup", inGroup);
    }
}
