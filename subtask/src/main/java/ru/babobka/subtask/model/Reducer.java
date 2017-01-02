package ru.babobka.subtask.model;

import ru.babobka.nodeserials.NodeResponse;
import ru.babobka.subtask.exception.ReducingException;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * Created by dolgopolov.a on 03.08.15.
 */
public interface Reducer {

	public Map<String, Serializable> reduce(
			List<NodeResponse> responses) throws ReducingException;

	public boolean isValidResponse(NodeResponse response);

}
