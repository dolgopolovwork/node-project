package ru.babobka.nodemasterserver.applyer;

import ru.babobka.nodemasterserver.exception.DistributionException;
import ru.babobka.nodemasterserver.service.DistributionService;
import ru.babobka.nodemasterserver.slave.SlavesStorage;
import ru.babobka.nodeserials.NodeRequest;
import ru.babobka.nodeutils.container.Container;
import ru.babobka.nodeutils.func.Applyer;
import ru.babobka.nodeutils.logger.SimpleLogger;

/**
 * Created by 123 on 10.09.2017.
 */
public class CancelAllTasksApplyer extends Applyer<NodeRequest> {

    private final SimpleLogger logger = Container.getInstance().get(SimpleLogger.class);
    private final DistributionService distributionService = Container.getInstance().get(DistributionService.class);
    private final SlavesStorage slavesStorage = Container.getInstance().get(SlavesStorage.class);

    @Override
    protected void applyImpl(NodeRequest request) {
        try {
            distributionService.broadcastStopRequests(slavesStorage.getListByTaskId(request.getTaskId()),
                    request.getTaskId());
        } catch (DistributionException e) {
            logger.error(e);
        }
    }
}