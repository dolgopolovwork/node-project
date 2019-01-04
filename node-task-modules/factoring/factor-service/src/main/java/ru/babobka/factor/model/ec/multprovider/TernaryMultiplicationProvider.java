package ru.babobka.factor.model.ec.multprovider;

import ru.babobka.factor.model.ec.EllipticCurvePoint;
import ru.babobka.nodeutils.util.MathUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 123 on 19.10.2018.
 */

public class TernaryMultiplicationProvider extends MultiplicationProvider {

    @Override
    protected EllipticCurvePoint multImpl(EllipticCurvePoint point, long times) {
        List<Integer> ternary = MathUtil.toTernary(times);
        int biggestExp = ternary.size() - 1;
        List<EllipticCurvePoint> expPoints = new ArrayList<>();
        expPoints.add(point);
        for (int i = 1; i <= biggestExp; i++) {
            expPoints.add(expPoints.get(i - 1).doublePoint());
        }
        List<EllipticCurvePoint> pointsToAdd = new ArrayList<>();
        int trit;
        for (int i = 0; i < ternary.size(); i++) {
            trit = ternary.get(i);
            if (trit == 1) {
                pointsToAdd.add(expPoints.get(ternary.size() - i - 1));
            } else if (trit == -1) {
                pointsToAdd.add(expPoints.get(ternary.size() - i - 1).negate());
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
