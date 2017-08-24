package ru.babobka.nodemasterserver.listener;

import ru.babobka.nodeserials.NodeResponse;
import ru.babobka.nodeutils.container.Container;
import ru.babobka.nodeutils.logger.SimpleLogger;

/**
 * Created by 123 on 15.07.2017.
 */
public class OnTaskIsReady implements OnResponseListener {

    private static final String TASK = "Task";

    private final SimpleLogger logger = Container.getInstance().get(SimpleLogger.class);

    @Override
    public void onResponse(NodeResponse response) {
        logger.info(TASK + " " + response.getTaskId() + " is ready ");
    }
}
