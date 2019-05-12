package ru.babobka.nodeslaveserver.callback;

import lombok.NonNull;
import org.apache.log4j.Logger;
import ru.babobka.nodeserials.NodeResponse;
import ru.babobka.nodeserials.enumerations.ResponseStatus;
import ru.babobka.nodeutils.func.Callback;
import ru.babobka.nodeutils.network.NodeConnection;

/**
 * Created by 123 on 30.03.2019.
 */
public class NodeResponseCallback implements Callback<NodeResponse> {

    private static final Logger logger = Logger.getLogger(NodeResponseCallback.class);
    private final NodeConnection nodeConnection;

    public NodeResponseCallback(@NonNull NodeConnection nodeConnection) {
        this.nodeConnection = nodeConnection;
    }

    @Override
    public void callback(NodeResponse response) {
        if (response.getStatus() != ResponseStatus.STOPPED) {
            nodeConnection.sendThrowRuntime(response);
            logger.info("response was sent " + response);
        } else {
            logger.warn("response was stopped " + response);
        }
    }
}
