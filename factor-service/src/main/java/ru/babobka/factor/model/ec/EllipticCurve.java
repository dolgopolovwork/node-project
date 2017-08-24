package ru.babobka.factor.model.ec;


import ru.babobka.nodeutils.math.Zp;
import ru.babobka.nodeutils.util.ArrayUtil;

import java.io.Serializable;
import java.math.BigInteger;

/**
 * Created by 123 on 28.09.2017.
 */
public class EllipticCurve implements Serializable {
    private static final long serialVersionUID = 8957418697062925442L;
    private final Zp a;
    private final Zp b;
    private final BigInteger n;
    private final Zp minusSixteen;
    private final Zp four;
    private final Zp twentySeven;

    EllipticCurve(Zp a, Zp b, BigInteger n) {
        if (ArrayUtil.isNull(a, b, n)) {
            throw new IllegalArgumentException("all the arguments must be non null");
        } else if (n.compareTo(BigInteger.ONE) < 1) {
            throw new IllegalArgumentException(n + " is too small to be mod");
        }
        this.a = a;
        this.b = b;
        this.n = n;
        minusSixteen = new Zp(BigInteger.valueOf(-16L), n);
        four = new Zp(BigInteger.valueOf(4L), n);
        twentySeven = new Zp(BigInteger.valueOf(27L), n);
    }

    public Zp getDiscriminant() {
        return minusSixteen.mult(four.mult(a.qube()).add(twentySeven.mult(b.square())));
    }

    public Zp getA() {
        return a;
    }

    public Zp getB() {
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
