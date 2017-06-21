package ru.babobka.primecounter.model;

import ru.babobka.nodeserials.NodeResponse;
import ru.babobka.primecounter.task.Params;
import ru.babobka.primecounter.task.PrimeCounterTask;
import ru.babobka.subtask.exception.ReducingException;
import ru.babobka.subtask.model.Reducer;
import ru.babobka.subtask.model.ReducingResult;

import java.util.List;

/**
 * Created by dolgopolov.a on 03.08.15.
 */
public final class PrimeCounterReducer implements Reducer {

    @Override
    public ReducingResult reduce(List<NodeResponse> responses) throws ReducingException {
        try {
            int result = 0;
            for (NodeResponse response : responses) {
                if (validResponse(response)) {
                    Integer subResult = response.getDataValue(Params.PRIME_COUNT.getValue());
                    if (subResult != null) {
                        result += subResult;
                    }
                } else {
                    throw new ReducingException();
                }
            }
            ReducingResult reducingResult = new ReducingResult();
            reducingResult.add(Params.PRIME_COUNT.getValue(), result);
            return reducingResult;
        } catch (Exception e) {
            throw new ReducingException(e);
        }
    }

    @Override
    public boolean validResponse(NodeResponse response) {
        return response != null && response.getStatus() == NodeResponse.Status.NORMAL
                && response.getDataValue(Params.PRIME_COUNT.getValue()) != null;
    }

}
