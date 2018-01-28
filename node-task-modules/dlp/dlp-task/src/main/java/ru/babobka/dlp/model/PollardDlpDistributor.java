package ru.babobka.dlp.model;


import ru.babobka.dlp.task.Params;
import ru.babobka.nodeserials.NodeRequest;
import ru.babobka.nodetask.model.RequestDistributor;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        Map<String, Serializable> innerDataMap = new HashMap<>();
        innerDataMap.put(Params.X.getValue(), x);
        innerDataMap.put(Params.Y.getValue(), y);
        innerDataMap.put(Params.MOD.getValue(), mod);
        for (int i = 0; i < nodes; i++) {
            requests.add(NodeRequest.race(request.getTaskId(), request.getTaskName(), innerDataMap));
        }
        return requests;
    }

}
