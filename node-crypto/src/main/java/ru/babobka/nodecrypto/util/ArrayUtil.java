package ru.babobka.nodecrypto.util;

import java.security.SecureRandom;
import java.util.Arrays;

/**
 * Created by 123 on 03.07.2017.
 */
public interface ArrayUtil {

    static byte[] concat(byte[] first, byte[] second) {
        byte[] result = Arrays.copyOf(first, first.length + second.length);
        System.arraycopy(second, 0, result, first.length, second.length);
        return result;
    }

    static byte[] randomArray(int length) {
        if (length <= 0) {
            throw new IllegalArgumentException("Invalid length " + length);
        }
        SecureRandom secureRandom = new SecureRandom();
        byte[] array = new byte[length];
        secureRandom.nextBytes(array);
        return array;

    }
}
