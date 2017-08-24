package ru.babobka.nodeutils.math;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.Random;

/**
 * Created by 123 on 23.09.2017.
 */
public class Zp implements Serializable {
    private static final BigInteger TWO = BigInteger.valueOf(2L);
    private static final BigInteger THREE = BigInteger.valueOf(3L);
    private static final long serialVersionUID = -1025489560763994359L;
    private final BigInteger number;
    private final BigInteger mod;


    public Zp(BigInteger number, BigInteger mod) {
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

    public static Zp random(BigInteger mod, int minBits) {
        if (mod == null) {
            throw new IllegalArgumentException("mod is null");
        } else if (mod.compareTo(BigInteger.ONE) <= 0) {
            throw new IllegalArgumentException("invalid mod " + mod);
        } else if (minBits < 0) {
            throw new IllegalArgumentException("minBits must be positive number");
        }
        return new Zp(new BigInteger(Math.max(new Random().nextInt(mod.bitLength()), minBits), new Random()), mod);
    }

    public static Zp random(BigInteger mod) {
        return random(mod, 0);
    }

    public static Zp addNeutral(BigInteger mod) {
        return new Zp(BigInteger.ZERO, mod);
    }

    public static Zp multNeutral(BigInteger mod) {
        return new Zp(BigInteger.ONE, mod);
    }

    public Zp add(Zp zp) {
        validateMod(zp);
        if (zp.isAddNeutral()) {
            return this;
        }
        return new Zp(zp.number.add(number), mod);
    }

    public Zp subtract(Zp zp) {
        validateMod(zp);
        if (zp.isAddNeutral()) {
            return this;
        }
        return new Zp(number.subtract(zp.getNumber()), mod);
    }

    public Zp mult(Zp zp) {
        validateMod(zp);
        if (zp.isMultNeutral()) {
            return this;
        }
        return new Zp(number.multiply(zp.number), mod);
    }

    public Zp mult(BigInteger n) {
        if (n == null) {
            throw new IllegalArgumentException("can not mult null times");
        } else if (n.equals(BigInteger.ONE)) {
            return this;
        }
        return new Zp(number.multiply(n), mod);
    }

    public Zp divide(Zp zp) {
        validateMod(zp);
        if (zp.getNumber().equals(BigInteger.ZERO)) {
            throw new IllegalArgumentException("Can not divide by zero");
        } else if (zp.isMultNeutral()) {
            return this;
        }
        return mult(zp.getNumber().modInverse(mod));
    }

    public Zp negate() {
        return new Zp(number.negate(), mod);
    }

    public Zp pow(BigInteger power) {
        if (power == null) {
            throw new IllegalArgumentException("power is null");
        } else if (power.compareTo(BigInteger.ZERO) < 0) {
            throw new IllegalArgumentException("can not power to negative number");
        } else if (power.equals(BigInteger.ONE) || isMultNeutral()) {
            return this;
        }
        return new Zp(number.modPow(power, mod), mod);
    }

    public Zp square() {
        return pow(TWO);
    }

    public Zp qube() {
        return pow(THREE);
    }

    public boolean isAddNeutral() {
        return this.getNumber().equals(BigInteger.ZERO);
    }

    boolean isMultNeutral() {
        return this.getNumber().equals(BigInteger.ONE);
    }

    private boolean isSameMod(Zp zp) {
        return zp.mod.equals(mod);
    }

    private void validateMod(Zp zp) {
        if (zp == null) {
            throw new IllegalArgumentException("zp is null");
        } else if (!isSameMod(zp)) {
            throw new IllegalArgumentException("zp has different mod value");
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Zp zp = (Zp) o;

        if (mod != null ? !mod.equals(zp.mod) : zp.mod != null) return false;
        return number != null ? number.equals(zp.number) : zp.number == null;
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

    @Override
    public String toString() {
        return "Zp{" +
                "number=" + number +
                ", mod=" + mod +
                '}';
    }
}
