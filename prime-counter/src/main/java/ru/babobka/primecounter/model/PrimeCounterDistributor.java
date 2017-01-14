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
	public NodeRequest[] distribute(Map<String, String> addition, int nodes, UUID taskId) {

		long begin = Long.parseLong(addition.get(BEGIN));
		long end = Long.parseLong(addition.get(END));
		Range[] ranges = MathUtil.getRangeArray(begin, end, nodes);
		NodeRequest[] requests = new NodeRequest[nodes];
		Map<String, Serializable> innerAdditionMap;
		for (int i = 0; i < requests.length; i++) {
			innerAdditionMap = new HashMap<>();
			innerAdditionMap.put(BEGIN, ranges[i].getBegin());
			innerAdditionMap.put(END, ranges[i].getEnd());
			requests[i] = new NodeRequest(taskId, UUID.randomUUID(), taskName, innerAdditionMap, false, false);
		}
		return requests;

	}

	@Override
	public boolean isValidArguments(Map<String, String> addition) {
		try {
			long begin = Long.parseLong(addition.get(BEGIN));
			long end = Long.parseLong(addition.get(END));
			if (begin >= end || begin < 0) {
				return false;
			}
		} catch (NumberFormatException e) {
			return false;
		}
		return true;
	}
}
