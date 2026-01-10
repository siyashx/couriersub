package com.codesupreme.couriersub.common.util;

import java.security.SecureRandom;

public class OtpUtil {
    private static final SecureRandom RND = new SecureRandom();

    public static String generate6() {
        int n = 100000 + RND.nextInt(900000); // 100000..999999
        return String.valueOf(n);
    }
}
