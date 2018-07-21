package ru.babobka.dlp.model.dist;

import ru.babobka.nodeserials.NodeRequest;
import ru.babobka.nodetask.model.RequestDistributor;

import java.util.Collections;
import java.util.List;

/**
 * Created by 123 on 09.07.2018.
 */
public class PollardDlpDistDistributor extends RequestDistributor {
    @Override
    protected List<NodeRequest> distributeImpl(NodeRequest request, int nodes) {
        return Collections.singletonList(request);
    }
}
