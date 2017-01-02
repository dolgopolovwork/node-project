package ru.babobka.factor.model;

import ru.babobka.factor.task.EllipticCurveFactorTask;
import ru.babobka.nodeserials.NodeResponse;
import ru.babobka.subtask.exception.ReducingException;
import ru.babobka.subtask.model.Reducer;

import java.io.Serializable;
import java.math.BigInteger;

import java.util.List;
import java.util.Map;

/**
 * Created by dolgopolov.a on 22.12.15.
 */
public class EllipticFactorReducer implements Reducer {

	@Override
	public Map<String, Serializable> reduce(List<NodeResponse> responses) throws ReducingException {
		try {
			for (NodeResponse response : responses) {
				if (isValidResponse(response)) {
					return response.getAddition();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		throw new ReducingException();
	}

	@Override
	public boolean isValidResponse(NodeResponse response) {
		try {
			if (response != null && response.getStatus() == NodeResponse.Status.NORMAL) {
				BigInteger factor = response.getAdditionValue(EllipticCurveFactorTask.FACTOR);
				BigInteger n = response.getAdditionValue(EllipticCurveFactorTask.NUMBER);
				if (factor != null && n != null && !factor.equals(BigInteger.ONE.negate())) {
					if (n.mod(factor).equals(BigInteger.ZERO)) {
						return true;
					}

				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

}
