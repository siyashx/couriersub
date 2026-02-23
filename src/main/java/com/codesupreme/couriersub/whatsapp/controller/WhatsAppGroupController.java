package com.codesupreme.couriersub.whatsapp.controller;

import com.codesupreme.couriersub.whatsapp.group.WhatsAppGroupService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/whatsapp/group")
@RequiredArgsConstructor
public class WhatsAppGroupController {

    private final WhatsAppGroupService whatsAppGroupService;

    @GetMapping("/participant")
    public Map<String, Object> isParticipant(@RequestParam String phone) {
        try {
            boolean inGroup = whatsAppGroupService.isParticipantInGroup(phone);
            return Map.of("inGroup", inGroup);
        } catch (Exception ex) {
            return Map.of(
                    "inGroup", null,
                    "message", "WhatsApp qrupu yoxlanılmadı (Evolution API cavab vermədi)."
            );
        }
    }

    @PostMapping("/try-add")
    public Map<String, Object> tryAdd(@RequestParam String phone) {
        try {
            whatsAppGroupService.addToGroup(phone);
            return Map.of("ok", true);
        } catch (Exception ex) {
            return Map.of(
                    "ok", false,
                    "message", "WhatsApp qrupuna əlavə etmə cəhdi alınmadı (Evolution API cavab vermədi)."
            );
        }
    }
}