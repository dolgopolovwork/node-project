package ru.babobka.nodeutils.math;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.Random;

/**
 * Created by 123 on 23.09.2017.
 */
public class Fp implements Serializable {
    private static final BigInteger TWO = BigInteger.valueOf(2L);
    private static final BigInteger THREE = BigInteger.valueOf(3L);
    private static final long serialVersionUID = -1025489560763994359L;
    private final BigInteger number;
    private final BigInteger mod;


    public Fp(BigInteger number, BigInteger mod) {
        if (mod == null) {
            throw new IllegalArgumentException("mod is null");
        } else if (mod.compareTo(BigInteger.ONE) <= 0) {
            throw new IllegalArgumentException("invalid mod " + mod);
        } else if (number == null) {
            throw new IllegalArgumentException("number is null");
        }
        this.mod = mod;
        this.number = number.mod(mod);
    }

    public static Fp random(BigInteger mod, int minBits) {
        if (mod == null) {
            throw new IllegalArgumentException("mod is null");
        } else if (mod.compareTo(BigInteger.ONE) <= 0) {
            throw new IllegalArgumentException("invalid mod " + mod);
        } else if (minBits < 0) {
            throw new IllegalArgumentException("minBits must be positive number");
        }
        return new Fp(new BigInteger(Math.max(new Random().nextInt(mod.bitLength()), minBits), new Random()), mod);
    }

    public static Fp random(BigInteger mod) {
        return random(mod, 0);
    }

    public static Fp addNeutral(BigInteger mod) {
        return new Fp(BigInteger.ZERO, mod);
    }

    public static Fp multNeutral(BigInteger mod) {
        return new Fp(BigInteger.ONE, mod);
    }

    public Fp add(Fp fp) {
        validateMod(fp);
        if (fp.isAddNeutral()) {
            return this;
        }
        return new Fp(fp.number.add(number), mod);
    }

    public Fp subtract(Fp fp) {
        validateMod(fp);
        if (fp.isAddNeutral()) {
            return this;
        }
        return new Fp(number.subtract(fp.getNumber()), mod);
    }

    public Fp mult(Fp fp) {
        validateMod(fp);
        if (fp.isMultNeutral()) {
            return this;
        }
        return new Fp(number.multiply(fp.number), mod);
    }

    public Fp mult(BigInteger n) {
        if (n == null) {
            throw new IllegalArgumentException("can not mult null times");
        } else if (n.equals(BigInteger.ONE)) {
            return this;
        }
        return new Fp(number.multiply(n), mod);
    }

    public Fp divide(Fp fp) {
        validateMod(fp);
        if (fp.getNumber().equals(BigInteger.ZERO)) {
            throw new IllegalArgumentException("Can not divide by zero");
        } else if (fp.isMultNeutral()) {
            return this;
        }
        return mult(fp.getNumber().modInverse(mod));
    }

    public Fp negate() {
        return new Fp(number.negate(), mod);
    }


    public Fp pow(long power) {
        return pow(BigInteger.valueOf(power));
    }

    public Fp pow(BigInteger power) {
        if (power == null) {
            throw new IllegalArgumentException("power is null");
        } else if (power.compareTo(BigInteger.ZERO) < 0) {
            throw new IllegalArgumentException("can not power to negative number");
        } else if (power.equals(BigInteger.ONE) || isMultNeutral()) {
            return this;
        }
        return new Fp(number.modPow(power, mod), mod);
    }

    public Fp square() {
        return pow(TWO);
    }

    public Fp qube() {
        return pow(THREE);
    }

    public Fp dec() {
        return this.add(Fp.multNeutral(mod).negate());
    }

    public Fp inc() {
        return this.add(Fp.multNeutral(mod));
    }

    public boolean isAddNeutral() {
        return this.getNumber().equals(BigInteger.ZERO);
    }

    boolean isMultNeutral() {
        return this.getNumber().equals(BigInteger.ONE);
    }

    private boolean isSameMod(Fp fp) {
        return fp.mod.equals(mod);
    }

    private void validateMod(Fp fp) {
        if (fp == null) {
            throw new IllegalArgumentException("fp is null");
        } else if (!isSameMod(fp)) {
            throw new IllegalArgumentException("fp has different mod value");
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Fp fp = (Fp) o;

        if (mod != null ? !mod.equals(fp.mod) : fp.mod != null) return false;
        return number != null ? number.equals(fp.number) : fp.number == null;
    }

    @Override
    public int hashCode() {
        int result = mod != null ? mod.hashCode() : 0;
        result = 31 * result + (number != null ? number.hashCode() : 0);
        return result;
    }

    public BigInteger getNumber() {
        return number;
    }

    public BigInteger getMod() {
        return mod;
    }

    @Override
    public String toString() {
        return "Fp{" +
                "number=" + number +
                ", mod=" + mod +
                '}';
    }
}
