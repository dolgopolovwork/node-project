package ru.babobka.nodetask.model;

import ru.babobka.nodeserials.NodeRequest;
import ru.babobka.nodeserials.NodeResponse;

/**
 * Created by 123 on 22.10.2017.
 */
public abstract class DataValidators {
    public boolean isValidResponse(NodeResponse response) {
        return response != null && isValidResponseImpl(response);
    }

    public boolean isValidRequest(NodeRequest request) {
        return request != null && isValidRequestImpl(request);
    }

    protected abstract boolean isValidResponseImpl(NodeResponse response);

    protected abstract boolean isValidRequestImpl(NodeRequest request);

}
