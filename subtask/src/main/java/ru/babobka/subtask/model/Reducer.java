package ru.babobka.subtask.model;

import ru.babobka.nodeserials.NodeResponse;
import ru.babobka.subtask.exception.ReducingException;

import java.util.List;

/**
 * Created by dolgopolov.a on 03.08.15.
 */
public interface Reducer {

    ReducingResult reduce(List<NodeResponse> responses) throws ReducingException;

    boolean validResponse(NodeResponse response);

}
