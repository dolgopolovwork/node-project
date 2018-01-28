package ru.babobka.dlp.service.pollard.parallel;

import ru.babobka.nodeutils.math.Fp;
import ru.babobka.nodeutils.util.MathUtil;

/**
 * Created by 123 on 16.01.2018.
 */
public class PrimeDistinguishable extends Distinguishable {

    @Override
    protected boolean isDistinguishableImpl(Fp point) {
        return MathUtil.isPrime(point.getNumber().hashCode() % 1000);
    }

}
