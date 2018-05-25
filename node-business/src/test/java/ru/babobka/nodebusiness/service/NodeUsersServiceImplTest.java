package ru.babobka.nodebusiness.service;

import org.junit.Before;
import org.junit.Test;
import ru.babobka.nodebusiness.dao.NodeUsersDAO;
import ru.babobka.nodebusiness.dto.UserDTO;
import ru.babobka.nodebusiness.mapper.UserDTOMapper;
import ru.babobka.nodebusiness.model.User;
import ru.babobka.nodesecurity.config.SrpConfig;
import ru.babobka.nodesecurity.service.SRPService;
import ru.babobka.nodeutils.container.Container;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * Created by 123 on 12.08.2017.
 */
public class NodeUsersServiceImplTest {

    private NodeUsersDAO nodeUsersDAO;
    private UserDTOMapper userDTOMapper;
    private NodeUsersService nodeUsersService;
    private SrpConfig srpConfig;
    private SRPService SRPService;

    @Before
    public void setUp() {
        nodeUsersDAO = mock(NodeUsersDAO.class);
        userDTOMapper = mock(UserDTOMapper.class);
        srpConfig = mock(SrpConfig.class);
        SRPService = mock(SRPService.class);
        Container.getInstance().put(container -> {
            container.put(nodeUsersDAO);
            container.put(userDTOMapper);
            container.put(srpConfig);
            container.put(SRPService);
        });

        nodeUsersService = new NodeUsersServiceImpl();
    }

    @Test
    public void testGetList() {
        User user = createUser();
        List<User> users = Arrays.asList(user, user, user);
        when(nodeUsersDAO.getList()).thenReturn(users);
        assertEquals(users.size(), nodeUsersService.getList().size());
    }

    @Test
    public void testGet() {
        User user = createUser();
        UUID uuid = UUID.randomUUID();
        user.setId(uuid);
        when(nodeUsersDAO.get(uuid)).thenReturn(user);
        assertEquals(user, nodeUsersService.get(uuid));
    }

    @Test
    public void testGetUnexisting() {
        assertNull(nodeUsersService.get(UUID.randomUUID()));
    }

    @Test
    public void testRemove() {
        UUID uuid = UUID.randomUUID();
        when(nodeUsersDAO.remove(uuid)).thenReturn(true);
        assertTrue(nodeUsersService.remove(uuid));
    }

    @Test
    public void testRemoveUnexisting() {
        UUID uuid = UUID.randomUUID();
        when(nodeUsersDAO.remove(uuid)).thenReturn(false);
        assertFalse(nodeUsersService.remove(uuid));
    }


    @Test
    public void testAdd() {
        UserDTO userDTO = new UserDTO();
        when(userDTOMapper.map(userDTO)).thenReturn(new User());
        nodeUsersService.add(userDTO);
        verify(nodeUsersDAO).add(any(User.class));
    }

    private User createUser() {
        User user = new User();
        user.setEmail("abc@xyz.ru");
        user.setName("abc");
        user.setSalt(new byte[]{1, 2, 3});
        user.setSecret(new byte[]{4, 5, 6});
        user.setId(UUID.randomUUID());
        return user;
    }


}
