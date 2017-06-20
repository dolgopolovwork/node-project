package ru.babobka.factor.service;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

import ru.babobka.factor.model.EllipticCurveProjective;
import ru.babobka.factor.model.FactoringResult;
import ru.babobka.factor.runnable.EllipticCurveProjectiveFactorCallable;
import ru.babobka.factor.util.MathUtil;

/**
 * Created by dolgopolov.a on 08.12.15.
 */
public class EllipticCurveFactorService {

    private volatile ThreadPoolExecutor threadPool;

    private volatile boolean stopped;

    public FactoringResult factor(BigInteger number) {
        return factor(number, Runtime.getRuntime().availableProcessors());
    }

    public FactoringResult factor(BigInteger number, int cores) {

        try {
            if (!stopped) {
                FactoringResult factoringResult;
                if (!numberIsBigEnough(number)) {
                    factoringResult = new FactoringResult(BigInteger.valueOf(MathUtil.dummyFactor(number.longValue())),
                            EllipticCurveProjective.dummyCurve());
                } else {
                    synchronized (this) {
                        if (!stopped && threadPool == null) {
                            threadPool = (ThreadPoolExecutor) Executors.newFixedThreadPool(cores);
                        }
                    }
                    factoringResult = ellipticFactorParallel(threadPool, number);
                }
                return factoringResult;
            }
            return null;
        } finally {
            stopped = false;
        }

    }

    private boolean numberIsBigEnough(BigInteger number) {
        return number.bitLength() > 50;
    }

    public synchronized void stop() {
        stopped = true;
        if (threadPool != null) {
            threadPool.shutdownNow();
        }
    }

    private FactoringResult ellipticFactorParallel(ThreadPoolExecutor threadPool, BigInteger n) {
        if (threadPool != null) {
            List<Future<FactoringResult>> futures = new ArrayList<>();
            for (int i = 0; i < threadPool.getMaximumPoolSize(); i++) {
                futures.add(threadPool.submit(new EllipticCurveProjectiveFactorCallable(threadPool, n)));
            }
            for (Future<FactoringResult> future : futures) {
                FactoringResult result;
                try {
                    result = future.get();
                    if (result != null) {
                        return result;
                    }
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                    continue;
                }

            }

        }
        return null;

    }

}