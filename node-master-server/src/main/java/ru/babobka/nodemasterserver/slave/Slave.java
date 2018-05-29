package ru.babobka.nodemasterserver.slave;

import lombok.NonNull;
import ru.babobka.nodemasterserver.exception.DistributionException;
import ru.babobka.nodemasterserver.listener.OnSlaveExitListener;
import ru.babobka.nodemasterserver.model.ResponseStorage;
import ru.babobka.nodeserials.NodeResponse;
import ru.babobka.nodeutils.container.Container;
import ru.babobka.nodeutils.logger.NodeLogger;
import ru.babobka.nodeutils.network.NodeConnection;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by dolgopolov.a on 27.07.15.
 */
public class Slave extends AbstractNetworkSlave {

    private final ResponseStorage responseStorage = Container.getInstance().get(ResponseStorage.class);
    private final NodeLogger nodeLogger = Container.getInstance().get(NodeLogger.class);
    private final SlavesStorage slavesStorage = Container.getInstance().get(SlavesStorage.class);
    private final Set<String> availableTasks = new HashSet<>();
    private final OnSlaveExitListener onSlaveExitListener;

    public Slave(Set<String> availableTasks, NodeConnection connection) {
        this(availableTasks, connection, null);
    }

    public Slave(@NonNull Set<String> availableTasks, NodeConnection connection, OnSlaveExitListener onSlaveExitListener) {
        super(connection);
        this.availableTasks.addAll(availableTasks);
        this.onSlaveExitListener = onSlaveExitListener;
    }

    @Override
    protected synchronized void onReceive(NodeResponse response) {
        nodeLogger.info("received " + response + " by slave " + getSlaveId());
        if (responseStorage.exists(response.getTaskId())) {
            responseStorage.get(response.getTaskId()).add(response);
        } else {
            nodeLogger.warning("response was not created " + response + " for slave " + getSlaveId());
        }
        removeTask(response);
    }

    @Override
    protected synchronized void onExit() {
        slavesStorage.remove(this);
        if (!isNoTasks()) {
            nodeLogger.debug("slave " + getSlaveId() + " has a requests to redistribute " + getTasks());
            try {
                redistributeTasks();
            } catch (IOException e) {
                nodeLogger.error(e);
                Throwable cause = e.getCause();
                setBadStatusForAllTasks();
                if (cause != null && cause instanceof DistributionException) {
                    cancelAllTasks();
                }
                clearTasks();
            }
        }
        getConnection().close();
        executeExitListener();
        nodeLogger.info("slave " + getSlaveId() + " closed connection");
    }

    private void executeExitListener() {
        try {
            if (onSlaveExitListener != null) {
                onSlaveExitListener.onExit();
            }
        } catch (RuntimeException e) {
            nodeLogger.error("cannot execute exit listener", e);
        }
    }

    public boolean taskIsAvailable(String taskName) {
        return availableTasks.contains(taskName);
    }

}