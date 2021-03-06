package ru.babobka.dlp.service.pollard;

import lombok.NonNull;
import ru.babobka.dlp.model.PollardEntity;
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

    public ClassicPollardFunction(@NonNull BigInteger p) {
        thirdOfP = p.divide(THREE);
        twoThirdsOfP = thirdOfP.multiply(TWO);
    }

    @Override
    protected PollardEntity mixImpl(PollardEntity input) {
        Fp x = input.getX();
        if (x.getNumber().compareTo(thirdOfP) < 0) {
            return new PollardEntity(x.mult(input.getG()), input.getA(), input.getG(), input.getValExp(), input.incGenExp());
        } else if (x.getNumber().compareTo(twoThirdsOfP) < 0) {
            return new PollardEntity(x.square(), input.getA(), input.getG(), input.doubleValExp(), input.doubleGenExp());
        } else {
            return new PollardEntity(x.mult(input.getA()), input.getA(), input.getG(), input.incValExp(), input.getGenExp());
        }
    }

    PollardEntity doubleMix(PollardEntity input) {
        return mix(mix(input));
    }
}
