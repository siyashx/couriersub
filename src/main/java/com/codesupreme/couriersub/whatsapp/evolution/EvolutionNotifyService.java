package com.codesupreme.couriersub.whatsapp.evolution;

import com.codesupreme.couriersub.common.util.PhoneUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EvolutionNotifyService {

    private final EvolutionClient evolutionClient;

    @Value("${evolution.adminPhone}")
    private String adminPhone; // 99470...

    // admin JID: 994...@s.whatsapp.net
    private String adminJid() {
        String normalized = PhoneUtil.normalize(adminPhone);
        return normalized + "@s.whatsapp.net";
    }

    // UI format: 0XXXXXXXXX (0701234567)
    private String displayAz(String phone) {
        String p = PhoneUtil.normalize(phone); // 994XXXXXXXXX
        if (p.startsWith("994") && p.length() >= 12) return "0" + p.substring(3);
        return p;
    }

    public void notifyAddedToGroup(String userPhone) {
        String msg = "qrupa elave edildi bratim 🤪 : " + displayAz(userPhone);
        evolutionClient.sendText(adminJid(), msg);
    }
}
