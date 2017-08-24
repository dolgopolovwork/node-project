package ru.babobka.factor.model.ec.multprovider;

import ru.babobka.factor.model.ec.EllipticCurvePoint;

/**
 * Created by 123 on 01.10.2017.
 */
public class DummyMultiplicationProvider extends MultiplicationProvider {

    @Override
    protected EllipticCurvePoint multImpl(EllipticCurvePoint point, long times) {
        EllipticCurvePoint result = point.copy();
        for (int i = 1; i < times; i++) {
            result = result.add(point);
        }
        return result;
    }
}
