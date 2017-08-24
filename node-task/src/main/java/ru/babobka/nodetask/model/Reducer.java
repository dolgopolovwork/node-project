package ru.babobka.nodetask.model;

import ru.babobka.nodeserials.NodeResponse;
import ru.babobka.nodetask.exception.ReducingException;

import java.util.List;

/**
 * Created by dolgopolov.a on 03.08.15.
 */
public abstract class Reducer {

    public ReducingResult reduce(List<NodeResponse> responses) throws ReducingException {
        if (responses == null) {
            throw new IllegalArgumentException("can not reduce null responses");
        }
        return reduceImpl(responses);
    }

    protected abstract ReducingResult reduceImpl(List<NodeResponse> responses) throws ReducingException;
}