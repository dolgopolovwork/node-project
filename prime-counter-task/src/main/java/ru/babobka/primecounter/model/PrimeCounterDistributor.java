package ru.babobka.primecounter.model;

import ru.babobka.nodeserials.NodeRequest;
import ru.babobka.primecounter.util.MathUtil;
import ru.babobka.subtask.model.RequestDistributor;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created by dolgopolov.a on 31.07.15.
 */
public final class PrimeCounterDistributor implements RequestDistributor {

    private static final String BEGIN = "begin";

    private static final String END = "end";

    private final String taskName;

    public PrimeCounterDistributor(String taskName) {
	this.taskName = taskName;
    }

    @Override
    public NodeRequest[] distribute(Map<String, String> dataMap, int nodes, UUID taskId) {

	long begin = Long.parseLong(dataMap.get(BEGIN));
	long end = Long.parseLong(dataMap.get(END));
	Range[] ranges = MathUtil.getRangeArray(begin, end, nodes);
	NodeRequest[] requests = new NodeRequest[nodes];
	Map<String, Serializable> innerDataMap;
	for (int i = 0; i < requests.length; i++) {
	    innerDataMap = new HashMap<>();
	    innerDataMap.put(BEGIN, ranges[i].getBegin());
	    innerDataMap.put(END, ranges[i].getEnd());
	    requests[i] = NodeRequest.regular(taskId, taskName, innerDataMap);
	}
	return requests;

    }

    @Override
    public boolean validArguments(Map<String, String> addition) {
	try {
	    long begin = Long.parseLong(addition.get(BEGIN));
	    long end = Long.parseLong(addition.get(END));
	    if (begin >= end || begin < 0) {
		return false;
	    }
	} catch (RuntimeException e) {
	    return false;
	}
	return true;
    }
}
