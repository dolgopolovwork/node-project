package ru.babobka.nodeutils.util;

import java.io.*;
import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by 123 on 19.08.2017.
 */
public class HashUtil {

    private static final String SHA_256 = "SHA-256";

    private HashUtil() {

    }

    public static byte[] sha2(String message) {
        if (message == null) {
            throw new IllegalArgumentException("message is null");
        }
        MessageDigest sha256 = getSHA256MessageDigest();
        byte[] messageBytes = message.getBytes(TextUtil.CHARSET);
        return sha256.digest(messageBytes);
    }

    public static byte[] sha2(int... hashCodes) {
        if (ArrayUtil.isEmpty(hashCodes)) {
            throw new IllegalArgumentException("hashCodes is empty");
        }
        MessageDigest sha256 = getSHA256MessageDigest();
        for (int hashCode : hashCodes) {
            byte[] bytes = ByteBuffer.allocate(4).putInt(hashCode).array();
            sha256.update(bytes);
        }
        return sha256.digest();
    }

    public static int safeHashCode(Object object) {
        if (object == null) {
            return 0;
        }
        return object.hashCode();
    }

    public static byte[] sha2(Iterator<Map.Entry<String, Serializable>> iterator) {
        if (iterator == null) {
            throw new IllegalArgumentException("cannot hash null data");
        }
        try {
            MessageDigest sha256 = getSHA256MessageDigest();
            while (iterator.hasNext()) {
                Map.Entry<String, Serializable> entry = iterator.next();
                sha256.update(entry.getKey().getBytes(TextUtil.CHARSET));
                sha256.update(toByteArray(entry.getValue()));
            }
            return sha256.digest();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static byte[] toByteArray(Object object) throws IOException {
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream(); ObjectOutput out = new ObjectOutputStream(bos)) {
            out.writeObject(object);
            out.flush();
            return bos.toByteArray();
        }
    }

    public static byte[] sha2(byte[] message, byte[] salt) {
        if (message == null) {
            throw new IllegalArgumentException("message is null");
        } else if (salt == null) {
            throw new IllegalArgumentException("salt is null");
        }
        MessageDigest sha256 = getSHA256MessageDigest();
        sha256.update(message);
        sha256.update(salt);
        return sha256.digest();
    }

    public static byte[] sha2(byte[] message) {
        if (ArrayUtil.isEmpty(message)) {
            throw new IllegalArgumentException("message is empty");
        }
        MessageDigest sha256 = getSHA256MessageDigest();
        return sha256.digest(message);
    }

    public static String hexSha2(String message) {
        if (message == null) {
            throw new IllegalArgumentException("message is null");
        }
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

    public static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i + 1), 16));
        }
        return data;
    }

    private static MessageDigest getSHA256MessageDigest() {
        try {
            return MessageDigest.getInstance(SHA_256);
        } catch (NoSuchAlgorithmException e) {
            //this will never happen
            throw new RuntimeException(e);
        }
    }
}
