package ru.babobka.nodeserials.util;

import java.security.MessageDigest;

public interface MathUtil {

	static byte[] sha2(String message) {
		if (message != null) {
			try {
				MessageDigest sha256 = MessageDigest.getInstance("SHA-256");
				byte[] messageBytes = message.getBytes();
				return sha256.digest(messageBytes);
			} catch (Exception e) {
				e.printStackTrace();

			}
		}
		return new byte[256];
	}
}