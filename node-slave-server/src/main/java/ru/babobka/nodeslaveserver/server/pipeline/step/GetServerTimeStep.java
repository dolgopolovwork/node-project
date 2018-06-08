package ru.babobka.nodeslaveserver.server.pipeline.step;

import ru.babobka.nodesecurity.auth.AuthResult;
import ru.babobka.nodesecurity.network.SecureNodeConnection;
import ru.babobka.nodeserials.NodeRequest;
import ru.babobka.nodeslaveserver.server.pipeline.PipeContext;
import ru.babobka.nodeutils.container.Container;
import ru.babobka.nodeutils.func.pipeline.Step;
import ru.babobka.nodeutils.logger.NodeLogger;
import ru.babobka.nodeutils.network.NodeConnection;
import ru.babobka.nodeutils.time.ServerTime;

import java.io.IOException;

/**
 * Created by 123 on 08.06.2018.
 */
public class GetServerTimeStep implements Step<PipeContext> {

    private final NodeLogger nodeLogger = Container.getInstance().get(NodeLogger.class);

    @Override
    public boolean execute(PipeContext pipeContext) {
        AuthResult authResult = pipeContext.getAuthResult();
        NodeConnection connection = pipeContext.getConnection();
        try {
            SecureNodeConnection secureNodeConnection = new SecureNodeConnection(connection, authResult.getSecretKey());
            ServerTime serverTime = getServerTime(secureNodeConnection);
            pipeContext.setServerTime(serverTime);
            return true;
        } catch (IOException e) {
            nodeLogger.error("cannot  get master-server time due to network error");
            return false;
        }
    }

    private static ServerTime getServerTime(NodeConnection connection) throws IOException {
        NodeRequest timeSettingRequest = connection.receive();
        return new ServerTime(timeSettingRequest.getTimeStamp());
    }
}
