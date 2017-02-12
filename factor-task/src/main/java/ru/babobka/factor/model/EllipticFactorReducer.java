package ru.babobka.factor.model;

import ru.babobka.factor.task.EllipticCurveFactorTask;
import ru.babobka.nodeserials.NodeResponse;
import ru.babobka.subtask.exception.ReducingException;
import ru.babobka.subtask.model.Reducer;
import ru.babobka.subtask.model.ReducingResult;

import java.math.BigInteger;

import java.util.List;

/**
 * Created by dolgopolov.a on 22.12.15.
 */
public class EllipticFactorReducer implements Reducer {

    @Override
    public ReducingResult reduce(List<NodeResponse> responses) throws ReducingException {
	try {
	    for (NodeResponse response : responses) {
		if (validResponse(response)) {
		    return new ReducingResult().add(response.getData());
		}
	    }
	} catch (Exception e) {
	    throw new ReducingException(e);
	}
	throw new ReducingException();
    }

    @Override
    public boolean validResponse(NodeResponse response) {
	try {
	    if (response != null && response.getStatus() == NodeResponse.Status.NORMAL) {
		BigInteger factor = response.getDataValue(EllipticCurveFactorTask.FACTOR);
		BigInteger n = response.getDataValue(EllipticCurveFactorTask.NUMBER);
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
