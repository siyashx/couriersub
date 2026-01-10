package com.codesupreme.couriersub.whatsapp.evolution;

import com.codesupreme.couriersub.common.util.PhoneUtil;
import org.springframework.stereotype.Service;

@Service
public class EvolutionService {

    private final EvolutionClient client;

    public EvolutionService(EvolutionClient client) {
        this.client = client;
    }

    public void sendOtp(String phone, String otp) {
        String normalized = PhoneUtil.normalize(phone);

        String msg = "Təsdiqləmə kodunuz: " + otp + "\nBu kodu heç kimlə paylaşmayın.";
        // ✅ səndə sendText number üçün çox vaxt 994... işləyir
        client.sendText(normalized, msg);

        // Əgər səndə 994... işləməsə, bunu edərik:
        // client.sendText(PhoneUtil.toWaJid(normalized), msg);
    }
}
