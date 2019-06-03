package ru.babobka.nodebusiness.service;

import org.junit.Before;
import org.junit.Test;
import ru.babobka.nodebusiness.dao.NodeUsersDAO;
import ru.babobka.nodebusiness.dto.UserDTO;
import ru.babobka.nodebusiness.mapper.UserDTOMapper;
import ru.babobka.nodebusiness.model.User;
import ru.babobka.nodesecurity.keypair.KeyDecoder;
import ru.babobka.nodeutils.container.Container;

import java.security.spec.InvalidKeySpecException;
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

    @Before
    public void setUp() {
        nodeUsersDAO = mock(NodeUsersDAO.class);
        userDTOMapper = mock(UserDTOMapper.class);
        Container.getInstance().put(container -> {
            container.put(nodeUsersDAO);
            container.put(userDTOMapper);
        });

        nodeUsersService = new NodeUsersServiceImpl();
    }

    @Test
    public void testGetList() throws InvalidKeySpecException {
        User user = createUser();
        List<User> users = Arrays.asList(user, user, user);
        when(nodeUsersDAO.getList()).thenReturn(users);
        assertEquals(users.size(), nodeUsersService.getList().size());
    }

    @Test
    public void testGet() throws InvalidKeySpecException {
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

    private User createUser() throws InvalidKeySpecException {
        User user = new User();
        user.setEmail("abc@xyz.ru");
        user.setName("abc");
        user.setPublicKey(KeyDecoder.decodePublicKey(DebugBase64KeyPair.DEBUG_PUB_KEY));
        user.setId(UUID.randomUUID());
        return user;
    }


}
