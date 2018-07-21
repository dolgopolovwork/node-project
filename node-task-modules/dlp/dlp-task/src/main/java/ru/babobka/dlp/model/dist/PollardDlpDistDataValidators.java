package ru.babobka.dlp.model.dist;

import ru.babobka.dlp.model.regular.PollardDlpDataValidators;
import ru.babobka.dlp.task.Params;
import ru.babobka.nodeserials.NodeRequest;
import ru.babobka.nodeserials.NodeResponse;
import ru.babobka.nodetask.model.DataValidators;
import ru.babobka.nodeutils.container.Container;

import java.math.BigInteger;

/**
 * Created by 123 on 09.07.2018.
 */
public class PollardDlpDistDataValidators extends DataValidators {

    private final PollardDlpDataValidators pollardDlpDataValidators = Container.getInstance().get(PollardDlpDataValidators.class);

    @Override
    protected boolean isValidResponseImpl(NodeResponse response) {
        BigInteger exp = response.getDataValue(Params.EXP.getValue());
        if (exp != null) {
            return pollardDlpDataValidators.isValidResponse(response);
        }
        return true;
    }

    @Override
    protected boolean isValidRequestImpl(NodeRequest request) {
        int loops = request.getDataValue(Params.LOOPS.getValue());
        return loops > 0 && pollardDlpDataValidators.isValidRequest(request);
    }
}
