package ru.babobka.nodemasterserver.slave;

import ru.babobka.nodemasterserver.exception.DistributionException;
import ru.babobka.nodemasterserver.listener.OnSlaveExitListener;
import ru.babobka.nodemasterserver.model.ResponseStorage;
import ru.babobka.nodeserials.NodeResponse;
import ru.babobka.nodeutils.container.Container;
import ru.babobka.nodeutils.logger.SimpleLogger;
import ru.babobka.nodeutils.network.NodeConnection;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by dolgopolov.a on 27.07.15.
 */
public class Slave extends AbstractNetworkSlave {

    private final ResponseStorage responseStorage = Container.getInstance().get(ResponseStorage.class);
    private final SimpleLogger logger = Container.getInstance().get(SimpleLogger.class);
    private final SlavesStorage slavesStorage = Container.getInstance().get(SlavesStorage.class);
    private final Set<String> availableTasks = new HashSet<>();
    private final OnSlaveExitListener onSlaveExitListener;

    public Slave(Set<String> availableTasks, NodeConnection connection) {
        this(availableTasks, connection, null);
    }

    public Slave(Set<String> availableTasks, NodeConnection connection, OnSlaveExitListener onSlaveExitListener) {
        super(connection);
        if (availableTasks == null) {
            throw new IllegalArgumentException("available tasks must be set");
        }
        this.availableTasks.addAll(availableTasks);
        this.onSlaveExitListener = onSlaveExitListener;
    }

    @Override
    protected synchronized void onReceive(NodeResponse response) {
        logger.info("received " + response + " by slave " + getSlaveId());
        if (responseStorage.exists(response.getTaskId())) {
            responseStorage.get(response.getTaskId()).add(response);
        } else {
            logger.warning("response was not created " + response + " for slave " + getSlaveId());
        }
        removeTask(response);
    }

    @Override
    protected synchronized void onExit() {
        slavesStorage.remove(this);
        if (!isNoTasks()) {
            logger.debug("slave " + getSlaveId() + " has a requests to redistribute " + getTasks());
            try {
                redistributeTasks();
            } catch (IOException e) {
                logger.error(e);
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
        logger.info("slave " + getSlaveId() + " closed connection");
    }

    private void executeExitListener() {
        try {
            if (onSlaveExitListener != null) {
                onSlaveExitListener.onExit();
            }
        } catch (RuntimeException e) {
            logger.error("cannot execute exit listener", e);
        }
    }


    public boolean taskIsAvailable(String taskName) {
        return availableTasks.contains(taskName);
    }

}