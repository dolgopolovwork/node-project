package ru.babobka.primecounter.model;

import ru.babobka.nodeserials.NodeResponse;
import ru.babobka.primecounter.task.PrimeCounterTask;
import ru.babobka.subtask.exception.ReducingException;
import ru.babobka.subtask.model.Reducer;
import ru.babobka.subtask.model.ReducingResult;

import java.util.List;

/**
 * Created by dolgopolov.a on 03.08.15.
 */
public final class PrimeCounterReducer implements Reducer {

    @Override
    public ReducingResult reduce(List<NodeResponse> responses) throws ReducingException {
	try {
	    int result = 0;
	    for (NodeResponse response : responses) {
		if (validResponse(response)) {
		    Integer subResult = response.getDataValue(PrimeCounterTask.PRIME_COUNT);
		    if (subResult != null) {
			result += subResult;
		    }
		} else {
		    throw new ReducingException();
		}
	    }
	    ReducingResult reducingResult = new ReducingResult();
	    reducingResult.add(PrimeCounterTask.PRIME_COUNT, result);
	    return reducingResult;
	} catch (Exception e) {
	    throw new ReducingException(e);
	}
    }

    @Override
    public boolean validResponse(NodeResponse response) {
	if (response != null && response.getStatus() == NodeResponse.Status.NORMAL
		&& response.getDataValue(PrimeCounterTask.PRIME_COUNT) != null) {
	    return true;
	}
	return false;
    }

}
