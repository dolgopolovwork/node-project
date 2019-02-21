package ru.babobka.nodemasterserver.listener;

import org.apache.log4j.Logger;
import ru.babobka.nodemasterserver.service.DistributionService;
import ru.babobka.nodemasterserver.slave.Slave;
import ru.babobka.nodemasterserver.slave.SlavesStorage;
import ru.babobka.nodeserials.NodeResponse;
import ru.babobka.nodeutils.container.Container;

import java.util.List;

/**
 * Created by 123 on 15.07.2017.
 */
public class OnRaceStyleTaskIsReady implements OnResponseListener {

    private static final Logger logger = Logger.getLogger(OnRaceStyleTaskIsReady.class);
    private final DistributionService distributionService = Container.getInstance().get(DistributionService.class);
    private final SlavesStorage slavesStorage = Container.getInstance().get(SlavesStorage.class);

    @Override
    public void onResponse(NodeResponse response) {
        List<Slave> slaves = slavesStorage.getListByTaskId(response);
        if (!slaves.isEmpty()) {
            logger.debug("cancel all requests for task id " + response.getTaskId());
            distributionService.broadcastStopRequests(slaves,
                    response.getTaskId());
        }
    }
}
