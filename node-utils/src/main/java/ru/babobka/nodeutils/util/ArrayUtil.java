package ru.babobka.nodeutils.util;

/**
 * Created by 123 on 28.09.2017.
 */
public interface ArrayUtil {
    static boolean isNull(Object... objects) {
        if (objects == null) {
            return true;
        }
        for (Object object : objects) {
            if (object == null) {
                return true;
            }
        }
        return false;
    }

    static void validateNonNull(Object... objects) {
        if (isNull(objects)) {
            throw new IllegalArgumentException("All the values must be set");
        }
    }

    static boolean isEmpty(byte[] array) {
        return array == null || array.length == 0;
    }

    static boolean isEmpty(int[] array) {
        return array == null || array.length == 0;
    }

    static boolean isUnique(int... array) {
        if (ArrayUtil.isEmpty(array)) {
            return true;
        } else if (array.length == 1) {
            return true;
        }
        for (int i = 0; i < array.length; i++) {
            for (int j = i + 1; j < array.length; j++) {
                if (array[i] == array[j]) {
                    return false;
                }
            }
        }
        return true;
    }
}
