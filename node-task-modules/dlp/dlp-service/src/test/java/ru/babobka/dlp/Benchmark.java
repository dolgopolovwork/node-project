package ru.babobka.dlp;

import ru.babobka.dlp.collision.CollisionDLPService;
import ru.babobka.dlp.collision.pollard.ClassicPollardDLPService;
import ru.babobka.dlp.collision.pollard.PollardCollisionService;
import ru.babobka.dlp.collision.pollard.parallel.PrimeDistinguishable;
import ru.babobka.dlp.collision.pollard.parallel.ParallelPollardDLPService;
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
    private static final int TESTS = 1000;

    static {
        Container.getInstance().put(new PollardCollisionService());
        Container.getInstance().put(new PrimeDistinguishable());
    }

    private static final CollisionDLPService collisionDLPService = new CollisionDLPService();
    private static final ClassicPollardDLPService pollardDLPService = new ClassicPollardDLPService();
    private static final ParallelPollardDLPService parallelPollardDLPService = new ParallelPollardDLPService(Runtime.getRuntime().availableProcessors());

    public static void main(String[] args) {
        warmUp();
        System.out.println("Done warm up");
        for (int i = 16; i <= 25; i++) {
            MathUtil.SafePrime safePrime = MathUtil.getSafePrime(i);
            BigInteger gen = MathUtil.getGenerator(safePrime);
            int intGen = gen.intValue();
            int intPrime = safePrime.getPrime().intValue();
            System.out.println(safePrime.getPrime().bitLength() + " bits");
            printBenchMark(intGen, intPrime, collisionDLPService);
            printBenchMark(intGen, intPrime, pollardDLPService);
            printBenchMark(intGen, intPrime, parallelPollardDLPService);
        }
        parallelPollardDLPService.stop();
    }

    private static void warmUp() {
        for (int i = 0; i < 10; i++) {
            someExecution(LITTLE_GEN, LITTLE_MOD, collisionDLPService);
            someExecution(LITTLE_GEN, LITTLE_MOD, pollardDLPService);
            someExecution(LITTLE_GEN, LITTLE_MOD, parallelPollardDLPService);
        }
    }

    private static void printBenchMark(int gen, int mod, DlpService dlpService) {
        System.out.println(dlpService.getClass().getSimpleName() + "\t" + (benchmark(gen, mod, dlpService) / (double) TESTS) + "mls");

    }

    private static long benchmark(int gen, int mod, DlpService dlpService) {
        long oldTime = System.currentTimeMillis();
        someExecution(gen, mod, dlpService);
        return System.currentTimeMillis() - oldTime;
    }

    private static void someExecution(int gen, int mod, DlpService dlpService) {
        BigInteger bigMod = BigInteger.valueOf(mod);
        Fp bigGen = new Fp(BigInteger.valueOf(gen), bigMod);
        for (int i = 1; i < Math.min(mod, TESTS); i++) {
            Fp y = new Fp(BigInteger.valueOf(i), bigMod);
            DlpTask dlpTask = new DlpTask(bigGen, y);
            dlpService.dlp(dlpTask);
            if (dlpService instanceof ParallelPollardDLPService) {
                ((ParallelPollardDLPService) dlpService).resetDone();
            }
        }
    }
    /*
Last benchmark
17 bits
CollisionDLPService	1.029mls
ClassicPollardDLPService	0.868mls
ParallelPollardDLPService	0.332mls
18 bits
CollisionDLPService	1.102mls
ClassicPollardDLPService	1.157mls
ParallelPollardDLPService	0.67mls
19 bits
CollisionDLPService	1.611mls
ClassicPollardDLPService	1.746mls
ParallelPollardDLPService	0.519mls
20 bits
CollisionDLPService	2.706mls
ClassicPollardDLPService	2.556mls
ParallelPollardDLPService	0.533mls
21 bits
CollisionDLPService	2.777mls
ClassicPollardDLPService	2.831mls
ParallelPollardDLPService	0.724mls
22 bits
CollisionDLPService	3.453mls
ClassicPollardDLPService	3.666mls
ParallelPollardDLPService	0.943mls
23 bits
CollisionDLPService	8.278mls
ClassicPollardDLPService	8.194mls
ParallelPollardDLPService	1.595mls
24 bits
CollisionDLPService	8.068mls
ClassicPollardDLPService	7.522mls
ParallelPollardDLPService	2.253mls
25 bits
CollisionDLPService	16.541mls
ClassicPollardDLPService	10.068mls
ParallelPollardDLPService	4.116mls
26 bits
CollisionDLPService	18.619mls
ClassicPollardDLPService	12.049mls
ParallelPollardDLPService	6.645mls
*/
}
