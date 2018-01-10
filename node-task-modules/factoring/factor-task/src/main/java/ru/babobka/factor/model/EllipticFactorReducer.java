package ru.babobka.factor.model;

import ru.babobka.nodeserials.NodeResponse;
import ru.babobka.nodetask.exception.ReducingException;
import ru.babobka.nodetask.model.Reducer;
import ru.babobka.nodetask.model.ReducingResult;
import ru.babobka.nodeutils.container.Container;

import java.util.List;

/**
 * Created by dolgopolov.a on 22.12.15.
 */
public class EllipticFactorReducer extends Reducer {

    private final EllipticFactorDataValidators ellipticFactorDataValidators = Container.getInstance().get(EllipticFactorDataValidators.class);

    @Override
    protected ReducingResult reduceImpl(List<NodeResponse> responses) throws ReducingException {
        for (NodeResponse response : responses) {
            if (ellipticFactorDataValidators.isValidResponse(response)) {
                return new ReducingResult().add(response.getData());
            }
        }
        throw new ReducingException();
    }

}
