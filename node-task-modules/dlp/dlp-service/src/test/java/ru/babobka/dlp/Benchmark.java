package ru.babobka.dlp;

import ru.babobka.dlp.collision.CollisionDLPService;
import ru.babobka.dlp.collision.pollard.ClassicPollardDLPService;
import ru.babobka.dlp.collision.pollard.PollardCollisionService;
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
    private static final CollisionDLPService collisionDLPService = new CollisionDLPService();
    private static final ClassicPollardDLPService pollardDLPService = new ClassicPollardDLPService();

    static {
        Container.getInstance().put(new PollardCollisionService());
    }

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
        }
    }

    private static void warmUp() {
        for (int i = 0; i < 10; i++) {
            someExecution(LITTLE_GEN, LITTLE_MOD, collisionDLPService);
            someExecution(LITTLE_GEN, LITTLE_MOD, pollardDLPService);
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
        }
    }
    /*
Last benchmark
17 bits
CollisionDLPService	2.078mls
18 bits
CollisionDLPService	1.873mls
19 bits
CollisionDLPService	1.874mls
20 bits
CollisionDLPService	2.286mls
21 bits
CollisionDLPService	3.221mls
22 bits
CollisionDLPService	4.757mls
23 bits
CollisionDLPService	10.058mls
24 bits
CollisionDLPService	10.816mls
25 bits
CollisionDLPService	18.892mls
26 bits
CollisionDLPService	25.135mls
 */
}
