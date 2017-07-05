package ru.babobka.nodecrypto;

import java.math.BigInteger;

/**
 * Created by 123 on 04.07.2017.
 */
class CryptoPolitics {

    static final int MIN_SAFE_PRIME_BIT_LENGTH = 512;

    static final int MIN_EXP_BIT_LENGTH = 1024;

    static final int MIN_SALT_BIT_LENGTH = 1024;

    boolean isExponentValid(BigInteger exp) {
        return exp.bitLength() >= MIN_EXP_BIT_LENGTH;
    }

    boolean isSafePrimeValid(SafePrime safePrime) {
        return safePrime.getPrime().bitLength() >= MIN_SAFE_PRIME_BIT_LENGTH;
    }

    boolean isSaltIsValid(byte[] salt) {
        return salt.length >= MIN_SALT_BIT_LENGTH;
    }

}
