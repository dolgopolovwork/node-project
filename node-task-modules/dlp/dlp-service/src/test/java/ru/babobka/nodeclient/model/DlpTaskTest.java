package ru.babobka.nodeclient.model;

import org.junit.Test;
import ru.babobka.dlp.model.DlpTask;
import ru.babobka.nodeutils.math.Fp;

import java.math.BigInteger;

/**
 * Created by 123 on 26.01.2018.
 */
public class DlpTaskTest {

    @Test(expected = IllegalArgumentException.class)
    public void testNullGen() {
        new DlpTask(null, Fp.random(BigInteger.TEN));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNullValue() {
        new DlpTask(Fp.random(BigInteger.TEN), null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testDifferentMods() {
        Fp gen = new Fp(BigInteger.valueOf(2), BigInteger.valueOf(3));
        Fp val = new Fp(BigInteger.valueOf(2), BigInteger.valueOf(4));
        new DlpTask(gen, val);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testZeroDlp() {
        Fp gen = new Fp(BigInteger.valueOf(2), BigInteger.valueOf(3));
        Fp val = new Fp(BigInteger.valueOf(0), BigInteger.valueOf(4));
        new DlpTask(gen, val);
    }

}
