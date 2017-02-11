package ru.babobka.subtask.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by dolgopolov.a on 29.09.15.
 */
public class ExecutionResult {

	private final boolean stopped;

	private final Map<String, Serializable> resultMap = new HashMap<>();

	public ExecutionResult(boolean stopped, Map<String, Serializable> resultMap) {
		this.stopped = stopped;
		if (resultMap != null)
			this.resultMap.putAll(resultMap);
	}

	public ExecutionResult(boolean stopped) {
		this(stopped, null);

	}

	public boolean isStopped() {
		return stopped;
	}

	public Map<String, Serializable> getResultMap() {
		return resultMap;
	}

	@Override
	public String toString() {
		return "ExecutionResult [stopped=" + stopped + ", resultMap=" + resultMap + "]";
	}

}
