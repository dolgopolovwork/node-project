package ru.babobka.nodecrypto.hash;

import com.lambdaworks.crypto.SCrypt;

import java.security.GeneralSecurityException;

/**
 * Created by 123 on 04.07.2017.
 */
public class SCryptHashService implements HashService {

    private static final int CPU_COST = 16384;
    private static final int MEMORY_COST = 8;
    private static final int PARALLELIZATION = 1;
    private static final int DEFAULT_HASH_BYTE_LENGTH = 256;
    private static final byte[] EMPTY_ARRAY = {};


    @Override
    public byte[] hash(byte[] bytes) {
        return hash(bytes, DEFAULT_HASH_BYTE_LENGTH);
    }


    public byte[] hash(byte[] bytes, int length) {
        return hash(bytes, EMPTY_ARRAY, length);
    }

    public byte[] hash(byte[] bytes, byte[] salt, int length) {
        if (length <= 0) {
            throw new IllegalArgumentException("length must be bigger than 0");
        }
        try {
            return SCrypt.scrypt(bytes, salt, CPU_COST, MEMORY_COST, PARALLELIZATION, length);
        } catch (GeneralSecurityException e) {
            throw new UnsupportedOperationException(e);
        }
    }
}
