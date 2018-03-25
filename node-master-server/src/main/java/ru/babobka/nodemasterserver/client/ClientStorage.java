package ru.babobka.nodemasterserver.client;

import ru.babobka.nodeutils.container.Container;
import ru.babobka.nodeutils.logger.SimpleLogger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by 123 on 01.11.2017.
 */
public class ClientStorage {

    private final SimpleLogger logger = Container.getInstance().get(SimpleLogger.class);
    private final List<Client> clients = new ArrayList<>();

    public synchronized void add(Client client) {
        validateClient(client);
        clients.add(client);
    }

    public synchronized void addAll(List<Client> clients) {
        if (clients == null) {
            throw new IllegalArgumentException("cannot add null clients");
        }
        for (Client client : clients) {
            add(client);
        }
    }

    public synchronized void remove(Client client) {
        validateClient(client);
        clients.remove(client);
    }

    public synchronized void clear() {
        for (Client client : clients) {
            try {
                client.close();
            } catch (RuntimeException e) {
                logger.error(e);
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

    void sendHeartBeating(Client client) {
        try {
            client.sendHeartBeating();
        } catch (IOException e) {
            logger.error(e);
        }
    }

    boolean contains(Client client) {
        validateClient(client);
        return clients.contains(client);
    }

    void validateClient(Client client) {
        if (client == null) {
            throw new IllegalArgumentException("client is null");
        }
    }

    boolean isEmpty() {
        return clients.isEmpty();
    }
}
