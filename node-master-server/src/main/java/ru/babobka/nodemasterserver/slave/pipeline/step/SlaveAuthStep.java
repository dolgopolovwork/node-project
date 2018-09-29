package ru.babobka.nodemasterserver.slave.pipeline.step;

import ru.babobka.nodeconfigs.master.MasterServerConfig;
import ru.babobka.nodemasterserver.service.MasterAuthService;
import ru.babobka.nodemasterserver.slave.pipeline.PipeContext;
import ru.babobka.nodesecurity.auth.AuthResult;
import ru.babobka.nodeutils.container.Container;
import ru.babobka.nodeutils.func.pipeline.Step;
import ru.babobka.nodeutils.logger.NodeLogger;
import ru.babobka.nodeutils.network.NodeConnection;

import java.io.IOException;

/**
 * Created by 123 on 08.06.2018.
 */
public class SlaveAuthStep implements Step<PipeContext> {
    private final MasterAuthService authService = Container.getInstance().get(MasterAuthService.class);
    private final MasterServerConfig config = Container.getInstance().get(MasterServerConfig.class);
    private final NodeLogger nodeLogger = Container.getInstance().get(NodeLogger.class);

    @Override
    public boolean execute(PipeContext context) {
        NodeConnection connection = context.getConnection();
        try {
            connection.setReadTimeOut(config.getTime().getAuthTimeOutMillis());
            AuthResult authResult = authService.authClient(connection);
            context.setAuthResult(authResult);
            if (!authResult.isSuccess()) {
                nodeLogger.warning("auth fail");
                return false;
            }
            nodeLogger.info("new slave was successfully authenticated");
            return true;
        } catch (IOException e) {
            nodeLogger.error("cannot authenticate slave server due to network error");
            return false;
        }
    }
}
