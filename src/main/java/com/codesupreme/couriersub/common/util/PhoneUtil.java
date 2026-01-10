package com.codesupreme.couriersub.common.util;

public class PhoneUtil {

    // Sən istəsən sonra daha sərt validasiya edərik.
    public static String normalize(String phone) {
        if (phone == null) return null;
        String p = phone.replaceAll("[^0-9]", "").trim();
        if (p.startsWith("0")) {
            // 055... -> 99455...
            p = "994" + p.substring(1);
        }
        return p;
    }

    // Evolution üçün jid formatı (çox vaxt belə olur)
    public static String toWaJid(String normalized994) {
        return normalized994 + "@s.whatsapp.net";
    }
}
