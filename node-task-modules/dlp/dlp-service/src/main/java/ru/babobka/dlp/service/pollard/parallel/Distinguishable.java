package ru.babobka.dlp.service.pollard.parallel;

import lombok.NonNull;
import ru.babobka.nodeutils.math.Fp;

/**
 * Created by 123 on 16.01.2018.
 */
public abstract class Distinguishable {

    boolean isDistinguishable(@NonNull Fp point) {
        return isDistinguishableImpl(point);
    }

    protected abstract boolean isDistinguishableImpl(Fp point);
}
