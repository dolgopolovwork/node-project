package ru.babobka.factor.service;

import ru.babobka.factor.callable.EllipticCurveProjectiveFactorCallable;
import ru.babobka.factor.model.FactoringResult;
import ru.babobka.nodeutils.container.Container;
import ru.babobka.nodeutils.logger.SimpleLogger;
import ru.babobka.nodeutils.thread.ThreadPoolService;
import ru.babobka.nodeutils.util.MathUtil;

import java.math.BigInteger;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by dolgopolov.a on 08.12.15.
 */
public class EllipticCurveFactorService extends ThreadPoolService<BigInteger, FactoringResult> {

    private final SimpleLogger logger = Container.getInstance().get(SimpleLogger.class);

    private final AtomicBoolean done = new AtomicBoolean(false);

    EllipticCurveFactorService(int cores) {
        super(cores);
    }

    @Override
    protected void stopImpl() {
        done.set(true);
    }

    @Override
    protected FactoringResult getStoppedResponse() {
        return new FactoringResult(BigInteger.ONE, null);
    }

    @Override
    protected FactoringResult executeImpl(BigInteger number) {
        done.set(false);
        FactoringResult factoringResult;
        if (!numberIsBigEnough(number)) {
            factoringResult = new FactoringResult(BigInteger.valueOf(MathUtil.dummyFactor(number.longValue())),
                    null);
        } else {
            factoringResult = ellipticFactorParallel(number);
        }
        return factoringResult;
    }

    private FactoringResult ellipticFactorParallel(BigInteger n) {
        List<Future<FactoringResult>> futures = submit(EllipticCurveProjectiveFactorCallable.createCalls(done, n, getCores()));
        for (Future<FactoringResult> future : futures) {
            FactoringResult result;
            try {
                result = future.get();
                if (result != null) {
                    return result;
                }
            } catch (InterruptedException | ExecutionException e) {
                if (e instanceof InterruptedException) {
                    Thread.currentThread().interrupt();
                }
                logger.error(e);
                stop();
            }
        }
        return null;
    }

    private boolean numberIsBigEnough(BigInteger number) {
        return number.bitLength() > 50;
    }
}