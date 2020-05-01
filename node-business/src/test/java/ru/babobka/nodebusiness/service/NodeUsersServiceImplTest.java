package ru.babobka.nodebusiness.service;

import org.junit.Before;
import org.junit.Test;
import ru.babobka.nodebusiness.dao.user.NodeUsersDAO;
import ru.babobka.nodebusiness.dto.UserDTO;
import ru.babobka.nodebusiness.mapper.UserDTOToEntityMapper;
import ru.babobka.nodebusiness.model.User;
import ru.babobka.nodesecurity.keypair.KeyDecoder;
import ru.babobka.nodeutils.container.Container;
import ru.babobka.nodeutils.util.TextUtil;

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
    private UserDTOToEntityMapper userDTOToEntityMapper;
    private NodeUsersService nodeUsersService;

    @Before
    public void setUp() {
        nodeUsersDAO = mock(NodeUsersDAO.class);
        userDTOToEntityMapper = mock(UserDTOToEntityMapper.class);
        Container.getInstance().put(container -> {
            container.put(nodeUsersDAO);
            container.put(userDTOToEntityMapper);
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
    public void testGet() {
        User user = createUser();
        UUID uuid = UUID.randomUUID();
        user.setId(uuid.toString());
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
        when(userDTOToEntityMapper.map(userDTO)).thenReturn(new User());
        nodeUsersService.add(userDTO);
        verify(nodeUsersDAO).add(any(User.class));
    }

    private User createUser() {
        User user = new User();
        user.setEmail("abc@xyz.ru");
        user.setName("abc");
        user.setPublicKeyBase64(TextUtil.toBase64(KeyDecoder.generateKeyPair().getPublic().getEncoded()));
        user.setId(UUID.randomUUID().toString());
        return user;
    }


}
