package ru.babobka.nodeserials;

import static org.junit.Assert.*;

import java.math.BigInteger;

import org.junit.Test;

public class RSATest {

	private static final int keyBitLength = 256;

	private static final int tests = 100;

	@Test
	public void stringEncryptionTest() {

		for (int i = 0; i < tests; i++) {
			String message = generateRandomString();
			BigInteger originalMessageInteger = RSA.stringToBigInteger(message);
			RSA rsa = new RSA(keyBitLength);
			BigInteger encryptedMessageInteger = rsa.encrypt(message);
			BigInteger decryptedMessageInteger = rsa.decrypt(encryptedMessageInteger);
			assertEquals(originalMessageInteger, decryptedMessageInteger);
		}
	}

	private String generateRandomString() {
		int length = (int) (Math.random() * 1000) + 1;
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < length; i++) {
			sb.append((char) (Math.random() * 128));
		}
		return sb.toString();
	}

}
