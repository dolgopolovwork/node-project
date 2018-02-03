package ru.babobka.nodeclient.model;

import org.junit.Test;
import ru.babobka.dlp.model.PollardEntity;
import ru.babobka.nodeutils.math.Fp;

import java.math.BigInteger;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Created by 123 on 26.01.2018.
 */
public class PollardEntityTest {

    @Test(expected = IllegalArgumentException.class)
    public void testDifferentExpMods() {
        Fp x = new Fp(BigInteger.ONE, BigInteger.TEN);
        Fp a = new Fp(BigInteger.ONE, BigInteger.TEN);
        Fp g = new Fp(BigInteger.ONE, BigInteger.TEN);
        Fp valExp = new Fp(BigInteger.ONE, BigInteger.valueOf(2));
        Fp genExp = new Fp(BigInteger.ONE, BigInteger.valueOf(3));
        new PollardEntity(x, a, g, valExp, genExp);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testDifferentValuesMods() {
        Fp x = new Fp(BigInteger.ONE, BigInteger.TEN);
        Fp a = new Fp(BigInteger.ONE, BigInteger.valueOf(2));
        Fp g = new Fp(BigInteger.ONE, BigInteger.valueOf(3));
        Fp valExp = new Fp(BigInteger.ONE, BigInteger.TEN);
        Fp genExp = new Fp(BigInteger.ONE, BigInteger.TEN);
        new PollardEntity(x, a, g, valExp, genExp);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testDifferentValueMod() {
        Fp x = new Fp(BigInteger.ONE, BigInteger.valueOf(2));
        Fp a = new Fp(BigInteger.ONE, BigInteger.TEN);
        Fp g = new Fp(BigInteger.ONE, BigInteger.TEN);
        Fp valExp = new Fp(BigInteger.ONE, BigInteger.TEN);
        Fp genExp = new Fp(BigInteger.ONE, BigInteger.TEN);
        new PollardEntity(x, a, g, valExp, genExp);
    }

    @Test
    public void testIsCollisionNull() {
        Fp a = new Fp(BigInteger.ONE, BigInteger.TEN);
        Fp b = new Fp(BigInteger.ONE, BigInteger.TEN);
        PollardEntity pollardEntity = PollardEntity.initRandom(a, b);
        assertFalse(pollardEntity.isCollision(null));
    }

    @Test
    public void testIsCollisionSameReference() {
        Fp x = new Fp(BigInteger.ONE, BigInteger.TEN);
        Fp a = new Fp(BigInteger.ONE, BigInteger.TEN);
        Fp g = new Fp(BigInteger.ONE, BigInteger.TEN);
        Fp valExp = new Fp(BigInteger.ONE, BigInteger.TEN);
        Fp genExp = new Fp(BigInteger.ONE, BigInteger.TEN);
        PollardEntity pollardEntity = new PollardEntity(x, a, g, valExp, genExp);
        assertFalse(pollardEntity.isCollision(pollardEntity));
    }

    @Test
    public void testIsCollisionSameValues() {
        Fp x = new Fp(BigInteger.ONE, BigInteger.TEN);
        Fp a = new Fp(BigInteger.ONE, BigInteger.TEN);
        Fp g = new Fp(BigInteger.ONE, BigInteger.TEN);
        Fp valExp = new Fp(BigInteger.ONE, BigInteger.TEN);
        Fp genExp = new Fp(BigInteger.ONE, BigInteger.TEN);
        PollardEntity pollardEntity = new PollardEntity(x, a, g, valExp, genExp);
        assertFalse(pollardEntity.isCollision(new PollardEntity(x, a, g, valExp, genExp)));
    }

    @Test
    public void testIsCollisionDifferentValues() {
        Fp x = new Fp(BigInteger.ONE, BigInteger.TEN);
        Fp a = new Fp(BigInteger.ONE, BigInteger.TEN);
        Fp g = new Fp(BigInteger.ONE, BigInteger.TEN);
        Fp valExp = new Fp(BigInteger.ONE, BigInteger.TEN);
        Fp genExp = new Fp(BigInteger.ONE, BigInteger.TEN);
        PollardEntity pollardEntity = new PollardEntity(x, a, g, valExp, genExp);
        assertFalse(pollardEntity.isCollision(new PollardEntity(x.negate(), a, g, valExp, genExp)));
    }

    @Test
    public void testIsCollision() {
        Fp x = new Fp(BigInteger.ONE, BigInteger.TEN);
        Fp a = new Fp(BigInteger.ONE, BigInteger.TEN);
        Fp g = new Fp(BigInteger.ONE, BigInteger.TEN);
        Fp valExp = new Fp(BigInteger.ONE, BigInteger.TEN);
        Fp genExp = new Fp(BigInteger.ONE, BigInteger.TEN);
        PollardEntity pollardEntity = new PollardEntity(x, a, g, valExp, genExp);
        assertTrue(pollardEntity.isCollision(new PollardEntity(x, a.negate(), g, valExp, genExp)));
    }
}
