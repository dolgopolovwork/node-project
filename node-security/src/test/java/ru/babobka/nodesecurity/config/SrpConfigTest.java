package ru.babobka.nodesecurity.config;

import org.junit.Test;
import ru.babobka.nodeutils.math.Fp;
import ru.babobka.nodeutils.util.HashUtil;

import java.math.BigInteger;

import static org.junit.Assert.assertEquals;

/**
 * Created by 123 on 04.05.2018.
 */
public class SrpConfigTest {

    @Test(expected = IllegalArgumentException.class)
    public void testNullG() {
        new SrpConfig(null, 2);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testZeroChallengeBytes() {
        new SrpConfig(new Fp(BigInteger.ONE, BigInteger.TEN), 0);
    }

    @Test
    public void testGetK() {
        Fp g = new Fp(BigInteger.ONE, BigInteger.TEN);
        BigInteger numK = new BigInteger(HashUtil.sha2(g.getMod().toByteArray(), g.getNumber().toByteArray()));
        Fp k = new Fp(numK, g.getMod());
        SrpConfig srpConfig = new SrpConfig(g, 2);
        assertEquals(k, srpConfig.getK());
    }
}
