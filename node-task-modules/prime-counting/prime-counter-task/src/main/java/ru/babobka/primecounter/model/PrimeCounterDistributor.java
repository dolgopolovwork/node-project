package ru.babobka.primecounter.model;

import ru.babobka.nodeserials.NodeRequest;
import ru.babobka.nodetask.model.RequestDistributor;
import ru.babobka.primecounter.task.Params;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by dolgopolov.a on 31.07.15.
 */
public class PrimeCounterDistributor extends RequestDistributor {

    @Override
    protected List<NodeRequest> distributeImpl(NodeRequest request, int nodes) {
        long begin = request.getDataValue(Params.BEGIN.getValue());
        long end = request.getDataValue(Params.END.getValue());
        List<Range> ranges = Range.getRanges(begin, end, nodes);
        List<NodeRequest> requests = new ArrayList<>(nodes);
        for (Range range : ranges) {
            Map<String, Serializable> localArguments = new HashMap<>();
            localArguments.put(Params.BEGIN.getValue(), range.getBegin());
            localArguments.put(Params.END.getValue(), range.getEnd());
            requests.add(NodeRequest.regular(request.getTaskId(), request.getTaskName(), localArguments));
        }
        return requests;
    }
}
