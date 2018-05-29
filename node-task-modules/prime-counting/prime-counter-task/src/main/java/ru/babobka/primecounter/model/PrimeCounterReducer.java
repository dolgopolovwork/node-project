package ru.babobka.primecounter.model;

import ru.babobka.nodeserials.NodeResponse;
import ru.babobka.nodeserials.data.Data;
import ru.babobka.nodetask.exception.ReducingException;
import ru.babobka.nodetask.model.Reducer;
import ru.babobka.nodeutils.container.Container;
import ru.babobka.primecounter.task.Params;

import java.util.List;

/**
 * Created by dolgopolov.a on 03.08.15.
 */
public class PrimeCounterReducer extends Reducer {

    private final PrimeCounterDataValidators primeCounterDataValidators = Container.getInstance().get(PrimeCounterDataValidators.class);

    @Override
    protected Data reduceImpl(List<NodeResponse> responses) throws ReducingException {
        int result = 0;
        for (NodeResponse response : responses) {
            if (!primeCounterDataValidators.isValidResponse(response)) {
                throw new ReducingException("Not valid response " + response);
            }
            Integer subResult = response.getDataValue(Params.PRIME_COUNT.getValue());
            result += subResult;
        }
        return new Data().put(Params.PRIME_COUNT.getValue(), result);
    }

}
