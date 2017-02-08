package ru.babobka.factor.model;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import ru.babobka.nodeserials.NodeRequest;
import ru.babobka.subtask.model.RequestDistributor;

/**
 * Created by dolgopolov.a on 22.12.15.
 */
public class EllipticFactorDistributor implements RequestDistributor {

	private static final String NUMBER = "number";

	private final String taskName;

	public EllipticFactorDistributor(String taskName) {
		this.taskName = taskName;
	}

	@Override
	public NodeRequest[] distribute(Map<String, String> addition, int nodes, UUID id) {
		BigInteger n = new BigInteger(addition.get(NUMBER));
		NodeRequest[] requests = new NodeRequest[nodes];
		Map<String, Serializable> innerAdditionMap = new HashMap<>();
		innerAdditionMap.put(NUMBER, n);
		for (int i = 0; i < requests.length; i++) {
			requests[i] = new NodeRequest(id, UUID.randomUUID(), taskName, innerAdditionMap, false, true);
		}
		return requests;
	}

	@Override
	public boolean isValidArguments(Map<String, String> addition) {
		try {

			BigInteger n = new BigInteger(addition.getOrDefault(NUMBER, ""));
			if (n.compareTo(BigInteger.ZERO) < 0) {
				return false;
			}
		} catch (NumberFormatException e) {
			return false;
		}
		return true;
	}
}
