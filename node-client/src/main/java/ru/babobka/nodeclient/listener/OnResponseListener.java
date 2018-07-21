package ru.babobka.nodeclient.listener;

import ru.babobka.nodeserials.NodeResponse;

/**
 * Created by 123 on 07.07.2018.
 */
public interface OnResponseListener {

    ListenerResult onResponse(NodeResponse response);
}
