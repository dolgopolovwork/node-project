package ru.babobka.nodemasterserver.service;

import ru.babobka.nodebusiness.dto.ConnectedSlave;
import ru.babobka.nodebusiness.service.NodeMasterInfoService;
import ru.babobka.nodemasterserver.slave.SlavesStorage;
import ru.babobka.nodeutils.container.Container;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by 123 on 22.07.2018.
 */
public class NodeMasterInfoServiceImpl implements NodeMasterInfoService {

    private final long startTimeMillis = System.currentTimeMillis();
    private final SlavesStorage slavesStorage = Container.getInstance().get(SlavesStorage.class);

    @Override
    public int totalNodes() {
        return slavesStorage.getClusterSize();
    }

    @Override
    public int totalNodes(String taskName) {
        return slavesStorage.getClusterSize(taskName);
    }

    @Override
    public List<ConnectedSlave> getConnectedSlaves() {
        return slavesStorage.getFullList().stream().map(slave ->
                new ConnectedSlave(
                        slave.getSlaveId(),
                        slave.getAddress(),
                        slave.getUserName()))
                .collect(Collectors.toList());
    }

    @Override
    public long getMasterStartTime() {
        return startTimeMillis;
    }
}