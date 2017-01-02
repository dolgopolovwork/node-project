package ru.babobka.nodeserials;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.Random;

/**
 * Created by dolgopolov.a on 29.10.15.
 */
public class RSA implements Serializable {

	private static final long serialVersionUID = 1L;
	private final PrivateKey privateKey;
	private final PublicKey publicKey;
	private static final String NULL_MESSAGE_ERROR = "Can not encrypt null message";
	private static final int MIN_BITS = 256;

	public RSA(int bits) {
		if (bits >= MIN_BITS) {
			BigInteger p = BigInteger.probablePrime(bits, new Random());
			BigInteger q = BigInteger.probablePrime(bits, new Random());
			BigInteger n = p.multiply(q);
			BigInteger f = p.subtract(BigInteger.ONE).multiply(q.subtract(BigInteger.ONE));
			BigInteger e = BigInteger.probablePrime(f.bitLength() - 1, new Random());
			while (!e.gcd(f).equals(BigInteger.ONE)) {
				e = BigInteger.probablePrime(f.bitLength() - 1, new Random());
			}
			BigInteger d = e.modInverse(f);
			privateKey = new PrivateKey(n, d);
			publicKey = new PublicKey(n, e);
		} else
			throw new IllegalArgumentException("Key can not be shorter than " + MIN_BITS + " bits");
	}

	public RSA(PrivateKey privateKey, PublicKey publicKey) {
		this.privateKey = privateKey;
		this.publicKey = publicKey;
	}

	public BigInteger encrypt(String m) {
		if (m != null) {
			return encrypt(stringToBigInteger(m));
		} else {
			throw new NullPointerException(NULL_MESSAGE_ERROR);
		}
	}

	private BigInteger encrypt(BigInteger m) {
		if (publicKey != null)
			return m.abs().modPow(publicKey.getE(), publicKey.getN());
		throw new NullPointerException(NULL_MESSAGE_ERROR);
	}

	public static BigInteger stringToBigInteger(String m) {
		return new BigInteger(MathUtil.sha2(m)).abs();
	}

	public static BigInteger bytesToHashedBigInteger(byte[] bytes) {
		return new BigInteger(bytes).abs();
	}

	public BigInteger decrypt(BigInteger c) {
		if (privateKey != null)
			return c.abs().modPow(privateKey.getD(), privateKey.getN());
		throw new NullPointerException(NULL_MESSAGE_ERROR);
	}

	public PrivateKey getPrivateKey() {
		return privateKey;
	}

	public PublicKey getPublicKey() {
		return publicKey;
	}

	public static void main(String[] args) {
		
	}

}
