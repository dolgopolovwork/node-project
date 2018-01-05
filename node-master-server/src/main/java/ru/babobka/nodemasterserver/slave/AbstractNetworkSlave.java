package ru.babobka.nodemasterserver.slave;

import ru.babobka.nodemasterserver.applyer.CancelAllTasksApplyer;
import ru.babobka.nodemasterserver.applyer.GroupTaskApplyer;
import ru.babobka.nodemasterserver.applyer.StopTaskApplyer;
import ru.babobka.nodemasterserver.exception.DistributionException;
import ru.babobka.nodemasterserver.model.ResponseStorage;
import ru.babobka.nodemasterserver.server.MasterServerConfig;
import ru.babobka.nodemasterserver.service.DistributionService;
import ru.babobka.nodeserials.NodeRequest;
import ru.babobka.nodeserials.NodeResponse;
import ru.babobka.nodeserials.enumerations.RequestStatus;
import ru.babobka.nodeserials.enumerations.ResponseStatus;
import ru.babobka.nodeutils.container.Container;
import ru.babobka.nodeutils.func.Applyer;
import ru.babobka.nodeutils.logger.SimpleLogger;
import ru.babobka.nodeutils.network.NodeConnection;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Created by 123 on 24.08.2017.
 */
public abstract class AbstractNetworkSlave extends AbstractSlave {
    private final MasterServerConfig masterServerConfig = Container.getInstance().get(MasterServerConfig.class);
    private final DistributionService distributionService = Container.getInstance().get(DistributionService.class);
    private final ResponseStorage responseStorage = Container.getInstance().get(ResponseStorage.class);
    private final SimpleLogger logger = Container.getInstance().get(SimpleLogger.class);
    private final NodeConnection connection;

    AbstractNetworkSlave(NodeConnection connection) {
        if (connection == null) {
            throw new IllegalArgumentException("Connection is null");
        } else if (connection.isClosed()) {
            throw new IllegalArgumentException("Connection is closed");
        }
        this.connection = connection;
        logger.info("New connection " + connection + " slaveId: " + getSlaveId());
    }


    @Override
    public void run() {
        try {
            while (!isInterrupted()) {
                connection.setReadTimeOut(masterServerConfig.getRequestTimeOutMillis());
                NodeResponse response = connection.receive();
                if (response.getStatus() != ResponseStatus.HEART_BEAT) {
                    onReceive(response);
                }
            }
        } catch (IOException | RuntimeException e) {
            if (!isInterrupted() && !connection.isClosed()) {
                logger.error(e);
            }
        } finally {
            logger.info("Removing connection " + connection);
            synchronized (AbstractSlave.class) {
                onExit();
            }
            logger.info("Slave " + getSlaveId() + " was disconnected");
        }
    }

    @Override
    public void interrupt() {
        super.interrupt();
        connection.close();
    }

    NodeConnection getConnection() {
        return connection;
    }

    void sendHeartBeating() throws IOException {
        getConnection().send(NodeRequest.heartBeatRequest());
    }

    public Map<String, List<NodeRequest>> getRequestsGroupedByTasks() {
        GroupTaskApplyer groupTaskApplyer = new GroupTaskApplyer();
        applyToTasks(groupTaskApplyer);
        return groupTaskApplyer.getGroupedTasks();
    }

    public synchronized void executeTask(NodeRequest request) throws IOException {
        boolean closedConnection = getConnection().isClosed();
        logger.info("send request " + request + " to slave " + getSlaveId());
        if (closedConnection) {
            logger.warning("connection of slave " + getSlaveId() + " was closed");
            throw new IOException("Closed connection");
        } else if (hasRequest(request)) {
            logger.warning("Slave " + getSlaveId() + " already has request with id " + request.getId());
        } else if (!(request.getRequestStatus() == RequestStatus.RACE && hasTask(request.getTaskId()))) {
            addTask(request);
            getConnection().send(request);
            logger.info(request + " was sent by slave " + getSlaveId());
        } else {
            logger.info("Request  " + request + " was ignored due to race style");
            responseStorage.get(request.getTaskId()).add(NodeResponse.dummy(request));
        }
    }

    public void stopTask(UUID taskId) throws IOException {
        applyToTasks(new StopTaskApplyer(taskId, this));
        getConnection().send(NodeRequest.stop(taskId));
    }

    public void cancelAllTasks() {
        applyToTasks(new CancelAllTasksApplyer());
    }

    public void setBadStatusForAllTasks() {
        applyToTasks(new Applyer<NodeRequest>() {
            @Override
            protected void applyImpl(NodeRequest request) {
                responseStorage.addBadResponse(request);
            }
        });
    }

    public synchronized void redistributeTasks() throws IOException {
        try {
            distributionService.redistribute(this);
        } catch (DistributionException e) {
            throw new IOException(e);
        }
    }

}
