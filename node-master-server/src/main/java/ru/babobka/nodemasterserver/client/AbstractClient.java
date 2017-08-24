package ru.babobka.nodemasterserver.client;

import ru.babobka.nodeserials.NodeRequest;
import ru.babobka.nodeserials.NodeResponse;
import ru.babobka.nodetask.model.StoppedTasks;
import ru.babobka.nodeutils.network.NodeConnection;

import java.io.IOException;
import java.util.UUID;

/**
 * Created by 123 on 01.11.2017.
 */
public abstract class AbstractClient implements Runnable {

    protected final UUID id = UUID.randomUUID();
    protected final NodeConnection connection;
    protected final NodeRequest request;


    public AbstractClient(NodeConnection connection, NodeRequest request, StoppedTasks stoppedTasks) {
        if (connection == null) {
            throw new IllegalArgumentException("connection is null");
        } else if (connection.isClosed()) {
            throw new IllegalArgumentException("connection is closed");
        } else if (request == null) {
            throw new IllegalArgumentException("can not process null request");
        } else if (stoppedTasks == null) {
            throw new IllegalArgumentException("stoppedTasks is null");
        }
        this.connection = connection;
        this.request = request;
    }

    public void sendHeartBeating() throws IOException {
        connection.send(NodeResponse.heartBeat());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Client client = (Client) o;
        return id.equals(client.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
