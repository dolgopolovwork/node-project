package ru.babobka.factor.task;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

import ru.babobka.factor.model.EllipticFactorDistributor;
import ru.babobka.factor.model.EllipticFactorReducer;
import ru.babobka.factor.model.FactoringResult;
import ru.babobka.factor.service.EllipticCurveFactorService;
import ru.babobka.factor.util.MathUtil;
import ru.babobka.nodeserials.NodeRequest;
import ru.babobka.subtask.model.ExecutionResult;
import ru.babobka.subtask.model.Reducer;
import ru.babobka.subtask.model.RequestDistributor;
import ru.babobka.subtask.model.SubTask;
import ru.babobka.subtask.model.ValidationResult;

/**
 * Created by dolgopolov.a on 08.12.15.
 */
public class EllipticCurveFactorTask implements SubTask {

	public static final String NUMBER = "number";

	public static final String FACTOR = "factor";

	private final EllipticFactorDistributor distributor = new EllipticFactorDistributor();

	private final EllipticFactorReducer reducer = new EllipticFactorReducer();

	private final EllipticCurveFactorService factorService = new EllipticCurveFactorService();

	private static final String X = "x";

	private static final String Y = "y";

	private static final String A = "a";

	private static final String B = "b";

	private volatile boolean stopped;

	@Override
	public ExecutionResult execute(NodeRequest request) {

		try {
			Map<String, Serializable> result = new HashMap<>();
			BigInteger number = new BigInteger(request.getStringDataValue(NUMBER));
			FactoringResult factoringResult = factorService.factor(number);
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
			factorService.stop();
		}
	}

	@Override
	public synchronized void stopTask() {
		stopped = true;
		factorService.stop();
	}

	@Override
	public ValidationResult validateRequest(NodeRequest request) {
		if (request == null) {
			return new ValidationResult("Empty request", false);
		} else {
			try {

				BigInteger number = new BigInteger(request.getStringDataValue(NUMBER));
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
		BigInteger number = new BigInteger(request.getStringDataValue(NUMBER));
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