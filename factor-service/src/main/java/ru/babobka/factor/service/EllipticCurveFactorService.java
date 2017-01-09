package ru.babobka.factor.service;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import ru.babobka.factor.model.EllipticCurveProjective;
import ru.babobka.factor.model.FactoringResult;
import ru.babobka.factor.runnable.EllipticCurveProjectiveFactorCallable;
import ru.babobka.factor.util.MathUtil;

/**
 * Created by dolgopolov.a on 08.12.15.
 */
public class EllipticCurveFactorService {

	private volatile ExecutorService threadPool;

	private volatile boolean stopped;

	public FactoringResult factor(BigInteger number) {
		return factor(number, Runtime.getRuntime().availableProcessors());
	}

	public FactoringResult factor(BigInteger number, int cores) {

		try {
			if (!stopped) {
				FactoringResult factoringResult;
				if (number.bitLength() < 50) {
					factoringResult = new FactoringResult(BigInteger.valueOf(MathUtil.dummyFactor(number.longValue())),
							EllipticCurveProjective.dummyCurve());
				} else {
					synchronized (this) {
						if (!stopped && threadPool == null) {
							threadPool = Executors.newFixedThreadPool(cores);
						}
					}
					factoringResult = ellipticFactorParallel(threadPool, number,
							Runtime.getRuntime().availableProcessors());
				}
				return factoringResult;
			}
			return null;
		} finally {
			stopped = false;
		}

	}

	public synchronized void stop() {
		stopped = true;
		if (threadPool != null) {
			threadPool.shutdownNow();
		}
	}

	private FactoringResult ellipticFactorParallel(ExecutorService threadPool, BigInteger n, int cores) {
		if (threadPool != null) {
			List<Future<FactoringResult>> futures = new ArrayList<>();
			for (int i = 0; i < cores; i++) {
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