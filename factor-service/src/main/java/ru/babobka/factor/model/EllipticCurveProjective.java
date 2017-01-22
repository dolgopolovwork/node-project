package ru.babobka.factor.model;

import ru.babobka.factor.util.MathUtil;

import java.math.BigInteger;
import java.util.Random;

/**
 * Created by dolgopolov.a on 17.11.15.
 */
public class EllipticCurveProjective {
	private static final BigInteger TWO = BigInteger.valueOf(2);

	private static final BigInteger THREE = BigInteger.valueOf(3);

	private static final BigInteger FOUR = BigInteger.valueOf(4);

	private static final BigInteger EIGHT = BigInteger.valueOf(8);

	private final BigInteger x;

	private final BigInteger y;

	private final BigInteger z;

	private final BigInteger a;

	private final BigInteger b;

	private final BigInteger n;

	private EllipticCurveProjective(BigInteger x, BigInteger y, BigInteger z, BigInteger a, BigInteger b,
			BigInteger n) {
		super();
		this.x = x;
		this.y = y;
		this.z = z;
		this.a = a;
		this.b = b;
		this.n = n;
	}

	private EllipticCurveProjective(int x, int y, int z, int a, int b, int n) {
		this.x = BigInteger.valueOf(x);
		this.y = BigInteger.valueOf(y);
		this.a = BigInteger.valueOf(a);
		this.b = BigInteger.valueOf(b);
		this.n = BigInteger.valueOf(n);
		this.z = BigInteger.valueOf(z);
	}

	public static EllipticCurveProjective dummyCurve() {

		return new EllipticCurveProjective(0, 0, 0, 0, 0, 1);
	}

	public static EllipticCurveProjective generateRandomCurve(BigInteger n) {
		BigInteger x = BigInteger.probablePrime(n.bitLength(), new Random()).mod(n);
		BigInteger y = BigInteger.probablePrime(n.bitLength() - 1, new Random()).mod(n);
		BigInteger a = BigInteger.probablePrime(n.bitLength() - 1, new Random()).mod(n);
		// This b should fit Weierstrass equation y^2=x^3+ax+b, where
		// b=y^2-x^3-ax
		BigInteger b = (y.modPow(TWO, n).subtract(x.modPow(THREE, n)).subtract(a.multiply(x))).mod(n);
		return new EllipticCurveProjective(x, y, BigInteger.ONE, a, b, n);
	}

	public EllipticCurveProjective doublePoint() {
		/*
		 * if (Y == 0) return POINT_AT_INFINITY
		 */
		if (y.equals(BigInteger.ZERO)) {
			return getInfinityPoint();
		}
		// W = a*Z^2 + 3*X^2
		BigInteger w = a.multiply(z.modPow(TWO, n)).add(THREE.multiply(x.modPow(TWO, n)));
		// S = Y*Z
		BigInteger s = y.multiply(z).mod(n);
		// B = X*Y*S
		BigInteger b = x.multiply(y).mod(n).multiply(s).mod(n);
		// H = W^2 - 8*B
		BigInteger h = w.modPow(TWO, n).subtract(EIGHT.multiply(b)).mod(n);
		// X' = 2*H*S
		BigInteger x3 = TWO.multiply(h).mod(n).multiply(s).mod(n);
		// Y' = W*(4*B - H) - 8*Y^2*S^2
		BigInteger y3 = w.multiply(FOUR.multiply(b).subtract(h).mod(n)).mod(n)
				.subtract(EIGHT.multiply(y.modPow(TWO, n)).multiply(s.modPow(TWO, n)).mod(n)).mod(n);
		// Z' = 8*S^3
		BigInteger z3 = EIGHT.multiply(s.modPow(THREE, n)).mod(n);
		return new EllipticCurveProjective(x3, y3, z3, a, this.b, n);
	}

