package ru.babobka.factor.task;

import ru.babobka.factor.model.EllipticCurveProjective;
import ru.babobka.factor.model.EllipticFactorDistributor;
import ru.babobka.factor.model.EllipticFactorReducer;
import ru.babobka.factor.model.FactoringResult;
import ru.babobka.factor.runnable.EllipticCurveProjectiveFactorCallable;
import ru.babobka.factor.util.MathUtil;
import ru.babobka.nodeserials.NodeRequest;
import ru.babobka.subtask.model.ExecutionResult;
import ru.babobka.subtask.model.Reducer;
import ru.babobka.subtask.model.RequestDistributor;
import ru.babobka.subtask.model.SubTask;
import ru.babobka.subtask.model.ValidationResult;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Created by dolgopolov.a on 08.12.15.
 */
public class EllipticCurveFactorTask implements SubTask {

	private volatile ExecutorService threadPool;

	public static final String NUMBER = "number";

	public static final String FACTOR = "factor";

	private final EllipticFactorDistributor distributor = new EllipticFactorDistributor();

	private final EllipticFactorReducer reducer = new EllipticFactorReducer();

	private static final String X = "x";

	private static final String Y = "y";

	private static final String A = "a";

	private static final String B = "b";

	private volatile boolean stopped;

	@Override
	public ExecutionResult execute(NodeRequest request) {

		try {
			Map<String, Serializable> result = new HashMap<>();
			BigInteger number = new BigInteger(request.getStringAdditionValue(NUMBER));
			FactoringResult factoringResult;

			if (isRequestDataTooSmall(request)) {
				factoringResult = new FactoringResult(BigInteger.valueOf(MathUtil.dummyFactor(number.longValue())),
						EllipticCurveProjective.dummyCurve());
			} else {
				synchronized (this) {
					if (!stopped && threadPool == null) {
						threadPool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
					}
				}
				factoringResult = ellipticFactorParallel(threadPool, number,
						Runtime.getRuntime().availableProcessors());
			}
			if (factoringResult != null) {
				result.put(NUMBER, number);
				result.put(FACTOR, factoringResult.getFactor());
				result.put(X, factoringResult.getEllipticCurveProjective().getX());
				result.put(Y, factoringResult.getEllipticCurveProjective().getY());
				result.put(A, factoringResult.getEllipticCurveProjective().getA());
				result.put(B, factoringResult.getEllipticCurveProjective().getB());
				return new ExecutionResult(stopped, result);
			}
			return new ExecutionResult(stopped, null);
		} finally {
			if (threadPool != null) {
				threadPool.shutdown();
			}
		}
	}

	@Override
	public synchronized void stopTask() {
		stopped = true;
		if (threadPool != null) {
			threadPool.shutdownNow();
		}
	}

	@Override
	public ValidationResult validateRequest(NodeRequest request) {
		if (request == null) {
			return new ValidationResult("Empty request", false);
		} else {
			try {

				BigInteger number = new BigInteger(request.getStringAdditionValue(NUMBER));
				if (number.compareTo(BigInteger.valueOf(3)) <= 0) {
					return new ValidationResult("number must be greater than 3", false);
				} else if (MathUtil.isPrime(number)) {
					return new ValidationResult("number is not composite", false);
				}

			} catch (Exception e) {
				return new ValidationResult(e, false);
			}
		}
		return new ValidationResult(true);
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
					if (!stopped) {
						e.printStackTrace();
					}
					continue;
				}

			}

		}
		return null;

	}

	@Override
	public RequestDistributor getDistributor() {
		return distributor;
	}

	@Override
	public Reducer getReducer() {
		return reducer;
	}

	@Override
	public boolean isRequestDataTooSmall(NodeRequest request) {
		BigInteger number = new BigInteger(request.getStringAdditionValue(NUMBER));
		return number.bitLength() < 50;
	}

	@Override
	public SubTask newInstance() {
		return new EllipticCurveFactorTask();
	}

	@Override
	public boolean isStopped() {

		return stopped;
	}

}