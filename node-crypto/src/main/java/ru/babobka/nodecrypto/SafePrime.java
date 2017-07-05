package ru.babobka.nodecrypto;

import java.math.BigInteger;
import java.security.SecureRandom;

/**
 * Created by 123 on 24.04.2017.
 */
public class SafePrime {

    private static final BigInteger TWO = BigInteger.valueOf(2L);

    private static final int CERTAINTY = 64;

    private final BigInteger sophieNumber;

    private final BigInteger prime;


    public SafePrime(BigInteger sophieNumber, BigInteger prime) {
        if (prime == null) {
            throw new IllegalArgumentException("prime is null");
        } else if (sophieNumber == null) {
            throw new IllegalArgumentException("sophieNumber is null");
        } else if (!prime.isProbablePrime(CERTAINTY)) {
            throw new IllegalArgumentException(prime + " is not prime");
        } else if (!prime.subtract(BigInteger.ONE).mod(sophieNumber).equals(BigInteger.ZERO)) {
            throw new IllegalArgumentException(sophieNumber + " doesn't form Sophie number for " + prime);
        } else if (!prime.subtract(BigInteger.ONE).divide(sophieNumber).equals(TWO)) {
            throw new IllegalArgumentException(prime + " is not safe");
        }
        this.sophieNumber = sophieNumber;
        this.prime = prime;
    }


    private SafePrime(int bits) {
        if (bits <= 2) {
            throw new IllegalArgumentException("Invalid bit length " + bits);
        }
        BigInteger p = BigInteger.probablePrime(bits, new SecureRandom());
        BigInteger q = BigInteger.ONE;
        while (!q.isProbablePrime(CERTAINTY)) {
            p = nextPrime(p);
            q = p.multiply(TWO).add(BigInteger.ONE);
        }
        sophieNumber = p;
        prime = q;
    }


    private static BigInteger nextPrime(BigInteger prime) {
        if (prime.mod(TWO).equals(BigInteger.ZERO)) {
            prime = prime.add(BigInteger.ONE);
        }
        prime = prime.add(TWO);

        while (!prime.isProbablePrime(CERTAINTY)) {
            prime = prime.add(TWO);
        }
        return prime;
    }

    public static SafePrime randomPrime(int bits) {
        return new SafePrime(bits);
    }

    public BigInteger getSophieNumber() {
        return sophieNumber;
    }

    public BigInteger getPrime() {
        return prime;
    }
}
