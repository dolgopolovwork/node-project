package ru.babobka.dlp.model.regular;

import ru.babobka.nodeutils.math.Fp;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.Random;

/**
 * Created by 123 on 11.01.2018.
 */
public class PollardEntity implements Serializable{
    private static final BigInteger TWO = BigInteger.valueOf(2L);
    private static final long serialVersionUID = 7938335877089609608L;
    private final Fp collision;
    private final Fp g;
    private final Fp y;
    private final Fp valExp;
    private final Fp genExp;

    public PollardEntity(Fp collision, Fp y, Fp g, Fp valExp, Fp genExp) {
        if (collision == null) {
            throw new IllegalArgumentException("collision is null");
        } else if (y == null) {
            throw new IllegalArgumentException("y is null");
        } else if (g == null) {
            throw new IllegalArgumentException("g is null");
        } else if (valExp == null) {
            throw new IllegalArgumentException("valExp is null");
        } else if (genExp == null) {
            throw new IllegalArgumentException("genExp is null");
        } else if (!valExp.getMod().equals(genExp.getMod())) {
            throw new IllegalArgumentException("exponents must be from the same field");
        } else if (!g.getMod().equals(y.getMod())) {
            throw new IllegalArgumentException("both generator and searched value must be from the same field");
        } else if (!collision.getMod().equals(g.getMod())) {
            throw new IllegalArgumentException("both collision and generator must be from the same field");
        }
        this.collision = collision;
        this.g = g;
        this.y = y;
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
        return collision.equals(doubleResult.getCollision()) && !this.equals(doubleResult);
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

    public Fp getCollision() {
        return collision;
    }

    public Fp getG() {
        return g;
    }

    public Fp getY() {
        return y;
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

        if (!collision.equals(that.collision)) return false;
        if (!g.equals(that.g)) return false;
        if (!y.equals(that.y)) return false;
        if (!valExp.equals(that.valExp)) return false;
        return genExp.equals(that.genExp);
    }

    @Override
    public int hashCode() {
        int result = collision.hashCode();
        result = 31 * result + g.hashCode();
        result = 31 * result + y.hashCode();
        result = 31 * result + valExp.hashCode();
        result = 31 * result + genExp.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "PollardEntity{" +
                "collision=" + collision +
                ", g=" + g +
                ", y=" + y +
                ", valExp=" + valExp +
                ", genExp=" + genExp +
                '}';
    }

}
