package ru.babobka.nodemasterserver.listener;

import ru.babobka.nodemasterserver.service.DistributionService;
import ru.babobka.nodemasterserver.slave.Slave;
import ru.babobka.nodemasterserver.slave.SlavesStorage;
import ru.babobka.nodeserials.NodeResponse;
import ru.babobka.nodeutils.container.Container;
import ru.babobka.nodeutils.logger.NodeLogger;

import java.util.List;

/**
 * Created by 123 on 15.07.2017.
 */
public class OnRaceStyleTaskIsReady implements OnResponseListener {

    private final DistributionService distributionService = Container.getInstance().get(DistributionService.class);
    private final NodeLogger nodeLogger = Container.getInstance().get(NodeLogger.class);
    private final SlavesStorage slavesStorage = Container.getInstance().get(SlavesStorage.class);

    @Override
    public void onResponse(NodeResponse response) {
        List<Slave> slaves = slavesStorage.getListByTaskId(response);
        if (!slaves.isEmpty()) {
            nodeLogger.debug("cancel all requests for task id " + response.getTaskId());
            distributionService.broadcastStopRequests(slaves,
                    response.getTaskId());
        }
    }
}
