package ru.babobka.dlp;

import ru.babobka.dlp.model.DlpTask;
import ru.babobka.dlp.service.DlpService;
import ru.babobka.dlp.service.pollard.ClassicPollardDlpService;
import ru.babobka.dlp.service.pollard.PollardCollisionService;
import ru.babobka.dlp.service.pollard.parallel.ParallelPollardDLPServiceTestable;
import ru.babobka.dlp.service.pollard.parallel.PrimeDistinguishable;
import ru.babobka.nodeutils.container.Container;
import ru.babobka.nodeutils.math.Fp;
import ru.babobka.nodeutils.util.MathUtil;

import java.math.BigInteger;

/**
 * Created by 123 on 08.01.2018.
 */
public class Benchmark {

    private static final int LITTLE_MOD = 659;
    private static final int LITTLE_GEN = 2;
    private static final int TESTS = 100;

    static {
        Container.getInstance().put(new PollardCollisionService());
        Container.getInstance().put(new PrimeDistinguishable());
    }

    private static final ClassicPollardDlpService pollardDLPService = new ClassicPollardDlpService();
    private static final ParallelPollardDLPServiceTestable parallelPollardDLPService = new ParallelPollardDLPServiceTestable();

    public static void main(String[] args) {
        warmUp();
        System.out.println("Done warm up");
        for (int i = 32; i <= 64; i++) {
            MathUtil.SafePrime safePrime = MathUtil.getSafePrime(i);
            BigInteger gen = MathUtil.getGenerator(safePrime);
            long intGen = gen.longValue();
            long intPrime = safePrime.getPrime().longValue();
            System.out.println(safePrime.getPrime().bitLength() + " bits");
            printBenchMark(intGen, intPrime, pollardDLPService);
            printBenchMark(intGen, intPrime, parallelPollardDLPService);
        }
        parallelPollardDLPService.stop();
    }

    private static void warmUp() {
        for (int i = 0; i < 20; i++) {
            someExecution(LITTLE_GEN, LITTLE_MOD, pollardDLPService);
            someExecution(LITTLE_GEN, LITTLE_MOD, parallelPollardDLPService);
        }
    }

    private static void printBenchMark(long gen, long mod, DlpService dlpService) {
        System.out.println(dlpService.getClass().getSimpleName() + "\t" + (benchmark(gen, mod, dlpService) / (double) TESTS) + "mls");

    }

    private static long benchmark(long gen, long mod, DlpService dlpService) {
        long oldTime = System.currentTimeMillis();
        someExecution(gen, mod, dlpService);
        return System.currentTimeMillis() - oldTime;
    }

    private static void someExecution(long gen, long mod, DlpService dlpService) {
        BigInteger bigMod = BigInteger.valueOf(mod);
        Fp bigGen = new Fp(BigInteger.valueOf(gen), bigMod);
        for (int i = 1; i < Math.min(mod, TESTS); i++) {
            Fp y = new Fp(BigInteger.valueOf(i), bigMod);
            DlpTask dlpTask = new DlpTask(bigGen, y);
            dlpService.dlp(dlpTask);
            if (dlpService instanceof ParallelPollardDLPServiceTestable) {
                ((ParallelPollardDLPServiceTestable) dlpService).reset();
            }
        }
    }
    /*
Last benchmark
32 bits
ClassicPollardDlpService	167.69mls
ParallelPollardDlpService	47.57mls
33 bits
ClassicPollardDlpService	279.69mls
ParallelPollardDlpService	101.68mls
34 bits
ClassicPollardDlpService	359.87mls
ParallelPollardDlpService	122.74mls
35 bits
ClassicPollardDlpService	495.23mls
ParallelPollardDlpService	254.34mls
36 bits
ClassicPollardDlpService	861.38mls
ParallelPollardDlpService	431.17mls
37 bits
ClassicPollardDlpService	958.89mls
ParallelPollardDlpService	472.49mls
38 bits
ClassicPollardDlpService	2023.48mls
ParallelPollardDlpService	1098.59mls
*/
}
