package ru.babobka.nodebusiness.dao;

import ru.babobka.nodebusiness.model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class DebugNodeUsersDAOImpl implements NodeUsersDAO {

    private final Map<UUID, User> debugDataMap;

    public DebugNodeUsersDAOImpl(Map<UUID, User> debugDataMap) {
        this.debugDataMap = debugDataMap;
    }

    @Override
    public synchronized User get(UUID id) {
        return debugDataMap.get(id);
    }

    @Override
    public synchronized User get(String login) {
        for (Map.Entry<UUID, User> userEntry : debugDataMap.entrySet()) {
            if (userEntry.getValue().getName().equals(login)) {
                return userEntry.getValue();
            }
        }
        return null;
    }

    @Override
    public synchronized List<User> getList() {
        List<User> users = new ArrayList<>();
        for (Map.Entry<UUID, User> userEntry : debugDataMap.entrySet()) {
            users.add(userEntry.getValue());
        }
        return users;
    }

    @Override
    public synchronized void add(User user) {
        debugDataMap.put(user.getId(), user);
    }

    @Override
    public synchronized boolean exists(String login) {
        for (Map.Entry<UUID, User> userEntry : debugDataMap.entrySet()) {
            if (userEntry.getValue().getName().equals(login)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public synchronized boolean remove(UUID id) {
        return debugDataMap.remove(id) != null;
    }

    @Override
    public synchronized boolean update(UUID id, User user) {
        User foundUser = get(id);
        if (foundUser == null) {
            return false;
        }
        if (user.getEmail() != null)
            foundUser.setEmail(user.getEmail());
        if (user.getName() != null)
            foundUser.setName(user.getName());
        if (user.getTaskCount() != null)
            foundUser.setTaskCount(user.getTaskCount());
        if (user.getHashedPassword() != null)
            foundUser.setHashedPassword(user.getHashedPassword());

        return true;
    }
}
