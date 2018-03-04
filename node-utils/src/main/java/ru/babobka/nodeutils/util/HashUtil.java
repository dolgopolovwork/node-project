package ru.babobka.nodeutils.util;

import org.apache.commons.lang.builder.HashCodeBuilder;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by 123 on 19.08.2017.
 */
public interface HashUtil {
    static byte[] sha2(String message) {
        if (message == null) {
            throw new IllegalArgumentException("message is null");
        }
        try {
            MessageDigest sha256 = MessageDigest.getInstance("SHA-256");
            byte[] messageBytes = message.getBytes(TextUtil.CHARSET);
            return sha256.digest(messageBytes);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalArgumentException(e);
        }
    }

    static int hashMap(Map<?, ?> map) {
        if (map == null) {
            throw new IllegalArgumentException("Can not hash null map");
        }
        HashCodeBuilder hashBuilder = new HashCodeBuilder(17, 31);
        for (Map.Entry<?, ?> entry : map.entrySet()) {
            if (entry.getValue() != null) {
                hashBuilder.append(entry.getValue().hashCode()).append(entry.getKey());
            }
        }
        return hashBuilder.toHashCode();
    }

    static String hexSha2(String message) {
        byte[] hash = sha2(message);
        StringBuilder hexString = new StringBuilder();
        for (byte hashCode : hash) {
            if ((0xff & hashCode) < 0x10) {
                hexString.append("0").append(Integer.toHexString((0xFF & hashCode)));
            } else {
                hexString.append(Integer.toHexString(0xFF & hashCode));
            }
        }
        return hexString.toString();
    }
}
