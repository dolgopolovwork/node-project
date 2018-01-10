package ru.babobka.factor.model;

import ru.babobka.factor.task.Params;
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
public class EllipticFactorDistributor extends RequestDistributor {

    @Override
    protected List<NodeRequest> distributeImpl(NodeRequest request, int nodes) {
        BigInteger n = request.getDataValue(Params.NUMBER.getValue());
        List<NodeRequest> requests = new ArrayList<>(nodes);
        Map<String, Serializable> innerDataMap = new HashMap<>();
        innerDataMap.put(Params.NUMBER.getValue(), n);
        for (int i = 0; i < nodes; i++) {
            requests.add(NodeRequest.race(request.getTaskId(), request.getTaskName(), innerDataMap));
        }
        return requests;
    }

}
