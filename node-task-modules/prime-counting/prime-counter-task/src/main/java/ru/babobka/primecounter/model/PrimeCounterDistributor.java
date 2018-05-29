package ru.babobka.primecounter.model;

import ru.babobka.nodeserials.NodeRequest;
import ru.babobka.nodeserials.data.Data;
import ru.babobka.nodetask.model.RequestDistributor;
import ru.babobka.primecounter.task.Params;

import java.util.ArrayList;
import java.util.List;

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
            Data data = new Data();
            data.put(Params.BEGIN.getValue(), range.getBegin());
            data.put(Params.END.getValue(), range.getEnd());
            requests.add(NodeRequest.regular(request.getTaskId(), request.getTaskName(), data));
        }
        return requests;
    }
}
