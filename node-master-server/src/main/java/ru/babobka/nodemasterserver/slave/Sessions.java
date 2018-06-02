package ru.babobka.nodemasterserver.slave;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by 123 on 16.05.2018.
 */
public class Sessions {
    private final Set<String> activeSessions = new HashSet<>();

    public synchronized boolean put(String userName) {
        if (activeSessions.contains(userName)) {
            return false;
        }
        activeSessions.add(userName);
        return true;
    }

    public synchronized boolean contains(String userName) {
        return activeSessions.contains(userName);
    }

    public synchronized void remove(String userName) {
        activeSessions.remove(userName);
    }

    public synchronized void clear() {
        activeSessions.clear();
    }

    public synchronized boolean isEmpty() {
        return activeSessions.isEmpty();
    }
}
