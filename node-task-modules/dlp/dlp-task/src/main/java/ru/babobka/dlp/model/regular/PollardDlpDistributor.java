package ru.babobka.dlp.model.regular;


import ru.babobka.dlp.task.Params;
import ru.babobka.nodeserials.NodeRequest;
import ru.babobka.nodeserials.data.Data;
import ru.babobka.nodetask.model.RequestDistributor;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by dolgopolov.a on 22.12.15.
 */
public class PollardDlpDistributor extends RequestDistributor {

    @Override
    protected List<NodeRequest> distributeImpl(NodeRequest request, int nodes) {
        BigInteger x = request.getDataValue(Params.X.getValue());
        BigInteger y = request.getDataValue(Params.Y.getValue());
        BigInteger mod = request.getDataValue(Params.MOD.getValue());
        List<NodeRequest> requests = new ArrayList<>(nodes);
        Data data = new Data();
        data.put(Params.X.getValue(), x);
        data.put(Params.Y.getValue(), y);
        data.put(Params.MOD.getValue(), mod);
        for (int i = 0; i < nodes; i++) {
            requests.add(NodeRequest.race(request.getTaskId(), request.getTaskName(), data));
        }
        return requests;
    }

}
