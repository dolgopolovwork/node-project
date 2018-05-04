package ru.babobka.nodebusiness.service;


import ru.babobka.nodebusiness.dao.NodeUsersDAO;
import ru.babobka.nodebusiness.dto.UserDTO;
import ru.babobka.nodebusiness.mapper.UserDTOMapper;
import ru.babobka.nodebusiness.model.User;
import ru.babobka.nodesecurity.config.SrpConfig;
import ru.babobka.nodesecurity.service.SecurityService;
import ru.babobka.nodeutils.container.Container;
import ru.babobka.nodeutils.util.HashUtil;

import java.util.List;
import java.util.UUID;

public class NodeUsersServiceImpl implements NodeUsersService {

    private final UserDTOMapper userDTOMapper = Container.getInstance().get(UserDTOMapper.class);
    private final SecurityService securityService = Container.getInstance().get(SecurityService.class);
    private final SrpConfig srpConfig = Container.getInstance().get(SrpConfig.class);
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
        user.setSalt(new byte[]{1, 2, 3});
        byte[] debugSecret = securityService.secretBuilder(HashUtil.sha2("test_password"), user.getSalt(), srpConfig);
        user.setSecret(debugSecret);
        userDAO.add(user);
    }
}
