package ru.babobka.factor.model.ec;


import ru.babobka.nodeutils.math.Fp;

import java.io.Serializable;
import java.math.BigInteger;

/**
 * Created by 123 on 28.09.2017.
 */
public class EllipticCurve implements Serializable {
    private static final long serialVersionUID = 8957418697062925442L;
    private final Fp a;
    private final Fp b;
    private final BigInteger n;
    private final Fp minusSixteen;
    private final Fp four;
    private final Fp twentySeven;

    EllipticCurve(Fp a, Fp b, BigInteger n) {
        if (a == null) {
            throw new IllegalArgumentException("a is null");
        } else if (b == null) {
            throw new IllegalArgumentException("b is null");
        } else if (n == null) {
            throw new IllegalArgumentException("n is null");
        } else if (n.compareTo(BigInteger.ONE) < 1) {
            throw new IllegalArgumentException(n + " is too small to be mod");
        }
        this.a = a;
        this.b = b;
        this.n = n;
        minusSixteen = new Fp(BigInteger.valueOf(-16L), n);
        four = new Fp(BigInteger.valueOf(4L), n);
        twentySeven = new Fp(BigInteger.valueOf(27L), n);
    }

    public Fp getDiscriminant() {
        return minusSixteen.mult(four.mult(a.qube()).add(twentySeven.mult(b.square())));
    }

    public Fp getA() {
        return a;
    }

    public Fp getB() {
        return b;
    }

    public BigInteger getN() {
        return n;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        EllipticCurve curve = (EllipticCurve) o;

        if (!a.equals(curve.a)) return false;
        if (!b.equals(curve.b)) return false;
        return n.equals(curve.n);
    }

    @Override
    public int hashCode() {
        int result = a.hashCode();
        result = 31 * result + b.hashCode();
        result = 31 * result + n.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "EllipticCurve{" +
                "a=" + a +
                ", b=" + b +
                ", n=" + n +
                '}';
    }
}
