package ru.babobka.primecounter.model;

import ru.babobka.nodeserials.NodeResponse;
import ru.babobka.primecounter.task.PrimeCounterTask;
import ru.babobka.subtask.exception.ReducingException;
import ru.babobka.subtask.model.Reducer;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by dolgopolov.a on 03.08.15.
 */
public final class PrimeCounterReducer implements Reducer {

	@Override
	public Map<String, Serializable> reduce(List<NodeResponse> responses) throws ReducingException {
		try {
			int result = 0;
			for (NodeResponse response : responses) {
				if (isValidResponse(response)) {
					Integer subResult = response.getAdditionValue(PrimeCounterTask.PRIME_COUNT);
					if (subResult != null) {
						result += subResult;
					}

				} else {
					throw new ReducingException();
				}
			}
			Map<String, Serializable> resultMap = new HashMap<>();
			resultMap.put(PrimeCounterTask.PRIME_COUNT, result);
			return resultMap;
		} catch (Exception e) {
			e.printStackTrace();
		}
		throw new ReducingException();
	}

	@Override
	public boolean isValidResponse(NodeResponse response) {
		if (response != null && response.getStatus() == NodeResponse.Status.NORMAL
				&& response.getAdditionValue(PrimeCounterTask.PRIME_COUNT) != null) {
			return true;
		}
		return false;
	}

}
