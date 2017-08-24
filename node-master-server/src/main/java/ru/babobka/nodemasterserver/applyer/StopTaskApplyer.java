package ru.babobka.nodemasterserver.applyer;

import ru.babobka.nodemasterserver.model.ResponseStorage;
import ru.babobka.nodemasterserver.slave.AbstractNetworkSlave;
import ru.babobka.nodeserials.NodeRequest;
import ru.babobka.nodeutils.container.Container;
import ru.babobka.nodeutils.func.Applyer;

import java.util.UUID;

/**
 * Created by 123 on 10.09.2017.
 */
public class StopTaskApplyer extends Applyer<NodeRequest> {
    private final UUID taskId;
    private final AbstractNetworkSlave slave;
    private final ResponseStorage responseStorage = Container.getInstance().get(ResponseStorage.class);

    public StopTaskApplyer(UUID taskId, AbstractNetworkSlave slave) {
        if (taskId == null) {
            throw new IllegalArgumentException("taskId is null");
        } else if (slave == null) {
            throw new IllegalArgumentException("slave is null");
        }
        this.taskId = taskId;
        this.slave = slave;
    }

    @Override
    protected void applyImpl(NodeRequest request) {
        if (request.getTaskId().equals(taskId)) {
            responseStorage.addStopResponse(taskId);
            slave.removeTask(request);
        }
    }
}
