package ru.babobka.nodecrypto.util;

import java.security.SecureRandom;
import java.util.Arrays;

/**
 * Created by 123 on 03.07.2017.
 */
public interface ArrayUtil {

    static byte[] concat(byte[] first, byte[] second) {
        if (first == null || second == null) {
            throw new IllegalArgumentException("Can not concat null array");
        }
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

    static byte[] getLastBlock(byte[] array, int blockSize) {
        if (array == null || array.length == 0) {
            throw new IllegalArgumentException("array is empty");
        }
        if (blockSize <= 0) {
            throw new IllegalArgumentException("blockSize must be bigger than 0");
        }
        if (blockSize >= array.length) {
            return array;
        }
        int blocks = (int) Math.ceil(array.length / (double) blockSize);
        int begin = (blocks - 1) * blockSize;
        int end = array.length;
        return Arrays.copyOfRange(array, begin, end);
    }

    static byte[] getFirstBlock(byte[] array, int blockSize) {
        if (array == null || array.length == 0) {
            throw new IllegalArgumentException("array is empty");
        }
        if (blockSize <= 0) {
            throw new IllegalArgumentException("blockSize must be bigger than 0");
        } else if (blockSize >= array.length) {
            return array;
        }
        int begin = 0;
        int end = Math.min(array.length, blockSize);
        return Arrays.copyOfRange(array, begin, end);
    }
}
