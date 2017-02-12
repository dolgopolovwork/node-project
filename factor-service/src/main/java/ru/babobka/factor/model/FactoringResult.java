package ru.babobka.factor.model;

import java.math.BigInteger;

/**
 * Created by dolgopolov.a on 27.11.15.
 */
public class FactoringResult {

    private final BigInteger factor;

    private final EllipticCurveProjective ellipticCurveProjective;

    public FactoringResult(BigInteger factor, EllipticCurveProjective ellipticCurveProjective) {
	this.factor = factor;
	this.ellipticCurveProjective = ellipticCurveProjective;
    }

    public BigInteger getFactor() {
	return factor;
    }

    public EllipticCurveProjective getEllipticCurveProjective() {
	return ellipticCurveProjective;
    }

    @Override
    public String toString() {
	return "FactoringResult{" + "factor=" + factor + ", ellipticCurveProjective=" + ellipticCurveProjective + '}';
    }
}
