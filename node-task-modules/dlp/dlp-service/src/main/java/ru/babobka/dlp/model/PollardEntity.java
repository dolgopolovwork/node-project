package ru.babobka.dlp.model;

import ru.babobka.nodeutils.math.Fp;
import ru.babobka.nodeutils.util.ArrayUtil;

import java.math.BigInteger;
import java.util.Random;

/**
 * Created by 123 on 11.01.2018.
 */
public class PollardEntity {
    private static final BigInteger TWO = BigInteger.valueOf(2L);
    private final Fp x;
    private final Fp g;
    private final Fp a;
    private final Fp valExp;
    private final Fp genExp;

    public PollardEntity(Fp x, Fp a, Fp g, Fp valExp, Fp genExp) {
        ArrayUtil.validateNonNull(x, g, a, valExp, genExp);
        if (!valExp.getMod().equals(genExp.getMod())) {
            throw new IllegalArgumentException("exponents must be from the same field");
        } else if (!g.getMod().equals(a.getMod())) {
            throw new IllegalArgumentException("both generator and searched value must be from the same field");
        } else if (!x.getMod().equals(g.getMod())) {
            throw new IllegalArgumentException("both x and generator must be from the same field");
        }
        this.x = x;
        this.g = g;
        this.a = a;
        this.valExp = valExp;
        this.genExp = genExp;
    }

    public static PollardEntity initRandom(Fp a, Fp g) {
        Random random = new Random();
        BigInteger orderMod = g.getMod().subtract(BigInteger.ONE);
        Fp valueExp = new Fp(new BigInteger(random.nextInt(orderMod.bitLength()), random), orderMod);
        Fp genExp = new Fp(new BigInteger(random.nextInt(orderMod.bitLength()), random), orderMod);
        return new PollardEntity(a.pow(valueExp.getNumber()).mult(g.pow(genExp.getNumber())), a, g, valueExp, genExp);
    }

    public boolean isCollision(PollardEntity doubleResult) {
        if (doubleResult == null || doubleResult == this) {
            return false;
        }
        return this.getX().equals(doubleResult.getX()) && !this.equals(doubleResult);
    }

    public Fp incValExp() {
        return valExp.inc();
    }

    public Fp incGenExp() {
        return genExp.inc();
    }

    public Fp doubleGenExp() {
        return genExp.mult(TWO);
    }

    public Fp doubleValExp() {
        return valExp.mult(TWO);
    }

    public Fp getX() {
        return x;
    }

    public Fp getG() {
        return g;
    }

    public Fp getA() {
        return a;
    }

    public Fp getValExp() {
        return valExp;
    }

    public Fp getGenExp() {
        return genExp;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PollardEntity that = (PollardEntity) o;

        if (!x.equals(that.x)) return false;
        if (!g.equals(that.g)) return false;
        if (!a.equals(that.a)) return false;
        if (!valExp.equals(that.valExp)) return false;
        return genExp.equals(that.genExp);
    }

    @Override
    public int hashCode() {
        int result = x.hashCode();
        result = 31 * result + g.hashCode();
        result = 31 * result + a.hashCode();
        result = 31 * result + valExp.hashCode();
        result = 31 * result + genExp.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "PollardEntity{" +
                "x=" + x +
                ", g=" + g +
                ", a=" + a +
                ", valExp=" + valExp +
                ", genExp=" + genExp +
                '}';
    }

}
