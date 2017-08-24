package ru.babobka.nodemasterserver.listener;

import ru.babobka.nodeserials.NodeResponse;

/**
 * Created by 123 on 15.07.2017.
 */
public interface OnResponseListener {

    void onResponse(NodeResponse response);
}
