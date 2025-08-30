package com.example.wallet_app.utils;

import java.time.Instant;
import java.util.concurrent.ThreadLocalRandom;

public class TSIDGenerator {

    private static final char[] ALPHABET = "0123456789ABCDEFGHJKMNPQRSTVWXYZ".toCharArray();
    // Crockford’s Base32 (no I, L, O, U)

    public static String generateTSID() {
        long millis = Instant.now().toEpochMilli();

        // Shift left and add some randomness for uniqueness
        long value = (millis << 20) | (ThreadLocalRandom.current().nextInt(1 << 20));

        return toBase32(value);
    }

    private static String toBase32(long value) {
        StringBuilder sb = new StringBuilder();
        while (value > 0) {
            sb.append(ALPHABET[(int) (value & 31)]);
            value >>>= 5;
        }
        return sb.reverse().toString();
    }
}