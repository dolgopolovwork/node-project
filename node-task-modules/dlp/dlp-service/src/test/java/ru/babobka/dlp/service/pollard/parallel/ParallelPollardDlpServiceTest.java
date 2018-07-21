package ru.babobka.dlp.service.pollard.parallel;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import ru.babobka.dlp.model.regular.DlpTask;
import ru.babobka.dlp.service.pollard.parallel.regular.ParallelPollardDlpService;
import ru.babobka.nodeutils.container.Container;
import ru.babobka.nodeutils.key.UtilKey;
import ru.babobka.nodeutils.math.Fp;
import ru.babobka.nodeutils.thread.ThreadPoolService;

import java.math.BigInteger;

import static org.junit.Assert.assertEquals;

/**
 * Created by 123 on 20.01.2018.
 */
public class ParallelPollardDlpServiceTest {

    private ParallelPollardDlpService dlpService;

    @Before
    public void setUp() {
        Container.getInstance().put(UtilKey.SERVICE_THREAD_POOL, ThreadPoolService.createDaemonPool(Runtime.getRuntime().availableProcessors()));
        Container.getInstance().put(new PrimeDistinguishable());
        dlpService = new ParallelPollardDlpService(Runtime.getRuntime().availableProcessors());
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
            assertEquals(y, gen.pow(dlpService.execute(dlpTask)));
        }
    }
}
