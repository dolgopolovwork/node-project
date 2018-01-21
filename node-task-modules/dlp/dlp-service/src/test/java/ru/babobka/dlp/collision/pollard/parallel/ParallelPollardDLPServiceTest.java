package ru.babobka.dlp.collision.pollard.parallel;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import ru.babobka.dlp.DlpTask;
import ru.babobka.nodeutils.container.Container;
import ru.babobka.nodeutils.math.Fp;

import java.math.BigInteger;

import static org.junit.Assert.assertEquals;

/**
 * Created by 123 on 20.01.2018.
 */
public class ParallelPollardDLPServiceTest {

    private ParallelPollardDLPService dlpService;

    @Before
    public void setUp() {
        Container.getInstance().put(new PrimeDistinguishable());
        dlpService = new ParallelPollardDLPService(Runtime.getRuntime().availableProcessors());
    }

    @After
    public void tearDown() {
        Container.getInstance().clear();
    }

    @Test
    public void testDlp() {
        int intMod = 659;
        BigInteger mod = BigInteger.valueOf(intMod);
        Fp gen = new Fp(BigInteger.valueOf(2L), mod);
        for (int i = 1; i < intMod; i++) {
            Fp y = new Fp(BigInteger.valueOf(i), mod);
            DlpTask dlpTask = new DlpTask(gen, y);
            assertEquals(y, gen.pow(dlpService.dlp(dlpTask)));
            dlpService.resetDone();
        }
        dlpService.stop();
    }
}
