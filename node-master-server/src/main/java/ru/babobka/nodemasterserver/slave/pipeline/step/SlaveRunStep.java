package ru.babobka.nodemasterserver.slave.pipeline.step;

import ru.babobka.nodemasterserver.server.config.MasterServerConfig;
import ru.babobka.nodemasterserver.slave.Sessions;
import ru.babobka.nodemasterserver.slave.Slave;
import ru.babobka.nodemasterserver.slave.SlaveFactory;
import ru.babobka.nodemasterserver.slave.SlavesStorage;
import ru.babobka.nodemasterserver.slave.pipeline.PipeContext;
import ru.babobka.nodesecurity.auth.AuthResult;
import ru.babobka.nodesecurity.network.SecureNodeConnection;
import ru.babobka.nodeserials.NodeRequest;
import ru.babobka.nodeutils.container.Container;
import ru.babobka.nodeutils.func.pipeline.Step;
import ru.babobka.nodeutils.logger.NodeLogger;
import ru.babobka.nodeutils.network.NodeConnection;

import java.io.IOException;
import java.util.Set;

/**
 * Created by 123 on 07.06.2018.
 */
public class SlaveRunStep implements Step<PipeContext> {
    private final NodeLogger nodeLogger = Container.getInstance().get(NodeLogger.class);
    private final Sessions sessions = Container.getInstance().get(Sessions.class);
    private final SlaveFactory slaveFactory = Container.getInstance().get(SlaveFactory.class);
    private final SlavesStorage slavesStorage = Container.getInstance().get(SlavesStorage.class);
    private final MasterServerConfig config = Container.getInstance().get(MasterServerConfig.class);

    @Override
    public boolean execute(PipeContext pipeContext) {
        NodeConnection connection = pipeContext.getConnection();
        AuthResult authResult = pipeContext.getAuthResult();
        Set<String> availableTasks = pipeContext.getAvailableTasks();
        try {
            SecureNodeConnection secureNodeConnection = new SecureNodeConnection(connection, authResult.getSecretKey());
            if (!runNewSlave(availableTasks, authResult.getUserName(), secureNodeConnection)) {
                nodeLogger.warning("cannot run slave");
                sessions.remove(authResult.getUserName());
                connection.send(false);
                return false;
            }
            connection.send(true);
            secureNodeConnection.send(NodeRequest.time());
            return true;
        } catch (IOException e) {
            nodeLogger.error("cannot run slave due to network error", e);
            return false;
        }
    }

    private boolean runNewSlave(Set<String> availableTasks, String userName, SecureNodeConnection secureNodeConnection) {
        try {
            Slave slave = slaveFactory.create(availableTasks, secureNodeConnection, () -> {
                if (config.getModes().isSingleSessionMode()) {
                    sessions.remove(userName);
                }
            });
            if (slavesStorage.add(slave)) {
                slave.start();
                return true;
            }
            return false;
        } catch (RuntimeException e) {
            nodeLogger.error("cannot run new slave", e);
            return false;
        }
    }
}
