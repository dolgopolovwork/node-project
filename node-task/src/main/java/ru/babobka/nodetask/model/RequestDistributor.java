package ru.babobka.nodetask.model;

import ru.babobka.nodeserials.NodeRequest;

import java.util.List;

/**
 * Created by dolgopolov.a on 31.07.15.
 */
public abstract class RequestDistributor {

    public List<NodeRequest> distribute(NodeRequest request, int nodes) {
        if (request == null) {
            throw new IllegalArgumentException("arguments is null");
        } else if (nodes < 1) {
            throw new IllegalArgumentException("there must be at least one node");
        }
        return distributeImpl(request, nodes);
    }

    protected abstract List<NodeRequest> distributeImpl(NodeRequest request, int nodes);

}
