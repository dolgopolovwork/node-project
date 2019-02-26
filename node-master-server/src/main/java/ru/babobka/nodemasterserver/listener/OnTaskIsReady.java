package ru.babobka.nodemasterserver.listener;

import org.apache.log4j.Logger;
import ru.babobka.nodeserials.NodeResponse;

/**
 * Created by 123 on 15.07.2017.
 */
public class OnTaskIsReady implements OnResponseListener {
    private static final Logger logger = Logger.getLogger(OnTaskIsReady.class);

    @Override
    public void onResponse(NodeResponse response) {
        logger.info("task " + response.getTaskId() + " is ready ");
    }
}
