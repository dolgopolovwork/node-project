package ru.babobka.nodeutils.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by 123 on 19.08.2017.
 */
//TODO добавь md5
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
}
