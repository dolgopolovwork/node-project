package ru.babobka.factor.model;

import ru.babobka.factor.exception.InfinityPointException;
import ru.babobka.factor.util.MathUtil;

import java.math.BigInteger;
import java.util.Random;

/**
 * Created by dolgopolov.a on 17.11.15.
 */
public class EllipticCurveProjective {

	public static final BigInteger TWO = BigInteger.valueOf(2);

	public static final BigInteger THREE = BigInteger.valueOf(3);

	public static final BigInteger FOUR = BigInteger.valueOf(4);

	public static final BigInteger EIGHT = BigInteger.valueOf(8);

	private final BigInteger x;

	private final BigInteger y;

	private final BigInteger a;

	private final BigInteger b;

	private final BigInteger n;

	private final BigInteger z;

	private EllipticCurveProjective(BigInteger x, BigInteger y, BigInteger z, BigInteger a, BigInteger b,
			BigInteger n) {
		this.x = x;
		this.y = y;
		this.a = a;
		this.b = b;
		this.n = n;
		this.z = z;
	}

	public EllipticCurveProjective(int x, int y, int z, int a, int b, int n) {
		this.x = BigInteger.valueOf(x);
		this.y = BigInteger.valueOf(y);
		this.a = BigInteger.valueOf(a);
		this.b = BigInteger.valueOf(b);
		this.n = BigInteger.valueOf(n);
		this.z = BigInteger.valueOf(z);
	}

	public static EllipticCurveProjective generateRandomCurve(BigInteger n) {
		BigInteger x = BigInteger.probablePrime(n.bitLength(), new Random()).mod(n);
		BigInteger y = BigInteger.probablePrime(n.bitLength(), new Random()).mod(n);
		BigInteger a = BigInteger.probablePrime(n.bitLength(), new Random()).mod(n);
		BigInteger b = (y.pow(2).subtract(x.pow(3)).subtract(a.multiply(x))).mod(n);
		if (!(y.pow(2).subtract(x.pow(3)).subtract(a.multiply(x))).mod(n).equals(b)) {
			return generateRandomCurve(n);
		}
		BigInteger g = n.gcd(FOUR.multiply(a.pow(3)).add(BigInteger.valueOf(27).multiply(b.pow(2))));
		if (g.equals(n)) {
			return generateRandomCurve(n);
		}

		return new EllipticCurveProjective(x, y, BigInteger.ONE, a, b, n);
	}

	public static EllipticCurveProjective dummyCurve() {

		return new EllipticCurveProjective(0, 0, 0, 0, 0, 1);
	}

	public EllipticCurveProjective doublePoint() {

		if (y.equals(BigInteger.ZERO)) {
			return getInfinityPoint();
		}
		BigInteger w, s, b, h, x3, y3, z3;
		if (a.equals(THREE.negate())) {
			w = THREE.multiply(x.add(y)).multiply(x.subtract(z));
		} else {
			w = a.multiply(z.pow(2)).add(THREE.multiply(x.pow(2)));
		}
		s = y.multiply(z);
		b = x.multiply(y).multiply(s);
		h = w.pow(2).subtract(EIGHT.multiply(b));
		x3 = TWO.multiply(h).multiply(s);
		y3 = w.multiply(FOUR.multiply(b).subtract(h)).subtract(EIGHT.multiply(y.pow(2)).multiply(s.pow(2)));
		z3 = EIGHT.multiply(s.pow(3));
		return new EllipticCurveProjective(x3.mod(n), y3.mod(n), z3.mod(n), this.a, this.b, this.n);
	}

	public EllipticCurveProjective add(EllipticCurveProjective ec) {

		BigInteger u1, u2, v1, v2, u, v, w, a, x3, y3, z3, x2, y2, z2, x1, y1, z1;
		z2 = ec.getZ();
		y2 = ec.getY();
		x2 = ec.getX();
		x1 = this.x;
		y1 = this.y;
		z1 = this.z;
		u1 = y2.multiply(z1);
		u2 = y1.multiply(z2);
		v1 = x2.multiply(z1);
		v2 = x1.multiply(z2);
		if (v1.equals(v2)) {
			if (!u1.equals(u2)) {
				return getInfinityPoint();
			} else {
				return ec.doublePoint();
			}
		}
		u = u1.subtract(u2);
		v = v1.subtract(v2);
		w = z1.multiply(z2);
		a = u.pow(2).multiply(w).subtract(v.pow(3)).subtract(TWO.multiply(v.pow(2)).multiply(v2));
		x3 = v.multiply(a);
		y3 = u.multiply(v.pow(2).multiply(v2).subtract(a)).subtract(v.pow(3).multiply(u2));
		z3 = v.pow(3).multiply(w);

		return new EllipticCurveProjective(x3.mod(n), y3.mod(n), z3.mod(n), this.a, this.b, this.n);
	}

	public EllipticCurveProjective multiply(EllipticCurveProjective ec, long times) throws InfinityPointException {

		EllipticCurveProjective total = null;
		EllipticCurveProjective subTotal;
		boolean[] booleans = MathUtil.toBinary(times);
		for (int i = 0; i < booleans.length; i++) {
			if (booleans[i]) {
				subTotal = ec.copy();
				int k = booleans.length - 1 - i;
				for (int j = 0; j < k; j++) {
					subTotal = subTotal.doublePoint();
				}
				if (total == null) {
					total = subTotal;
				} else {
					total = total.add(subTotal);
				}
			}
		}
		if (total != null && total.isInfinityPoint()) {
			throw new InfinityPointException();
		}
		return total;
	}

	public BigInteger getX() {
		return x;
	}

	public BigInteger getY() {
		return y;
	}

	public BigInteger getA() {
		return a;
	}

	public BigInteger getB() {
		return b;
	}

	public BigInteger getN() {
		return n;
	}

	public BigInteger getZ() {
		return z;
	}

	public boolean isInfinityPoint() {
		if (x.equals(BigInteger.ZERO) && y.equals(BigInteger.ONE) && z.equals(BigInteger.ZERO)) {
			return true;
		}
		return false;
	}

	@Override
	public String toString() {
		return "EllipticCurveProjective{" + "x=" + x + ", y=" + y + ", a=" + a + ", b=" + b + ", n=" + n + ", z=" + z
				+ '}';
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (!(o instanceof EllipticCurveProjective))
			return false;

		EllipticCurveProjective that = (EllipticCurveProjective) o;

		if (!a.equals(that.a))
			return false;
		if (!b.equals(that.b))
			return false;
		if (!n.equals(that.n))
			return false;
		if (!x.equals(that.x))
			return false;
		if (!y.equals(that.y))
			return false;
		if (!z.equals(that.z))
			return false;

		return true;
	}

	public EllipticCurveProjective getInfinityPoint() {
		return new EllipticCurveProjective(BigInteger.ZERO, BigInteger.ONE, BigInteger.ZERO, a, b, n);
	}

	public EllipticCurveProjective copy() {
		return new EllipticCurveProjective(x, y, z, a, b, n);
	}

	@Override
	public int hashCode() {
		int result = x.hashCode();
		result = 31 * result + y.hashCode();
		result = 31 * result + a.hashCode();
		result = 31 * result + b.hashCode();
		result = 31 * result + n.hashCode();
		result = 31 * result + z.hashCode();
		return result;
	}
}
