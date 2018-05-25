package ru.babobka.nodemasterserver.listener;

import ru.babobka.nodeserials.NodeResponse;
import ru.babobka.nodeutils.container.Container;
import ru.babobka.nodeutils.logger.NodeLogger;

/**
 * Created by 123 on 15.07.2017.
 */
public class OnTaskIsReady implements OnResponseListener {

    private static final String TASK = "task";

    private final NodeLogger nodeLogger = Container.getInstance().get(NodeLogger.class);

    @Override
    public void onResponse(NodeResponse response) {
        nodeLogger.info(TASK + " " + response.getTaskId() + " is ready ");
    }
}
