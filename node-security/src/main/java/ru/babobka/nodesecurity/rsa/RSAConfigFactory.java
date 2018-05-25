package ru.babobka.nodesecurity.rsa;

import ru.babobka.nodeutils.math.SafePrime;
import ru.babobka.nodeutils.util.MathUtil;

import java.math.BigInteger;

/**
 * Created by 123 on 20.05.2018.
 */
public class RSAConfigFactory {

    private static final int MIN_BITS = 16;

    public static RSAConfig create(int modulusBits) {
        if (modulusBits < MIN_BITS) {
            throw new IllegalArgumentException(modulusBits + " is too little. " +
                    "need at least " + MIN_BITS + " bits");
        }
        if (modulusBits % 2 != 0) {
            modulusBits++;
        }
        int factorBits = modulusBits / 2;
        BigInteger p = SafePrime.secureRandom(factorBits).getPrime();
        BigInteger q = SafePrime.secureRandom(factorBits).getPrime();
        while (q.equals(p)) {
            q = SafePrime.secureRandom(factorBits).getPrime();
        }
        BigInteger n = p.multiply(q);
        BigInteger fiN = p.subtract(BigInteger.ONE).multiply(q.subtract(BigInteger.ONE));
        BigInteger e = MathUtil.getRelativePrime(fiN);
        BigInteger d = e.modInverse(fiN);
        return new RSAConfig(new RSAPrivateKey(d, n), new RSAPublicKey(e, n));
    }
}
