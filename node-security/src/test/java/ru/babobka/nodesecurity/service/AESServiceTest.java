package ru.babobka.nodesecurity.service;

import org.junit.Test;
import ru.babobka.nodeutils.util.TextUtil;

import java.io.IOException;
import java.security.SecureRandom;
import java.util.Arrays;

import static org.junit.Assert.*;

/**
 * Created by 123 on 05.09.2018.
 */
public class AESServiceTest {
    private static final AESService AES = new AESService();
    private static final int KEY_SIZE = 16;

    @Test
    public void testEncryptDecryptHelloWorld() throws IOException {
        String message = "Hello World";
        byte[] messageData = message.getBytes(TextUtil.CHARSET);
        byte[] key = new byte[KEY_SIZE];
        SecureRandom secureRandom = new SecureRandom();
        secureRandom.nextBytes(key);
        byte[] cipher = AES.encrypt(messageData, key);
        assertFalse(Arrays.equals(messageData, cipher));
        byte[] decryptedBytes = AES.decrypt(cipher, key);
        String decrypted = new String(decryptedBytes);
        assertEquals(message, decrypted);
    }

    @Test
    public void testEncryptDecryptRandom() throws IOException {
        SecureRandom secureRandom = new SecureRandom();
        for (int i = 0; i < 100; i++) {
            int messageLength = secureRandom.nextInt(1_000) + 1;
            byte[] messageData = new byte[messageLength];
            secureRandom.nextBytes(messageData);
            byte[] key = new byte[KEY_SIZE];
            secureRandom.nextBytes(key);
            byte[] cipher = AES.encrypt(messageData, key);
            assertFalse(Arrays.equals(messageData, cipher));
            byte[] decryptedBytes = AES.decrypt(cipher, key);
            assertArrayEquals(messageData, decryptedBytes);
        }
    }

    @Test
    public void testEncryptDecryptHelloWorldBadKey() throws IOException {
        String message = "Hello World";
        byte[] messageData = message.getBytes(TextUtil.CHARSET);
        byte[] key1 = new byte[KEY_SIZE];
        byte[] key2 = new byte[KEY_SIZE];
        SecureRandom secureRandom = new SecureRandom();
        secureRandom.nextBytes(key1);
        secureRandom.nextBytes(key2);
        byte[] cipher = AES.encrypt(messageData, key1);
        assertFalse(Arrays.equals(messageData, cipher));
        byte[] decryptedBytes = AES.decrypt(cipher, key2);
        String decrypted = new String(decryptedBytes);
        assertNotEquals(message, decrypted);
    }
}