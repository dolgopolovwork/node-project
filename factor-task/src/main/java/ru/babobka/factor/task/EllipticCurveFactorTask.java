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
public class EllipticCurveFactorTask extends SubTask {

	public static final String NUMBER = "number";

	public static final String FACTOR = "factor";

	private final EllipticFactorDistributor distributor;

	private final EllipticFactorReducer reducer = new EllipticFactorReducer();

	private final EllipticCurveFactorService factorService = new EllipticCurveFactorService();

	private static final String X = "x";

	private static final String Y = "y";

	private static final String A = "a";

	private static final String B = "b";

	private static final String NAME = "Elliptic curve factor";

	private static final String DESCRIPTION = "Factorizes a given big composite number using Lenstra algorithm";

	public EllipticCurveFactorTask() {
		this.distributor = new EllipticFactorDistributor(NAME);
	}

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
				return new ExecutionResult(isStopped(), result);
			}
			return new ExecutionResult(isStopped());
		} finally {
			factorService.stop();
		}
	}

	@Override
	protected void stopCurrentTask() {
		factorService.stop();
	}

	@Override
	public ValidationResult validateRequest(NodeRequest request) {
		if (request == null) {
			return ValidationResult.fail("Empty request");
		} else {
			try {

				BigInteger number = new BigInteger(request.getStringDataValue(NUMBER));
				if (number.compareTo(BigInteger.valueOf(3)) <= 0) {
					return ValidationResult.fail("number must be greater than 3");
				} else if (MathUtil.isPrime(number)) {
					return ValidationResult.fail("number is not composite");
				}

			} catch (Exception e) {
				return ValidationResult.fail(e);
			}
		}
		return ValidationResult.ok();
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
	public String getDescription() {
		return DESCRIPTION;
	}

	@Override
	public String getName() {
		return NAME;
	}

	@Override
	public boolean isRaceStyle() {
		return true;
	}

}