package ru.babobka.factor.model.ec.multprovider;

import ru.babobka.factor.model.ec.EllipticCurvePoint;

/**
 * Created by 123 on 01.10.2017.
 */
public abstract class MultiplicationProvider {

    public EllipticCurvePoint mult(EllipticCurvePoint point, long times) {
        if (point == null) {
            throw new IllegalArgumentException("point is null");
        } else if (times < 0) {
            throw new IllegalArgumentException("can not multiple by negative numbers");
        } else if (point.isInfinityPoint()) {
            return point;
        } else if (times == 0L) {
            return point.getInfinityPoint();
        } else {
            return multImpl(point, times);
        }
    }

    protected abstract EllipticCurvePoint multImpl(EllipticCurvePoint point, long times);
}
