package ru.babobka.nodecrypto.crypto;

import ru.babobka.nodecrypto.util.ArrayUtil;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.Arrays;

/**
 * Created by 123 on 03.07.2017.
 */
public class AESCryptoService implements CryptoService {

    private final byte[] key;

    private static final String AES_MODE = "AES/CBC/PKCS5Padding";

    private static final String AES = "AES";

    private static final String SUN_JCE = "SunJCE";

    private static final int IV_BYTE_LENGTH = 16;


    public AESCryptoService(byte[] key) {
        if (key == null) {
            throw new IllegalArgumentException("key is null");
        }
        if (!isKeyLengthValid(key.length)) {
            throw new IllegalArgumentException("key length is not valid");
        }
        this.key = key.clone();
    }

    private static boolean isKeyLengthValid(int keyLength) {
        return keyLength == 16 || keyLength == 24 || keyLength == 32;
    }

    @Override
    public byte[] encrypt(byte[] message) {
        return encrypt(message, ArrayUtil.randomArray(IV_BYTE_LENGTH));

    }

    public byte[] encrypt(byte[] message, byte[] iv) {
        try {
            Cipher cipher = Cipher.getInstance(AES_MODE, SUN_JCE);
            SecretKeySpec secretKeySpec = new SecretKeySpec(key, AES);
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, new IvParameterSpec(iv));
            return cipher.doFinal(message);
        } catch (NoSuchAlgorithmException | NoSuchProviderException | NoSuchPaddingException | BadPaddingException | IllegalBlockSizeException | InvalidAlgorithmParameterException | InvalidKeyException e) {
            throw new UnsupportedOperationException(e);
        }
    }

    @Override
    public byte[] decrypt(byte[] cipherText) {
        try {
            Cipher cipher = Cipher.getInstance(AES_MODE, SUN_JCE);
            SecretKeySpec secretKeySpec = new SecretKeySpec(key, AES);
            byte[] iv = Arrays.copyOfRange(cipherText, 0, IV_BYTE_LENGTH);
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, new IvParameterSpec(iv));
            return cipher.doFinal(cipherText);
        } catch (NoSuchAlgorithmException | NoSuchProviderException | NoSuchPaddingException | BadPaddingException | IllegalBlockSizeException | InvalidAlgorithmParameterException | InvalidKeyException e) {
            throw new UnsupportedOperationException(e);
        }
    }
}