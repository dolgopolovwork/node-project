package ru.babobka.nodetask.model;

import ru.babobka.nodeserials.NodeResponse;
import ru.babobka.nodeserials.data.Data;
import ru.babobka.nodetask.exception.ReducingException;

import java.util.List;

/**
 * Created by dolgopolov.a on 03.08.15.
 */
public abstract class Reducer {

    public Data reduce(List<NodeResponse> responses) throws ReducingException {
        if (responses == null) {
            throw new IllegalArgumentException("cannot reduce null responses");
        }
        try {
            return reduceImpl(responses);
        } catch (RuntimeException e) {
            throw new ReducingException(e);
        }
    }

    protected abstract Data reduceImpl(List<NodeResponse> responses) throws ReducingException;
}