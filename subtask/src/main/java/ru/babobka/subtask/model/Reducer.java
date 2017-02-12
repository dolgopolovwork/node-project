package ru.babobka.subtask.model;

import ru.babobka.nodeserials.NodeResponse;
import ru.babobka.subtask.exception.ReducingException;

import java.util.List;

/**
 * Created by dolgopolov.a on 03.08.15.
 */
public interface Reducer {

    public ReducingResult reduce(List<NodeResponse> responses) throws ReducingException;

    public boolean validResponse(NodeResponse response);

}
