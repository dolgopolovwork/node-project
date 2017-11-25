package ru.babobka.nodemasterserver.slave;

import ru.babobka.nodemasterserver.applyer.CancelAllTasksApplyer;
import ru.babobka.nodemasterserver.applyer.GroupTaskApplyer;
import ru.babobka.nodemasterserver.applyer.StopTaskApplyer;
import ru.babobka.nodemasterserver.exception.DistributionException;
import ru.babobka.nodemasterserver.model.ResponseStorage;
import ru.babobka.nodemasterserver.service.DistributionService;
import ru.babobka.nodeserials.NodeRequest;
import ru.babobka.nodeserials.NodeResponse;
import ru.babobka.nodeserials.enumerations.RequestStatus;
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

    private final DistributionService distributionService = Container.getInstance().get(DistributionService.class);
    private final ResponseStorage responseStorage = Container.getInstance().get(ResponseStorage.class);
    private final SimpleLogger logger = Container.getInstance().get(SimpleLogger.class);

    AbstractNetworkSlave(NodeConnection connection) {
        super(connection);
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
