package ru.babobka.dlp;

import ru.babobka.dlp.model.dist.DlpTaskDist;
import ru.babobka.dlp.service.DlpDistClient;
import ru.babobka.nodemasterserver.server.config.PortConfig;
import ru.babobka.nodetester.benchmark.performer.CustomBenchmarkPerformer;
import ru.babobka.nodeutils.math.Fp;
import ru.babobka.nodeutils.math.SafePrime;
import ru.babobka.nodeutils.time.Timer;
import ru.babobka.nodeutils.util.MathUtil;

import java.math.BigInteger;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by 123 on 01.02.2018.
 */
public class DlpDistNodeBenchmarkPerformer extends CustomBenchmarkPerformer {
    private static final Random RAND = new Random();
    private final SafePrime safePrime;
    private final BigInteger gen;

    public DlpDistNodeBenchmarkPerformer(int orderBitLength) {
        safePrime = SafePrime.random(orderBitLength - 1);
        gen = MathUtil.getGenerator(safePrime);
    }

    private BigInteger createNumber(BigInteger mod) {
        BigInteger number = BigInteger.valueOf(RAND.nextInt()).mod(mod);
        while (number.equals(BigInteger.ZERO)) {
            number = BigInteger.valueOf(RAND.nextInt()).mod(mod);
        }
        return number;
    }

    @Override
    protected void performBenchmark(PortConfig portConfig, AtomicLong timeStorage) throws InterruptedException {
        ServerConfig serverConfig = new ServerConfig(
                "localhost",
                portConfig.getClientListenerPort(),
                portConfig.getWebListenerPort());
        BigInteger y = createNumber(safePrime.getPrime());
        DlpTaskDist dlpTaskDist = new DlpTaskDist(new Fp(gen, safePrime.getPrime()), new Fp(y, safePrime.getPrime()));
        DlpDistClient dlpDistClient = new DlpDistClient(serverConfig, dlpTaskDist);
        dlpDistClient.start();
        Timer timer = new Timer();
        dlpDistClient.getResult();
        timeStorage.addAndGet(timer.getTimePassed());
    }
}