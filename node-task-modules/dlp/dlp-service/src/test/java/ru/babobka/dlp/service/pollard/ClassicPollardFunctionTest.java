package ru.babobka.dlp.service.pollard;

import org.junit.Test;
import ru.babobka.dlp.model.regular.PollardEntity;
import ru.babobka.nodeutils.math.Fp;

import java.math.BigInteger;

import static org.junit.Assert.assertEquals;

/**
 * Created by 123 on 13.01.2018.
 */
public class ClassicPollardFunctionTest {

    @Test
    public void testDoubleMix() {
        BigInteger mod = BigInteger.valueOf(659);
        Fp a = new Fp(BigInteger.valueOf(4), mod);
        Fp gen = new Fp(BigInteger.valueOf(2), mod);
        PollardEntity pollardEntity = PollardEntity.initRandom(a, gen);
        ClassicPollardFunction classicPollardFunction = new ClassicPollardFunction(mod);
        assertEquals(classicPollardFunction.mix(classicPollardFunction.mix(pollardEntity)), classicPollardFunction.doubleMix(pollardEntity));
    }
}
