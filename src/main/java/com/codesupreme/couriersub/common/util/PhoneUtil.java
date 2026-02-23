package com.codesupreme.couriersub.common.util;

public class PhoneUtil {

    public static String normalize(String phone) {
        if (phone == null) return null;

        // bütün qeyri-rəqəmləri sil
        String p = phone.replaceAll("[^0-9]", "").trim();

        if (p.isEmpty()) return null;

        // +994... və ya 994...
        if (p.startsWith("994") && p.length() == 12) {
            return p;
        }

        // 0701234567 → 994701234567
        if (p.startsWith("0") && p.length() == 10) {
            return "994" + p.substring(1);
        }

        // 701234567 → 994701234567
        if (p.length() == 9) {
            return "994" + p;
        }

        return p; // fallback
    }

    public static String toWaJid(String normalized994) {
        return normalized994 + "@s.whatsapp.net";
    }
}