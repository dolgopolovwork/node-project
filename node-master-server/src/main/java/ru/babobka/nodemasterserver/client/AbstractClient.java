package ru.babobka.nodemasterserver.client;

import lombok.NonNull;
import ru.babobka.nodeserials.NodeRequest;
import ru.babobka.nodeutils.network.NodeConnection;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

/**
 * Created by 123 on 01.11.2017.
 */
public abstract class AbstractClient extends Thread {

    protected final NodeConnection connection;
    protected final List<NodeRequest> requests;
    private final UUID id = UUID.randomUUID();

    AbstractClient(@NonNull NodeConnection connection, @NonNull List<NodeRequest> requests) {
        if (connection.isClosed()) {
            throw new IllegalArgumentException("connection is closed");
        }
        this.connection = connection;
        this.requests = requests;
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
