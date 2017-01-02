package ru.babobka.subtask.model;

import java.io.Serializable;
import java.util.Map;

/**
 * Created by dolgopolov.a on 29.09.15.
 */
public class ExecutionResult{

    private final boolean stopped;

    private final Map<String, Serializable> resultMap;


    public ExecutionResult(boolean stopped, Map<String, Serializable> resultMap) {
        this.stopped = stopped;
        this.resultMap = resultMap;
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
