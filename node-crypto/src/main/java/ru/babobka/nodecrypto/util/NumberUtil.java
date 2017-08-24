package ru.babobka.nodecrypto.util;

import ru.babobka.nodecrypto.model.SafePrime;

import java.math.BigInteger;
import java.security.SecureRandom;

/**
 * Created by 123 on 17.07.2017.
 */
public interface NumberUtil {

    static BigInteger generateExponent(SafePrime prime) {
        if (prime == null) {
            throw new IllegalArgumentException("prime is null");
        }
        BigInteger exp = BigInteger.ONE;
        BigInteger pMinusOne = prime.getPrime().subtract(BigInteger.ONE);
        int bits = prime.getPrime().bitLength() - 1;
        while (exp.equals(BigInteger.ONE) || exp.equals(pMinusOne)) {
            exp = new BigInteger(bits, new SecureRandom());
        }
        return exp;

    }
}
