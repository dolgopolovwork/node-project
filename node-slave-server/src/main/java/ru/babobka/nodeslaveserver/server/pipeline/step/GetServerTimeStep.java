package ru.babobka.nodeslaveserver.server.pipeline.step;

import org.apache.log4j.Logger;
import ru.babobka.nodesecurity.auth.AuthResult;
import ru.babobka.nodesecurity.network.SecureNodeConnection;
import ru.babobka.nodeserials.NodeRequest;
import ru.babobka.nodeslaveserver.server.pipeline.PipeContext;
import ru.babobka.nodeutils.func.pipeline.Step;
import ru.babobka.nodeutils.network.NodeConnection;
import ru.babobka.nodeutils.time.ServerTime;

import java.io.IOException;

/**
 * Created by 123 on 08.06.2018.
 */
public class GetServerTimeStep implements Step<PipeContext> {

    private static final Logger logger = Logger.getLogger(GetServerTimeStep.class);

    @Override
    public boolean execute(PipeContext pipeContext) {
        AuthResult authResult = pipeContext.getAuthResult();
        NodeConnection connection = pipeContext.getConnection();
        try {
            SecureNodeConnection secureNodeConnection =
                    new SecureNodeConnection(
                            connection,
                            pipeContext.getCredentials().getPrivateKey(),
                            authResult.getPublicKey());
            ServerTime serverTime = getServerTime(secureNodeConnection);
            pipeContext.setServerTime(serverTime);
            return true;
        } catch (IOException e) {
            logger.error("cannot  get master-server time due to network error", e);
            return false;
        }
    }

    private static ServerTime getServerTime(NodeConnection connection) throws IOException {
        NodeRequest timeSettingRequest = connection.receive();
        return new ServerTime(timeSettingRequest.getTimeStamp());
    }
}
