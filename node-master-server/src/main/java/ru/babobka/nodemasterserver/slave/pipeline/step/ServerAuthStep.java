package ru.babobka.nodemasterserver.slave.pipeline.step;

import ru.babobka.nodemasterserver.service.MasterAuthService;
import ru.babobka.nodemasterserver.slave.pipeline.PipeContext;
import ru.babobka.nodeutils.container.Container;
import ru.babobka.nodeutils.func.pipeline.Step;
import ru.babobka.nodeutils.logger.NodeLogger;
import ru.babobka.nodeutils.network.NodeConnection;

import java.io.IOException;

/**
 * Created by 123 on 07.06.2018.
 */
public class ServerAuthStep implements Step<PipeContext> {
    private final MasterAuthService authService = Container.getInstance().get(MasterAuthService.class);
    private final NodeLogger nodeLogger = Container.getInstance().get(NodeLogger.class);

    @Override
    public boolean execute(PipeContext pipeContext) {
        NodeConnection connection = pipeContext.getConnection();
        try {
            if (!authService.authServer(connection)) {
                nodeLogger.error("server authentication fail");
                return false;
            }
            return true;
        } catch (IOException e) {
            nodeLogger.error("cannot authenticate master-server due to network error");
            return false;
        }
    }
}
