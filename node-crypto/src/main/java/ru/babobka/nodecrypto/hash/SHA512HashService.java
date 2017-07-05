package ru.babobka.nodecrypto.hash;

import ru.babobka.nodecrypto.util.ArrayUtil;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by 123 on 03.07.2017.
 */
public class SHA512HashService implements HashService {

    private static final byte[] EMPTY_ARRAY = {};

    @Override
    public byte[] hash(byte[] bytes) {
        return hash(bytes, EMPTY_ARRAY);
    }

    @Override
    public byte[] hash(byte[] bytes, byte[] salt) {
        try {
            return MessageDigest.getInstance("SHA_512").digest(ArrayUtil.concat(bytes, salt));
        } catch (NoSuchAlgorithmException e) {
            throw new UnsupportedOperationException(e);
        }
    }


}
