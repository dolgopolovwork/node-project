package ru.babobka.dlp.model.dist;

import ru.babobka.nodeserials.NodeResponse;
import ru.babobka.nodeserials.data.Data;
import ru.babobka.nodetask.exception.ReducingException;
import ru.babobka.nodetask.model.Reducer;

import java.util.List;

/**
 * Created by 123 on 09.07.2018.
 */
public class PollardDlpDistReducer extends Reducer {
    @Override
    protected Data reduceImpl(List<NodeResponse> responses) throws ReducingException {
        return responses.iterator().next().getData();
    }
}
