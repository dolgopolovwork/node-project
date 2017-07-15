package ru.babobka.nodecrypto.hash;


import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by 123 on 03.07.2017.
 */
public class SHA512HashService implements HashService {

    @Override
    public byte[] hash(byte[] bytes) {
        try {
            return MessageDigest.getInstance("SHA_512").digest(bytes);
        } catch (NoSuchAlgorithmException e) {
            throw new UnsupportedOperationException(e);
        }
    }


}
