package ru.babobka.nodebusiness.service;


import ru.babobka.nodebusiness.dao.NodeUsersDAO;
import ru.babobka.nodebusiness.dto.UserDTO;
import ru.babobka.nodebusiness.mapper.UserDTOMapper;
import ru.babobka.nodebusiness.model.User;
import ru.babobka.nodesecurity.keypair.KeyDecoder;
import ru.babobka.nodeutils.container.Container;

import java.security.spec.InvalidKeySpecException;
import java.util.List;
import java.util.UUID;

public class NodeUsersServiceImpl implements NodeUsersService {
    private final UserDTOMapper userDTOMapper = Container.getInstance().get(UserDTOMapper.class);
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
    public void add(UserDTO user) {
        userDAO.add(userDTOMapper.map(user));
    }

    @Override
    public boolean update(UUID id, UserDTO user) {
        return userDAO.update(id, userDTOMapper.map(user));
    }

    @Override
    public void createDebugUser() {

        User user = new User();
        user.setName("test_user");
        user.setEmail("test@email.com");
        user.setId(UUID.randomUUID());
        try {
            user.setPublicKey(KeyDecoder.decodePublicKey(DebugBase64KeyPair.DEBUG_PUB_KEY));
        } catch (InvalidKeySpecException ignored) {
            // Won't happen
        }
        userDAO.add(user);
    }

}
