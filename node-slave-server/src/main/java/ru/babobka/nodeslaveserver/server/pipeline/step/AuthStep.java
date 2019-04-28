package ru.babobka.nodeslaveserver.server.pipeline.step;

import org.apache.log4j.Logger;
import ru.babobka.nodesecurity.auth.AuthCredentials;
import ru.babobka.nodesecurity.auth.AuthResult;
import ru.babobka.nodeslaveserver.server.pipeline.PipeContext;
import ru.babobka.nodeslaveserver.service.SlaveAuthService;
import ru.babobka.nodeutils.container.Container;
import ru.babobka.nodeutils.func.pipeline.Step;
import ru.babobka.nodeutils.network.NodeConnection;

import java.io.IOException;

/**
 * Created by 123 on 08.06.2018.
 */
public class AuthStep implements Step<PipeContext> {

    private static final Logger logger = Logger.getLogger(AuthStep.class);
    private final SlaveAuthService authService = Container.getInstance().get(SlaveAuthService.class);

    @Override
    public boolean execute(PipeContext pipeContext) {
        NodeConnection connection = pipeContext.getConnection();
        AuthCredentials credentials = pipeContext.getCredentials();
        try {
            AuthResult authResult = authService.authClient(connection, credentials.getLogin(), credentials.getPassword());
            pipeContext.setAuthResult(authResult);
            if (!authResult.isSuccess()) {
                logger.error("authentication fail");
                return false;
            }
            logger.info("authentication success");
            return true;
        } catch (IOException e) {
            logger.error("authentication fail due to network error", e);
            return false;
        }
    }
}
