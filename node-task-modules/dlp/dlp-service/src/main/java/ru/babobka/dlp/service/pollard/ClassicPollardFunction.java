package ru.babobka.dlp.service.pollard;

import ru.babobka.dlp.model.regular.PollardEntity;
import ru.babobka.dlp.service.pollard.func.PollardFunction;
import ru.babobka.nodeutils.math.Fp;

import java.math.BigInteger;

/**
 * Created by 123 on 11.01.2018.
 */
public class ClassicPollardFunction extends PollardFunction {
    private static final BigInteger TWO = BigInteger.valueOf(2);
    private static final BigInteger THREE = BigInteger.valueOf(3);
    private final BigInteger thirdOfP;
    private final BigInteger twoThirdsOfP;

    public ClassicPollardFunction(BigInteger p) {
        if (p == null) throw new IllegalArgumentException("p is null");
        thirdOfP = p.divide(THREE);
        twoThirdsOfP = thirdOfP.multiply(TWO);
    }

    @Override
    protected PollardEntity mixImpl(PollardEntity input) {
        Fp x = input.getCollision();
        if (x.getNumber().compareTo(thirdOfP) < 0) {
            return new PollardEntity(x.mult(input.getG()), input.getY(), input.getG(), input.getValExp(), input.incGenExp());
        } else if (x.getNumber().compareTo(twoThirdsOfP) < 0) {
            return new PollardEntity(x.square(), input.getY(), input.getG(), input.doubleValExp(), input.doubleGenExp());
        } else {
            return new PollardEntity(x.mult(input.getY()), input.getY(), input.getG(), input.incValExp(), input.getGenExp());
        }
    }

    PollardEntity doubleMix(PollardEntity input) {
        return mix(mix(input));
    }
}
