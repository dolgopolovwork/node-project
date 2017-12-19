package ru.babobka.nodemasterserver.client;

import ru.babobka.nodeserials.NodeRequest;
import ru.babobka.nodeutils.network.NodeConnection;

import java.io.IOException;
import java.util.UUID;

/**
 * Created by 123 on 01.11.2017.
 */
public abstract class AbstractClient implements Runnable {

    protected final NodeConnection connection;
    protected final NodeRequest request;
    private final UUID id = UUID.randomUUID();


    AbstractClient(NodeConnection connection, NodeRequest request) {
        if (connection == null) {
            throw new IllegalArgumentException("connection is null");
        } else if (connection.isClosed()) {
            throw new IllegalArgumentException("connection is closed");
        } else if (request == null) {
            throw new IllegalArgumentException("can not process null request");
        }
        this.connection = connection;
        this.request = request;
    }

    public void sendHeartBeating() throws IOException {
        connection.send(NodeRequest.heartBeatRequest());
    }

    public UUID getId() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AbstractClient that = (AbstractClient) o;

        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
