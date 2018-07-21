package ru.babobka.dlp.service.pollard.parallel;

import ru.babobka.nodeutils.math.Fp;

/**
 * Created by 123 on 16.01.2018.
 */
public abstract class Distinguishable {

   public boolean isDistinguishable(Fp point) {
        if (point == null) {
            throw new IllegalArgumentException("point is null");
        }
        return isDistinguishableImpl(point);
    }

    protected abstract boolean isDistinguishableImpl(Fp point);
}
