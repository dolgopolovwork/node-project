package ru.babobka.factor.model.ec.multprovider;

import ru.babobka.factor.model.ec.EllipticCurvePoint;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 123 on 01.10.2017.
 */
public class FastMultiplicationProvider extends MultiplicationProvider {
    @Override
    protected EllipticCurvePoint multImpl(EllipticCurvePoint point, long times) {
        char[] binaryMult = Long.toBinaryString(times).toCharArray();
        int biggestExp = binaryMult.length - 1;
        List<EllipticCurvePoint> expPoints = new ArrayList<>();
        expPoints.add(point);
        for (int i = 1; i <= biggestExp; i++) {
            expPoints.add(expPoints.get(i - 1).doublePoint());
        }
        List<EllipticCurvePoint> pointsToAdd = new ArrayList<>();
        for (int i = 0; i < binaryMult.length; i++) {
            if (binaryMult[i] == '1') {
                pointsToAdd.add(expPoints.get(binaryMult.length - i - 1));
            }
        }
        return addPoints(pointsToAdd);
    }


    private EllipticCurvePoint addPoints(List<EllipticCurvePoint> points) {
        EllipticCurvePoint result = points.get(0);
        for (int i = 1; i < points.size(); i++) {
            result = result.add(points.get(i));
        }
        return result;
    }

}
