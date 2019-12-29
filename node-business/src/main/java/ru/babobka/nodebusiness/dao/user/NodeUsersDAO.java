package ru.babobka.nodebusiness.dao.user;

import ru.babobka.nodebusiness.model.User;

import java.util.List;
import java.util.UUID;


/**
 * Created by dolgopolov.a on 05.12.15.
 */
//TODO think about error handling
public interface NodeUsersDAO {

    User get(UUID id);

    User get(String login);

    List<User> getList();

    boolean add(User user);

    boolean exists(String login);

    boolean remove(UUID id);

    boolean update(User user);

}
