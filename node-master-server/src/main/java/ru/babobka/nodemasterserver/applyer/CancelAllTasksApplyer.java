package ru.babobka.nodemasterserver.applyer;

import ru.babobka.nodemasterserver.service.DistributionService;
import ru.babobka.nodemasterserver.slave.SlavesStorage;
import ru.babobka.nodeserials.NodeRequest;
import ru.babobka.nodeutils.container.Container;
import ru.babobka.nodeutils.func.Applyer;

/**
 * Created by 123 on 10.09.2017.
 */
public class CancelAllTasksApplyer extends Applyer<NodeRequest> {
    private final DistributionService distributionService = Container.getInstance().get(DistributionService.class);
    private final SlavesStorage slavesStorage = Container.getInstance().get(SlavesStorage.class);

    @Override
    protected void applyImpl(NodeRequest request) {
        distributionService.broadcastStopRequests(slavesStorage.getListByTaskId(request.getTaskId()),
                request.getTaskId());
    }
}