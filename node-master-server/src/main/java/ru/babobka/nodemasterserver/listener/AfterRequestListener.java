package ru.babobka.nodemasterserver.listener;

import ru.babobka.nodeserials.NodeRequest;

import java.io.Serializable;

/**
 * Created by 123 on 21.11.2017.
 */
public interface AfterRequestListener<R extends Serializable> {
    void afterRequest(NodeRequest request, R result);
}
