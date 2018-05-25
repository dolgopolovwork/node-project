package ru.babobka.nodeutils.math;

import ru.babobka.nodeutils.util.MathUtil;

import java.io.Serializable;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Random;

/**
 * Created by 123 on 04.05.2018.
 */
public class SafePrime implements Serializable {
    private static final long serialVersionUID = 8763199470347175919L;
    private static final BigInteger TWO = BigInteger.valueOf(2L);
    private final BigInteger sophieNumber;
    private final BigInteger prime;

    public SafePrime(BigInteger sophieNumber, BigInteger prime) {
        if (sophieNumber == null) {
            throw new IllegalArgumentException("sophieNumber is null");
        } else if (prime == null) {
            throw new IllegalArgumentException("prime is null");
        } else if (!prime.equals(sophieNumber.multiply(TWO).add(BigInteger.ONE))) {
            throw new IllegalArgumentException("invalid safe parameters");
        } else if (!MathUtil.isPrime(prime)) {
            throw new IllegalArgumentException(prime + " is not prime");
        } else if (!MathUtil.isPrime(sophieNumber)) {
            throw new IllegalArgumentException(sophieNumber + " is not prime");

        }
        this.sophieNumber = sophieNumber;
        this.prime = prime;
    }

    public static SafePrime random(int bits) {
        return random(bits, new Random());
    }

    public static SafePrime secureRandom(int bits) {
        return random(bits, new SecureRandom());
    }

    private static SafePrime random(int bits, Random random) {
        if (bits < 2) {
            throw new IllegalArgumentException("There must be at least 2 bits to construct safe prime");
        }
        BigInteger sophieNumber = null;
        BigInteger prime = BigInteger.ONE;
        while (!MathUtil.isPrime(prime)) {
            sophieNumber = BigInteger.probablePrime(bits, random);
            prime = sophieNumber.multiply(TWO).add(BigInteger.ONE);
        }
        return new SafePrime(sophieNumber, prime);
    }

    public BigInteger getSophieNumber() {
        return sophieNumber;
    }

    public BigInteger getPrime() {
        return prime;
    }

    @Override
    public String toString() {
        return "SafePrime{" +
                "sophieNumber=" + sophieNumber +
                ", prime=" + prime +
                '}';
    }
}