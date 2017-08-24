package ru.babobka.primecounter.model;

import ru.babobka.nodeserials.NodeRequest;
import ru.babobka.nodeserials.NodeResponse;
import ru.babobka.nodeserials.enumerations.ResponseStatus;
import ru.babobka.nodetask.model.DataValidators;
import ru.babobka.primecounter.task.Params;

/**
 * Created by 123 on 22.10.2017.
 */
public class PrimeCounterDataValidators extends DataValidators {
    @Override
    protected boolean isValidResponseImpl(NodeResponse response) {
        return response.getStatus() == ResponseStatus.NORMAL
                && response.getDataValue(Params.PRIME_COUNT.getValue()) != null
                && (int) response.getDataValue(Params.PRIME_COUNT.getValue()) > 0;
    }


    @Override
    protected boolean isValidRequestImpl(NodeRequest request) {
        try {
            long begin = request.getDataValue(Params.BEGIN.getValue());
            long end = request.getDataValue(Params.END.getValue());
            if (begin < 0 || begin >= end) {
                return false;
            }
        } catch (RuntimeException e) {
            return false;
        }
        return true;
    }
}
