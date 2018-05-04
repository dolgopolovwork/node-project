package ru.babobka.nodeutils.util;

import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by 123 on 19.08.2017.
 */
public interface HashUtil {

    String SHA_256 = "SHA-256";

    static byte[] sha2(String message) {
        if (message == null) {
            throw new IllegalArgumentException("message is null");
        }
        try {
            MessageDigest sha256 = MessageDigest.getInstance(SHA_256);
            byte[] messageBytes = message.getBytes(TextUtil.CHARSET);
            return sha256.digest(messageBytes);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalArgumentException(e);
        }
    }

    static byte[] sha2(int... hashCodes) {
        if (hashCodes == null || hashCodes.length == 0) {
            throw new IllegalArgumentException("hashCodes is empty");
        }
        try {
            MessageDigest sha256 = MessageDigest.getInstance(SHA_256);
            for (int hashCode : hashCodes) {
                byte[] bytes = ByteBuffer.allocate(4).putInt(hashCode).array();
                sha256.update(bytes);
            }
            return sha256.digest();
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalArgumentException(e);
        }
    }

    static int safeHashCode(Object object) {
        if (object == null) {
            return 0;
        }
        return object.hashCode();
    }

    static byte[] sha2(byte[] message, byte[] salt) {
        if (message == null || message.length == 0) {
            throw new IllegalArgumentException("message is empty");
        }
        try {
            MessageDigest sha256 = MessageDigest.getInstance(SHA_256);
            sha256.update(message);
            sha256.update(salt);
            return sha256.digest();
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalArgumentException(e);
        }
    }

    static byte[] sha2(byte[] message) {
        if (message == null || message.length == 0) {
            throw new IllegalArgumentException("message is empty");
        }
        try {
            MessageDigest sha256 = MessageDigest.getInstance(SHA_256);
            return sha256.digest(message);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalArgumentException(e);
        }
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

    static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i + 1), 16));
        }
        return data;
    }
}
