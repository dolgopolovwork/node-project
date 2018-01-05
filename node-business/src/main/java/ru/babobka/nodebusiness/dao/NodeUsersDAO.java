package ru.babobka.nodebusiness.dao;

import ru.babobka.nodebusiness.model.User;

import java.util.List;
import java.util.UUID;


/**
 * Created by dolgopolov.a on 05.12.15.
 */
public interface NodeUsersDAO {

    User get(UUID id);

    User get(String login);

    List<User> getList();

    void add(User user);

    boolean exists(String login);

    boolean remove(UUID id);

    boolean update(UUID id, User user);

}
