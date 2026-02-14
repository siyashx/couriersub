package com.codesupreme.couriersub.common.util;

import java.security.SecureRandom;

public class ReferralUtil {
    private static final SecureRandom R = new SecureRandom();
    private static final char[] ALPH = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789".toCharArray();

    public static String generateCode(int len) {
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++) sb.append(ALPH[R.nextInt(ALPH.length)]);
        return sb.toString();
    }
}
