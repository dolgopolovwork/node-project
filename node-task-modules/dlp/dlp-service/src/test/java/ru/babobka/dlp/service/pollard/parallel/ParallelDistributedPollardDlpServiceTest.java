package ru.babobka.dlp.service.pollard.parallel;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import ru.babobka.dlp.model.dist.DlpTaskDist;
import ru.babobka.dlp.model.dist.PollardDistResult;
import ru.babobka.dlp.service.dist.DlpDistService;
import ru.babobka.dlp.service.collision.CollisionService;
import ru.babobka.dlp.service.pollard.parallel.dist.ParallelDistributedPollardDlpService;
import ru.babobka.nodeutils.container.Container;
import ru.babobka.nodeutils.key.UtilKey;
import ru.babobka.nodeutils.math.Fp;
import ru.babobka.nodeutils.thread.ThreadPoolService;

import java.math.BigInteger;

import static org.junit.Assert.assertEquals;

/**
 * Created by 123 on 08.07.2018.
 */
public class ParallelDistributedPollardDlpServiceTest {
    private ParallelDistributedPollardDlpService dlpService;

    @Before
    public void setUp() {
        Container.getInstance().put(UtilKey.SERVICE_THREAD_POOL, ThreadPoolService.createDaemonPool(Runtime.getRuntime().availableProcessors()));
        Container.getInstance().put(new PrimeDistinguishable());
        dlpService = new ParallelDistributedPollardDlpService(Runtime.getRuntime().availableProcessors());
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
            DlpTaskDist dlpTask = new DlpTaskDist(gen, y, Integer.MAX_VALUE);
            assertEquals(y, gen.pow(dlpService.execute(dlpTask).getExp()));
        }
    }

    @Test
    public void testDlpCollisionBasedMaxLoops() {
        int intMod = 659;
        BigInteger mod = BigInteger.valueOf(intMod);
        Fp gen = new Fp(BigInteger.valueOf(2L), mod);
        for (int i = 1; i < intMod; i++) {
            Fp y = new Fp(BigInteger.valueOf(i), mod);
            DlpTaskDist dlpTask = new DlpTaskDist(gen, y, Integer.MAX_VALUE);
            assertEquals(y, gen.pow(dlp(dlpTask)));
        }
    }

    @Test
    public void testDlpCollisionBasedTenLoops() {
        int intMod = 659;
        BigInteger mod = BigInteger.valueOf(intMod);
        Fp gen = new Fp(BigInteger.valueOf(2L), mod);
        for (int i = 1; i < intMod; i++) {
            Fp y = new Fp(BigInteger.valueOf(i), mod);
            DlpTaskDist dlpTask = new DlpTaskDist(gen, y, 10);
            assertEquals(y, gen.pow(dlp(dlpTask)));
        }
    }

    @Test
    public void testDlpCollisionBasedOneLoop() {
        int intMod = 659;
        BigInteger mod = BigInteger.valueOf(intMod);
        Fp gen = new Fp(BigInteger.valueOf(2L), mod);
        for (int i = 1; i < intMod; i++) {
            Fp y = new Fp(BigInteger.valueOf(i), mod);
            DlpTaskDist dlpTask = new DlpTaskDist(gen, y, 1);
            assertEquals(y, gen.pow(dlp(dlpTask)));
        }
    }

    @Test
    public void testDlpCollisionBased() {
        int intMod = 659;
        BigInteger mod = BigInteger.valueOf(intMod);
        Fp gen = new Fp(BigInteger.valueOf(2L), mod);
        for (int i = 1; i < intMod; i++) {
            Fp y = new Fp(BigInteger.valueOf(i), mod);
            DlpTaskDist dlpTask = new DlpTaskDist(gen, y, 1);
            assertEquals(y, gen.pow(dlp(dlpTask)));
        }
    }

    private BigInteger dlp(DlpTaskDist dlpTaskDist) {
        return CollisionService.dlp(dlpTaskDist, new DlpDistService() {
            @Override
            protected PollardDistResult dlpImpl(DlpTaskDist task) {
                return dlpService.execute(dlpTaskDist);
            }
        });
    }
}