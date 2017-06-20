package ru.babobka.subtask.model;

import java.util.Map;
import java.util.UUID;

import ru.babobka.nodeserials.NodeRequest;

/**
 * Created by dolgopolov.a on 31.07.15.
 */
public interface RequestDistributor {

    NodeRequest[] distribute(Map<String, String> arguments, int nodes, UUID taskId);

    boolean validArguments(Map<String, String> arguments);

}
