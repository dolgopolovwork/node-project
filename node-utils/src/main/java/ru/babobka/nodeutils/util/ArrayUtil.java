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
}
