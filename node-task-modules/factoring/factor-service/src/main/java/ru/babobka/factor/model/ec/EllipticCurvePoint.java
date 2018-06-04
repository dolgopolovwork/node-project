package ru.babobka.factor.model.ec;

import ru.babobka.factor.model.ec.multprovider.MultiplicationProvider;
import ru.babobka.nodeutils.container.Container;
import ru.babobka.nodeutils.math.Fp;

import java.io.Serializable;
import java.math.BigInteger;

/**
 * Created by dolgopolov.a on 17.11.15.
 */
public class EllipticCurvePoint implements Serializable {
    private static final long serialVersionUID = 8436924745774422337L;
    private static final BigInteger TWO = BigInteger.valueOf(2);
    private static final BigInteger THREE = BigInteger.valueOf(3);
    private static final BigInteger FOUR = BigInteger.valueOf(4);
    private static final BigInteger EIGHT = BigInteger.valueOf(8);
    private static final MultiplicationProvider multiplicationProvider = Container.getInstance().get(MultiplicationProvider.class);
    private final Fp x;
    private final Fp y;
    private final Fp z;
    private final EllipticCurve curve;

    private EllipticCurvePoint(Fp x, Fp y, Fp z, EllipticCurve curve) {
        if (x == null) {
            throw new IllegalArgumentException("x is null");
        } else if (y == null) {
            throw new IllegalArgumentException("y is null");
        } else if (z == null) {
            throw new IllegalArgumentException("z is null");
        } else if (curve == null) {
            throw new IllegalArgumentException("curve is null");
        }
        this.x = x;
        this.y = y;
        this.z = z;
        this.curve = curve;
    }

    public static EllipticCurvePoint generateRandomPoint(BigInteger n) {
        EllipticCurvePoint ellipticCurvePoint = null;
        while (ellipticCurvePoint == null) {
            ellipticCurvePoint = createGoodRandomPoint(n);
        }
        return ellipticCurvePoint;
    }

    private static EllipticCurvePoint createGoodRandomPoint(BigInteger n) {
        int randomBits = Math.max(n.bitLength() - 1, n.bitLength() / 2);
        Fp x = Fp.random(n, randomBits);
        Fp y = Fp.random(n, randomBits);
        Fp a = Fp.random(n, randomBits);
        // This b must fit Weierstrass equation y^2=x^3+ax+b, where
        // b=y^2-x^3-ax
        Fp b = y.square().subtract(x.qube()).subtract(a.mult(x));
        EllipticCurve curve = new EllipticCurve(a, b, n);
        if (curve.getDiscriminant().isAddNeutral()) {
            //not good point
            return null;
        }
        return new EllipticCurvePoint(x, y, Fp.multNeutral(n), curve);
    }


    public EllipticCurvePoint doublePoint() {
    /*
     * if (Y == 0) return POINT_AT_INFINITY
	 */
        if (y.isAddNeutral()) {
            return getInfinityPoint();
        }
        // W = a*Z^2 + 3*X^2
        Fp w = curve.getA().mult(z.square()).add(x.square().mult(THREE));
        // S = Y*Z
        Fp s = y.mult(z);
        // B = X*Y*S
        Fp b = x.mult(y).mult(s);
        // H = W^2 - 8*B
        Fp h = w.square().subtract(b.mult(EIGHT));
        // X' = 2*H*S
        Fp x3 = h.mult(TWO).mult(s);
        // Y' = W*(4*B - H) - 8*Y^2*S^2
        Fp y3 = w.mult((b.mult(FOUR).subtract(h)))
                .subtract(y.square().mult(EIGHT).mult(s.square()));
        // Z' = 8*S^3
        Fp z3 = s.qube().mult(EIGHT);
        return new EllipticCurvePoint(x3, y3, z3, curve);
    }

    public EllipticCurvePoint add(EllipticCurvePoint ecp) {
        if (ecp.isInfinityPoint()) {
            return this;
        }
    /*
     * if (ec.equals(this)) { return this.doublePoint(); }
	 */
        // U1 = Y2*Z1
        Fp u1 = ecp.getY().mult(z);
        // U2 = Y1*Z2
        Fp u2 = y.mult(ecp.getZ());
        // V1 = X2*Z1
        Fp v1 = ecp.getX().mult(z);
        // V2 = X1*Z2
        Fp v2 = x.mult(ecp.getZ());
        // if (V1 == V2)
        if (v1.equals(v2)) {
            // if (U1 != U2)
            if (!u1.equals(u2)) {
                return getInfinityPoint();
            } else {
                // return POINT_DOUBLE(X1, Y1, Z1)
                return this.doublePoint();
            }
        }
        // U = U1 - U2
        Fp u = u1.subtract(u2);
        // V = V1 - V2
        Fp v = v1.subtract(v2);
        // W = Z1*Z2
        Fp w = z.mult(ecp.getZ());
        // A = U^2*W - V^3 - 2*V^2*V2
        Fp A = u.square().mult(w).subtract(v.qube())
                .subtract(v.square().mult(TWO).mult(v2));
        // X3 = V*A
        Fp x3 = v.mult(A);
        // U*(V^2*V2 - A) - V^3*U2
        Fp y3 = u.mult(v.square().mult(v2).subtract(A))
                .subtract(v.qube().mult(u2));
        // Z3 = V^3*W
        Fp z3 = v.qube().mult(w);
        return new EllipticCurvePoint(x3, y3, z3, curve);
    }

    public EllipticCurvePoint getInfinityPoint() {
        return new EllipticCurvePoint(Fp.addNeutral(curve.getN()), Fp.multNeutral(curve.getN()), Fp.addNeutral(curve.getN()), curve);
    }

    public EllipticCurvePoint negate() {
        return new EllipticCurvePoint(x, y.negate(), z, curve);
    }

    public boolean isInfinityPoint() {
        return x.equals(Fp.addNeutral(curve.getN())) && y.equals(Fp.multNeutral(curve.getN())) && z.equals(Fp.addNeutral(curve.getN()));
    }

    public EllipticCurvePoint mult(long times) {
        return multiplicationProvider.mult(this, times);
    }

    public EllipticCurve getCurve() {
        return curve;
    }

    public EllipticCurvePoint copy() {
        return new EllipticCurvePoint(x, y, z, curve);
    }

    public Fp getX() {
        return x;
    }

    public Fp getY() {
        return y;
    }

    public Fp getZ() {
        return z;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        EllipticCurvePoint point = (EllipticCurvePoint) o;
        if (!x.divide(z).equals(point.getX().divide(point.getZ()))) {
            return false;
        }
        if (!y.divide(z).equals(point.getY().divide(point.getZ()))) {
            return false;
        }
        return curve.equals(point.curve);
    }

    @Override
    public int hashCode() {
        int result = x.hashCode();
        result = 31 * result + y.hashCode();
        result = 31 * result + z.hashCode();
        result = 31 * result + curve.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "EllipticCurvePoint{" +
                "x=" + x +
                ", y=" + y +
                ", z=" + z +
                ", curve=" + curve +
                '}';
    }
}