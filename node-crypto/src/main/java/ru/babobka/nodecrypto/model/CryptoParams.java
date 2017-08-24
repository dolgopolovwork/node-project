package ru.babobka.nodecrypto.model;

import java.math.BigInteger;

/**
 * Created by 123 on 24.04.2017.
 */
public class CryptoParams {

    private static final BigInteger TWO = BigInteger.valueOf(2L);

    private final BigInteger generator;

    private final SafePrime safePrime;

    public CryptoParams(SafePrime safePrime) {
        this.generator = findGenerator(safePrime);
        this.safePrime = safePrime;
    }

    private static BigInteger findGenerator(SafePrime safePrime) {
        BigInteger generator = TWO;
        while (generator.modPow(TWO, safePrime.getPrime()).equals(BigInteger.ONE) || generator.modPow(safePrime.getSophieNumber(), safePrime.getPrime()).equals(BigInteger.ONE)) {
            generator = generator.add(BigInteger.ONE).mod(safePrime.getPrime());
        }
        return generator;
    }

    public BigInteger getGenerator() {
        return generator;
    }

    public SafePrime getSafePrime() {
        return safePrime;
    }

}
