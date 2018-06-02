package ru.babobka.nodemasterserver.client;

import lombok.NonNull;
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

    AbstractClient(@NonNull NodeConnection connection, @NonNull NodeRequest request) {
        if (connection.isClosed()) {
            throw new IllegalArgumentException("connection is closed");
        }
        this.connection = connection;
        this.request = request;
    }

    public void sendHeartBeating() throws IOException {
        connection.send(NodeRequest.heartBeat());
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

    @Override
    public String toString() {
        return "AbstractClient{" +
                "connection=" + connection +
                ", id=" + id +
                '}';
    }
}
