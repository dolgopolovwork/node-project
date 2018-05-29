package ru.babobka.nodemasterserver.slave;

import lombok.NonNull;
import ru.babobka.nodemasterserver.applyer.CancelAllTasksApplyer;
import ru.babobka.nodemasterserver.applyer.GroupTaskApplyer;
import ru.babobka.nodemasterserver.applyer.StopTaskApplyer;
import ru.babobka.nodemasterserver.exception.DistributionException;
import ru.babobka.nodemasterserver.model.ResponseStorage;
import ru.babobka.nodemasterserver.server.config.MasterServerConfig;
import ru.babobka.nodemasterserver.service.DistributionService;
import ru.babobka.nodeserials.NodeRequest;
import ru.babobka.nodeserials.NodeResponse;
import ru.babobka.nodeserials.enumerations.RequestStatus;
import ru.babobka.nodeserials.enumerations.ResponseStatus;
import ru.babobka.nodeutils.container.Container;
import ru.babobka.nodeutils.func.Applyer;
import ru.babobka.nodeutils.logger.NodeLogger;
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
    private final NodeLogger nodeLogger = Container.getInstance().get(NodeLogger.class);
    protected final NodeConnection connection;

    AbstractNetworkSlave(@NonNull NodeConnection connection) {
        if (connection.isClosed()) {
            throw new IllegalArgumentException("connection is closed");
        }
        this.connection = connection;
    }

    @Override
    public void run() {
        nodeLogger.debug("slave " + this.getSlaveId() + " is running");
        try {
            while (!isInterrupted()) {
                connection.setReadTimeOut(masterServerConfig.getTimeouts().getRequestTimeOutMillis());
                NodeResponse response = connection.receive();
                if (response.getStatus() != ResponseStatus.HEART_BEAT) {
                    onReceive(response);
                }
            }
        } catch (IOException | RuntimeException e) {
            e.printStackTrace();
            if (!isInterrupted() && !connection.isClosed()) {
                nodeLogger.error(e);
            }
        } finally {
            nodeLogger.info("removing connection " + connection);
            synchronized (AbstractSlave.class) {
                onExit();
            }
            nodeLogger.info("slave " + getSlaveId() + " was disconnected");
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
        getConnection().send(NodeRequest.heartBeat());
    }

    public Map<String, List<NodeRequest>> getRequestsGroupedByTasks() {
        GroupTaskApplyer groupTaskApplyer = new GroupTaskApplyer();
        applyToTasks(groupTaskApplyer);
        return groupTaskApplyer.getGroupedTasks();
    }

    public synchronized void executeTask(NodeRequest request) throws IOException {
        nodeLogger.info("send request " + request + " to slave " + getSlaveId());
        boolean closedConnection = getConnection().isClosed();
        if (closedConnection) {
            nodeLogger.warning("connection of slave " + getSlaveId() + " was closed");
            throw new IOException("closed connection");
        } else if (hasRequest(request)) {
            nodeLogger.warning("slave " + getSlaveId() + " already has request with id " + request.getId());
        } else if (!(request.getRequestStatus() == RequestStatus.RACE && hasTask(request.getTaskId()))) {
            addTask(request);
            getConnection().send(request);
            nodeLogger.info(request + " was sent by slave " + getSlaveId());
        } else {
            nodeLogger.info("request  " + request + " was ignored due to race style");
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
