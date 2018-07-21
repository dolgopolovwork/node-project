package ru.babobka.dlp.service.pollard;

import org.junit.Test;
import ru.babobka.dlp.model.regular.DlpTask;
import ru.babobka.nodeutils.func.Pair;
import ru.babobka.dlp.model.regular.PollardEntity;
import ru.babobka.nodeutils.math.Fp;

import java.math.BigInteger;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

/**
 * Created by 123 on 27.01.2018.
 */
public class PollardEqualitySolverTest {

    @Test(expected = IllegalArgumentException.class)
    public void testSolveNullDlpTask() {
        PollardEqualitySolver.solve(null, mock(Pair.class));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSolveNullPair() {
        PollardEqualitySolver.solve(mock(DlpTask.class), null);
    }

    @Test
    public void testSolve() {
        BigInteger mod = BigInteger.valueOf(48611);
        BigInteger orderMod = mod.subtract(BigInteger.ONE);
        Fp gen = new Fp(BigInteger.valueOf(19), mod);
        Fp value = new Fp(BigInteger.valueOf(24717), mod);
        PollardEntity pollardEntity1 = new PollardEntity(new Fp(BigInteger.valueOf(33252), mod), value, gen, new Fp(BigInteger.valueOf(20155), orderMod), new Fp(BigInteger.valueOf(12133), orderMod));
        PollardEntity pollardEntity2 = new PollardEntity(new Fp(BigInteger.valueOf(33252), mod), value, gen, new Fp(BigInteger.valueOf(46665), orderMod), new Fp(BigInteger.valueOf(47273), orderMod));
        Pair<PollardEntity> collision = new Pair<>(pollardEntity1, pollardEntity2);
        BigInteger solution = PollardEqualitySolver.solve(new DlpTask(gen, value), collision);
        assertEquals(solution, BigInteger.valueOf(37869));
    }

}
