package ru.babobka.nodebusiness.service;


import ru.babobka.nodebusiness.dao.user.NodeUsersDAO;
import ru.babobka.nodebusiness.debug.DebugBase64KeyPair;
import ru.babobka.nodebusiness.debug.DebugCredentials;
import ru.babobka.nodebusiness.dto.UserDTO;
import ru.babobka.nodebusiness.mapper.UserDTOToEntityMapper;
import ru.babobka.nodebusiness.model.User;
import ru.babobka.nodeutils.container.Container;

import java.util.List;
import java.util.UUID;

public class NodeUsersServiceImpl implements NodeUsersService {
    private final UserDTOToEntityMapper userDTOToEntityMapper = Container.getInstance().get(UserDTOToEntityMapper.class);
    private final NodeUsersDAO userDAO = Container.getInstance().get(NodeUsersDAO.class);

    @Override
    public List<User> getList() {
        return userDAO.getList();
    }

    @Override
    public User get(UUID id) {
        return userDAO.get(id);
    }

    @Override
    public User get(String login) {
        return userDAO.get(login);
    }

    @Override
    public boolean remove(UUID id) {
        return userDAO.remove(id);
    }

    @Override
    public boolean add(UserDTO user) {
        User userToCreate = userDTOToEntityMapper.map(user);
        userToCreate.setId(UUID.randomUUID().toString());
        return userDAO.add(userToCreate);
    }

    @Override
    public boolean update(UserDTO user) {
        return userDAO.update(userDTOToEntityMapper.map(user));
    }

    @Override
    public boolean createDebugUser() {
        User user = new User();
        user.setName(DebugCredentials.USER_NAME);
        user.setEmail("test@email.com");
        user.setId(UUID.randomUUID().toString());
        user.setPublicKeyBase64(DebugBase64KeyPair.DEBUG_PUB_KEY);
        return userDAO.add(user);
    }

}
