package ru.babobka.dlp.model.regular;

import ru.babobka.nodeserials.NodeResponse;
import ru.babobka.nodeserials.data.Data;
import ru.babobka.nodetask.exception.ReducingException;
import ru.babobka.nodetask.model.Reducer;
import ru.babobka.nodeutils.container.Container;

import java.util.List;

/**
 * Created by dolgopolov.a on 22.12.15.
 */
public class PollardDlpReducer extends Reducer {

    private final PollardDlpDataValidators pollardDlpDataValidators = Container.getInstance().get(PollardDlpDataValidators.class);

    @Override
    protected Data reduceImpl(List<NodeResponse> responses) throws ReducingException {
        for (NodeResponse response : responses) {
            if (pollardDlpDataValidators.isValidResponse(response)) {
                return new Data().put(response.getData());
            }
        }
        throw new ReducingException();
    }
}
