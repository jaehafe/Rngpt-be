package org.jh.oauthjwt.util;

import java.util.Random;

public class VerificationCodeUtil {
    public static String generateVerificationCode() {
        Random random = new Random();
        return String.format("%06d", random.nextInt(999999));
    }
}
