package ru.babobka.factor.model;

import ru.babobka.factor.model.ec.EllipticCurvePoint;

import java.io.Serializable;
import java.math.BigInteger;

/**
 * Created by dolgopolov.a on 27.11.15.
 */
public class FactoringResult implements Serializable {

    private static final long serialVersionUID = -3645274013690775497L;
    private final BigInteger factor;
    private final EllipticCurvePoint ellipticCurvePoint;

    public FactoringResult(BigInteger factor, EllipticCurvePoint ellipticCurvePoint) {
        if (factor == null) {
            throw new IllegalArgumentException("factor is null");
        }
        this.factor = factor;
        this.ellipticCurvePoint = ellipticCurvePoint;
    }

    public BigInteger getFactor() {
        return factor;
    }

    public EllipticCurvePoint getEllipticCurvePoint() {
        return ellipticCurvePoint;
    }

    @Override
    public String toString() {
        return "FactoringResult{" + "factor=" + factor + ", ellipticCurvePoint=" + ellipticCurvePoint + '}';
    }
}