	public EllipticCurveProjective add(EllipticCurveProjective ec) {
		/*
		 * if (ec.equals(this)) { return this.doublePoint(); }
		 */
		// U1 = Y2*Z1
		BigInteger u1 = ec.getY().multiply(z).mod(n);
		// U2 = Y1*Z2
		BigInteger u2 = y.multiply(ec.getZ()).mod(n);
		// V1 = X2*Z1
		BigInteger v1 = ec.getX().multiply(z).mod(n);
		// V2 = X1*Z2
		BigInteger v2 = x.multiply(ec.getZ()).mod(n);
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
		BigInteger u = u1.subtract(u2).mod(n);
		// V = V1 - V2
		BigInteger v = v1.subtract(v2).mod(n);
		// W = Z1*Z2
		BigInteger w = z.multiply(ec.getZ()).mod(n);
		// A = U^2*W - V^3 - 2*V^2*V2
		BigInteger A = u.modPow(TWO, n).multiply(w).mod(n).subtract(v.modPow(THREE, n)).mod(n)
				.subtract(TWO.multiply(v.modPow(TWO, n)).multiply(v2)).mod(n);
		// X3 = V*A
		BigInteger x3 = v.multiply(A).mod(n);
		// U*(V^2*V2 - A) - V^3*U2
		BigInteger y3 = u.multiply(v.modPow(TWO, n).multiply(v2).mod(n).subtract(A))
				.subtract(v.modPow(THREE, n).multiply(u2)).mod(n);
		// Z3 = V^3*W
		BigInteger z3 = v.modPow(THREE, n).multiply(w).mod(n);
		return new EllipticCurveProjective(x3, y3, z3, a, b, n);
	}

	public EllipticCurveProjective getInfinityPoint() {
		return new EllipticCurveProjective(BigInteger.ZERO, BigInteger.ONE, BigInteger.ZERO, a, b, n);
	}

	public BigInteger getX() {
		return x;
	}

	public BigInteger getY() {
		return y;
	}

	public BigInteger getZ() {
		return z;
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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((a == null) ? 0 : a.hashCode());
		result = prime * result + ((b == null) ? 0 : b.hashCode());
		result = prime * result + ((n == null) ? 0 : n.hashCode());
		result = prime * result + ((x == null) ? 0 : x.hashCode());
		result = prime * result + ((y == null) ? 0 : y.hashCode());
		result = prime * result + ((z == null) ? 0 : z.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		EllipticCurveProjective other = (EllipticCurveProjective) obj;
		if (a == null) {
			if (other.a != null)
				return false;
		} else if (!a.equals(other.a))
			return false;
		if (b == null) {
			if (other.b != null)
				return false;
		} else if (!b.equals(other.b))
			return false;
		if (n == null) {
			if (other.n != null)
				return false;
		} else if (!n.equals(other.n))
			return false;
		if (n != null) {
			BigInteger x1 = x.multiply(z.modInverse(n)).mod(n);
			BigInteger x2 = other.getX().multiply(other.getZ().modInverse(n)).mod(n);

			if (!x1.equals(x2)) {
				return false;
			}
		}
		if (n != null) {
			BigInteger y1 = y.multiply(z.modInverse(n)).mod(n);
			BigInteger y2 = other.getY().multiply(other.getZ().modInverse(n)).mod(n);
			if (!y1.equals(y2)) {
				return false;
			}
		}

		return true;
	}

	@Override
	public String toString() {
		return "EllipticCurveProjective [x=" + x + ", y=" + y + ", z=" + z + ", a=" + a + ", b=" + b + ", n=" + n + "]";
	}

	public boolean isInfinityPoint() {
		if (x.equals(BigInteger.ZERO) && y.equals(BigInteger.ONE) && z.equals(BigInteger.ZERO)) {
			return true;
		}
		return false;
	}

	public EllipticCurveProjective multiply(long times) {

		EllipticCurveProjective total = null;
		EllipticCurveProjective subTotal;
		boolean[] booleans = MathUtil.toBinary(times);
		EllipticCurveProjective[] doubledCurves = null;
		if (!MathUtil.isPowerOfTwo(times)) {
			int maxPower = booleans.length - 1;
			doubledCurves = new EllipticCurveProjective[maxPower];
		}
		for (int i = 0; i < booleans.length; i++) {
			if (booleans[i]) {
				subTotal = this.copy();
				int k = booleans.length - 1 - i;
				if (k > 0) {
					if (doubledCurves != null && doubledCurves[k - 1] != null) {
						subTotal = doubledCurves[k - 1];
					} else {
						for (int j = 0; j < k; j++) {
							subTotal = subTotal.doublePoint();
							if (doubledCurves != null)
								doubledCurves[j] = subTotal;
						}
					}
				}
				if (total == null) {
					total = subTotal;
				} else {
					total = total.add(subTotal);
				}
			}
		}
		return total;
	}

	public EllipticCurveProjective copy() {
		return new EllipticCurveProjective(x, y, z, a, b, n);
	}

}