package ru.babobka.nodetester.dao;

import ru.babobka.nodebusiness.dao.user.NodeUsersDAO;
import ru.babobka.nodebusiness.model.User;

import java.util.*;

public class DummyNodeUserDAOImpl implements NodeUsersDAO {
    private final Map<String, User> debugDataMap = new HashMap<>();

    @Override
    public synchronized User get(UUID id) {
        return debugDataMap.get(id.toString());
    }

    @Override
    public synchronized User get(String login) {
        for (Map.Entry<String, User> userEntry : debugDataMap.entrySet()) {
            if (userEntry.getValue().getName().equals(login)) {
                return userEntry.getValue();
            }
        }
        return null;
    }

    @Override
    public synchronized List<User> getList() {
        List<User> users = new ArrayList<>();
        for (Map.Entry<String, User> userEntry : debugDataMap.entrySet()) {
            users.add(userEntry.getValue());
        }
        return users;
    }

    @Override
    public synchronized boolean add(User user) {
        debugDataMap.put(user.getId(), user);
        return true;
    }

    @Override
    public synchronized boolean exists(String login) {
        for (Map.Entry<String, User> userEntry : debugDataMap.entrySet()) {
            if (userEntry.getValue().getName().equals(login)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public synchronized boolean remove(UUID id) {
        return debugDataMap.remove(id.toString()) != null;
    }

    @Override
    public synchronized boolean update(User user) {
        User foundUser = get(user.getId());
        if (foundUser == null) {
            return false;
        }
        if (user.getEmail() != null)
            foundUser.setEmail(user.getEmail());
        if (user.getName() != null)
            foundUser.setName(user.getName());
        if (user.getPublicKeyBase64() != null) {
            foundUser.setPublicKeyBase64(user.getPublicKeyBase64());
        }
        return true;
    }
}
