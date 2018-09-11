package ru.babobka.nodesecurity.service;

import ru.babobka.nodeutils.util.ArrayUtil;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

/**
 * Created by 123 on 05.09.2018.
 */
public class AESService {

    private static final SecureRandom RANDOM = new SecureRandom();
    private static final int IV_LENGTH = 16;

    public byte[] encrypt(byte[] plainData, byte[] key) throws IOException {
        if (ArrayUtil.isEmpty(plainData)) {
            throw new IllegalArgumentException("cannot encrypt empty data");
        } else if (ArrayUtil.isEmpty(key)) {
            throw new IllegalArgumentException("cannot encrypt data using empty key");
        }
        Cipher cipher = createAESCipher();
        byte[] iv = createIV();
        SecretKey secretKey = new SecretKeySpec(key, "AES");
        IvParameterSpec parameterSpec = new IvParameterSpec(iv);
        try {
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, parameterSpec);
            byte[] cipherText = cipher.doFinal(plainData);
            ByteBuffer byteBuffer = ByteBuffer.allocate(4 + iv.length + cipherText.length);
            byteBuffer.putInt(iv.length);
            byteBuffer.put(iv);
            byteBuffer.put(cipherText);
            return byteBuffer.array();
        } catch (InvalidAlgorithmParameterException | BadPaddingException | InvalidKeyException | IllegalBlockSizeException e) {
            throw new IOException("cannot encrypt data", e);
        }
    }

    public byte[] decrypt(byte[] cipherMessage, byte[] key) throws IOException {
        if (ArrayUtil.isEmpty(cipherMessage)) {
            throw new IllegalArgumentException("cannot decrypt empty data");
        } else if (ArrayUtil.isEmpty(key)) {
            throw new IllegalArgumentException("cannot decrypt data using empty key");
        }

        ByteBuffer byteBuffer = ByteBuffer.wrap(cipherMessage);
        int ivLength = byteBuffer.getInt();
        if (ivLength != IV_LENGTH) { // check input parameter
            throw new IllegalArgumentException("invalid iv length");
        }
        byte[] iv = new byte[ivLength];
        byteBuffer.get(iv);
        byte[] cipherText = new byte[byteBuffer.remaining()];
        byteBuffer.get(cipherText);
        Cipher cipher = createAESCipher();
        SecretKey secretKey = new SecretKeySpec(key, "AES");
        IvParameterSpec parameterSpec = new IvParameterSpec(iv);
        try {
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, parameterSpec);
            return cipher.doFinal(cipherText);
        } catch (Exception e) {
            throw new IOException("cannot decrypt data", e);
        }
    }

    private static Cipher createAESCipher() {
        try {
            return Cipher.getInstance("AES/CTR/NoPadding");
        } catch (NoSuchAlgorithmException | NoSuchPaddingException expected) {
            //If do right, no can throw
            throw new RuntimeException(expected);
        }
    }

    private static byte[] createIV() {
        byte[] iv = new byte[IV_LENGTH];
        RANDOM.nextBytes(iv);
        return iv;
    }
}
