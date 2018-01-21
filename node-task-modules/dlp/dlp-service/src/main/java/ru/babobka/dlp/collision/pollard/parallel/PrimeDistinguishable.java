package ru.babobka.dlp.collision.pollard.parallel;

import ru.babobka.nodeutils.math.Fp;
import ru.babobka.nodeutils.util.MathUtil;

/**
 * Created by 123 on 16.01.2018.
 */
public class PrimeDistinguishable implements Distinguishable {

    @Override
    public boolean isDistinguishable(Fp point) {
        return MathUtil.isPrime(point.getNumber().hashCode());
    }

}
