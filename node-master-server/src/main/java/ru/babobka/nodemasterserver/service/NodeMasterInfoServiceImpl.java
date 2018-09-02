package ru.babobka.nodemasterserver.service;

import ru.babobka.nodebusiness.service.NodeMasterInfoService;
import ru.babobka.nodemasterserver.slave.SlavesStorage;
import ru.babobka.nodeutils.container.Container;

/**
 * Created by 123 on 22.07.2018.
 */
public class NodeMasterInfoServiceImpl implements NodeMasterInfoService {

    private final SlavesStorage slavesStorage = Container.getInstance().get(SlavesStorage.class);

    @Override
    public int totalNodes() {
        return slavesStorage.getClusterSize();
    }

    @Override
    public int totalNodes(String taskName) {
        return slavesStorage.getClusterSize(taskName);
    }
}
