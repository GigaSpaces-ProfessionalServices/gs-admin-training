package com.gigaspaces.common;

import java.nio.charset.Charset;
import java.util.Random;

public class RandomUtils {

    private static final int NUM_OF_CHARS = 15;
    private static Random random = new Random();

    public static Integer nextInt() {
        return random.nextInt() & Integer.MAX_VALUE; // transform to positive
    }

    public static String nextString() {
        byte[] array = new byte[NUM_OF_CHARS];

        random.nextBytes(array);
        return new String(array, Charset.forName("UTF-8"));
    }
}
