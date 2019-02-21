package ru.babobka.nodemasterserver.client;

import lombok.NonNull;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by 123 on 01.11.2017.
 */
public class ClientStorage {

    private static final Logger logger = Logger.getLogger(ClientStorage.class);
    private final List<Client> clients = new ArrayList<>();
    private boolean closed;

    private synchronized boolean isClosed() {
        return closed;
    }

    public synchronized void closeStorage() {
        this.closed = true;
    }

    public synchronized void add(@NonNull Client client) {
        if (!isClosed()) {
            logger.info("add new client " + client + " to client storage");
            clients.add(client);
        } else {
            logger.info("new client was not added to client client storage due to closed storage status");
        }
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
                logger.error("exception thrown", e);
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
            logger.error("exception thrown", e);
        }
    }

    boolean contains(@NonNull Client client) {
        return clients.contains(client);
    }


    boolean isEmpty() {
        return clients.isEmpty();
    }
}
