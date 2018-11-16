package ru.babobka.factor.model.ec.multprovider;

import ru.babobka.factor.model.ec.EllipticCurvePoint;
import ru.babobka.nodeutils.util.MathUtil;

/**
 * Created by 123 on 20.10.2018.
 */
public class FastMultiplicationProvider extends MultiplicationProvider {
    private final MultiplicationProvider binaryMultiplicationProvider = new BinaryMultiplicationProvider();
    private final MultiplicationProvider ternaryMultiplicationProvider = new TernaryMultiplicationProvider();

    @Override
    protected EllipticCurvePoint multImpl(EllipticCurvePoint point, long times) {
        if (MathUtil.makesSenseToUseTernary(times)) {
            return ternaryMultiplicationProvider.mult(point, times);
        }
        return binaryMultiplicationProvider.mult(point, times);
    }
}
