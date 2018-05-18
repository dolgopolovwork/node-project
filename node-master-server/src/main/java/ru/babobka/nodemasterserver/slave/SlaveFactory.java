package ru.babobka.nodemasterserver.slave;

import ru.babobka.nodemasterserver.listener.OnSlaveExitListener;
import ru.babobka.nodeutils.network.NodeConnection;

import java.util.Set;

/**
 * Created by 123 on 19.09.2017.
 */
public class SlaveFactory {
    public Slave create(Set<String> availableTasks, NodeConnection connection, OnSlaveExitListener onSlaveExitListener) {
        return new Slave(availableTasks, connection, onSlaveExitListener);
    }
}
