package ru.babobka.nodeserials.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

public interface MathUtil {

    static byte[] sha2(String message) {
	if (message != null) {
	    try {
		MessageDigest sha256 = MessageDigest.getInstance("SHA-256");
		byte[] messageBytes = message.getBytes(StandardCharsets.UTF_8);
		return sha256.digest(messageBytes);
	    } catch (Exception e) {
		e.printStackTrace();

	    }
	}
	return new byte[256];
    }
}