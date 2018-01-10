package ru.babobka.dlp.collision.mem;

import ru.babobka.nodeutils.math.Fp;
import ru.babobka.nodeutils.util.ArrayUtil;
import ru.babobka.nodeutils.util.MathUtil;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by 123 on 06.01.2018.
 */
public class CollisionStorage {

    private final Fp gen;
    private final Fp y;
    private final Map<Fp, Long> firstList = new HashMap<>();
    private final Map<Fp, Long> secondList = new HashMap<>();
    private static final BigInteger TWO = BigInteger.valueOf(2L);


    public CollisionStorage(Fp gen, Fp y) {
        if (ArrayUtil.isNull(gen, y)) {
            throw new IllegalArgumentException("Both generator and y must be set");
        }
        this.gen = gen;
        this.y = y;
    }

    public Pair<Long> produceCollision() {
        long longMod = gen.getMod().longValue();
        long border = 2 * MathUtil.sqrtBig(gen.getMod()).longValue();
        long previousExp = 0;
        Fp gxy;
        Fp gx = Fp.multNeutral(gen.getMod());
        for (long attempt = 1; attempt <= border; attempt++) {
            long exp = ThreadLocalRandom.current().nextLong(longMod - 2) + 1;
            if (previousExp * 2 == exp) {
                //g^2x=(g^x)^2
                gx = gx.pow(TWO);
            } else if (previousExp > 0 && exp > previousExp) {
                //g^(x+z)=(g^x)*(g^z)
                long expDelta = exp - previousExp;
                gx = gx.mult(gen.pow(expDelta));
            } else {
                gx = gen.pow(exp);
            }
            gxy = gx.mult(y);
            firstList.put(gx, exp);
            secondList.put(gxy, exp);
            previousExp = exp;
            Pair<Long> collision = findCollision(gx, gxy, exp);
            if (collision != null) {
                clear();
                return collision;
            }
        }
        clear();
        return null;
    }

    private void clear() {
        firstList.clear();
        secondList.clear();
    }

    private Pair<Long> findCollision(Fp gx, Fp gxy, long lastExp) {
        Long firstExp = firstList.get(gxy);
        Long secondExp = secondList.get(gx);
        if (firstExp != null) {
            return new Pair<>(firstExp, lastExp);
        } else if (secondExp != null) {
            return new Pair<>(lastExp, secondExp);
        }
        return null;
    }
}
