package ru.babobka.nodebusiness.service;

import ru.babobka.nodebusiness.dto.ConnectedSlave;

import java.util.List;

/**
 * Created by 123 on 22.07.2018.
 */
public interface NodeMasterInfoService {

    int totalNodes();

    int totalNodes(String taskName);

    List<ConnectedSlave> getConnectedSlaves();

    long getMasterStartTime();

}
