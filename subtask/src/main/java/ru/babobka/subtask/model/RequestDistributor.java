package ru.babobka.subtask.model;

import java.util.Map;
import java.util.UUID;

import ru.babobka.nodeserials.NodeRequest;

/**
 * Created by dolgopolov.a on 31.07.15.
 */
public interface RequestDistributor {

	public NodeRequest[] distribute(Map<String, String> arguments,
			int nodes, UUID taskId);

	public boolean isValidArguments(Map<String, String> arguments);

}
