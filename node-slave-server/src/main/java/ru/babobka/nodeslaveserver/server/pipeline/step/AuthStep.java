package ru.babobka.nodeslaveserver.server.pipeline.step;

import ru.babobka.nodesecurity.auth.AuthCredentials;
import ru.babobka.nodesecurity.auth.AuthResult;
import ru.babobka.nodeslaveserver.server.pipeline.PipeContext;
import ru.babobka.nodeslaveserver.service.SlaveAuthService;
import ru.babobka.nodeutils.container.Container;
import ru.babobka.nodeutils.func.pipeline.Step;
import ru.babobka.nodeutils.logger.NodeLogger;
import ru.babobka.nodeutils.network.NodeConnection;

import java.io.IOException;

/**
 * Created by 123 on 08.06.2018.
 */
public class AuthStep implements Step<PipeContext> {

    private final NodeLogger nodeLogger = Container.getInstance().get(NodeLogger.class);
    private final SlaveAuthService authService = Container.getInstance().get(SlaveAuthService.class);

    @Override
    public boolean execute(PipeContext pipeContext) {
        NodeConnection connection = pipeContext.getConnection();
        AuthCredentials credentials = pipeContext.getCredentials();
        try {
            AuthResult authResult = authService.authClient(connection, credentials.getLogin(), credentials.getPassword());
            pipeContext.setAuthResult(authResult);
            if (!authResult.isSuccess()) {
                nodeLogger.error("authentication fail");
                return false;
            }
            nodeLogger.info("authentication success");
            return true;
        } catch (IOException e) {
            nodeLogger.error("authentication fail due to network error");
            return false;
        }
    }
}
