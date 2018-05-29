package ru.babobka.nodemasterserver.client;

import lombok.NonNull;
import ru.babobka.nodeutils.container.Container;
import ru.babobka.nodeutils.logger.NodeLogger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by 123 on 01.11.2017.
 */
public class ClientStorage {

    private final NodeLogger nodeLogger = Container.getInstance().get(NodeLogger.class);
    private final List<Client> clients = new ArrayList<>();

    public synchronized void add(@NonNull Client client) {
        clients.add(client);
    }

    public synchronized void addAll(@NonNull List<Client> clients) {
        for (Client client : clients) {
            add(client);
        }
    }

    public synchronized void remove(@NonNull Client client) {
        clients.remove(client);
    }

    public synchronized void clear() {
        for (Client client : clients) {
            try {
                client.close();
            } catch (RuntimeException e) {
                nodeLogger.error(e);
            }
        }
        clients.clear();
    }

    public synchronized void heartBeatAllClients() {
        for (Client client : clients) {
            if (!Thread.currentThread().isInterrupted()) {
                sendHeartBeating(client);
            }
        }
    }

    public synchronized int getSize() {
        return clients.size();
    }

    void sendHeartBeating(@NonNull Client client) {
        try {
            client.sendHeartBeating();
        } catch (IOException e) {
            nodeLogger.error(e);
        }
    }

    boolean contains(@NonNull Client client) {
        return clients.contains(client);
    }


    boolean isEmpty() {
        return clients.isEmpty();
    }
}
