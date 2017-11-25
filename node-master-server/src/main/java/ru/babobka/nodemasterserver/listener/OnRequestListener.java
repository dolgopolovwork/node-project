package ru.babobka.nodemasterserver.listener;

import ru.babobka.nodeserials.NodeRequest;

import java.io.Serializable;

/**
 * Created by 123 on 21.11.2017.
 */
public interface OnRequestListener<R extends Serializable> {

    R onRequest(NodeRequest request);

}
