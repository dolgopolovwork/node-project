package ru.babobka.factor.runnable;

import java.math.BigInteger;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;

import ru.babobka.factor.model.EllipticCurveProjective;
import ru.babobka.factor.model.FactoringResult;
import ru.babobka.factor.util.MathUtil;

/**
 * Created by dolgopolov.a on 24.11.15.
 */
public class EllipticCurveProjectiveFactorCallable implements Callable<FactoringResult> {

    private final BigInteger n;

    private static final int MIN_B = 10000;

    private final ExecutorService threadPool;

    private long B;

    public EllipticCurveProjectiveFactorCallable(ExecutorService threadPool, BigInteger n) {
	this.threadPool = threadPool;
	this.n = n;

	this.B = MathUtil.sqrtBig(MathUtil.sqrtBig(MathUtil.sqrtBig(n))).longValue();
	if (B < MIN_B) {
	    B = MIN_B;
	}
    }

    private FactoringResult factor() throws InterruptedException {

	EllipticCurveProjective p = EllipticCurveProjective.generateRandomCurve(n);
	BigInteger g = p.getN().gcd(p.getX());
	if (g.compareTo(BigInteger.ONE) >= 1 && g.compareTo(p.getN()) < 0) {
	    return new FactoringResult(g, p);
	}

	EllipticCurveProjective beginCurve = p.copy();
	for (long i = 3; i < B; i += 2) {
	    if (MathUtil.isPrime(i)) {
		long r = MathUtil.log(i, B);
		for (long j = 0; j < r; j++) {
		    if (Thread.currentThread().isInterrupted()) {
			throw new InterruptedException();
		    }
		    p = p.multiply(i);
		    if (p.isInfinityPoint()) {
			p = EllipticCurveProjective.generateRandomCurve(n);
			beginCurve = p.copy();
			break;
		    }
		    g = p.getN().gcd(p.getX());
		    if (g.compareTo(BigInteger.ONE) >= 1 && g.compareTo(p.getN()) < 0) {
			threadPool.shutdownNow();
			return new FactoringResult(g, beginCurve);
		    }

		}
	    }
	}
	return null;

    }

    @Override
    public FactoringResult call() {

	FactoringResult result = null;
	for (int i = 0; i < n.bitLength() * 2; i++) {
	    try {
		result = factor();
		if (result != null) {
		    break;
		}
	    } catch (InterruptedException e) {
		Thread.currentThread().interrupt();
		break;
	    }
	}
	return result;
    }
}
