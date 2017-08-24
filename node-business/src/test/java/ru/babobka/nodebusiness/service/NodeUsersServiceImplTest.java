package ru.babobka.nodebusiness.service;

import org.junit.BeforeClass;
import org.junit.Test;
import ru.babobka.nodebusiness.dao.NodeUsersDAO;
import ru.babobka.nodebusiness.dto.UserDTO;
import ru.babobka.nodebusiness.mapper.UserDTOMapper;
import ru.babobka.nodebusiness.model.User;
import ru.babobka.nodeutils.container.ApplicationContainer;
import ru.babobka.nodeutils.container.Container;
import uk.co.jemos.podam.api.PodamFactory;
import uk.co.jemos.podam.api.PodamFactoryImpl;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * Created by 123 on 12.08.2017.
 */
public class NodeUsersServiceImplTest {

    private static NodeUsersDAO nodeUsersDAO = mock(NodeUsersDAO.class);

    private static UserDTOMapper userDTOMapper = mock(UserDTOMapper.class);

    private static NodeUsersService nodeUsersService;

    private PodamFactory podamFactory = new PodamFactoryImpl();

    @BeforeClass
    public static void setUp() {
        new ApplicationContainer() {
            @Override
            public void contain(Container container) {
                container.put(nodeUsersDAO);
                container.put(userDTOMapper);
            }
        }.contain(Container.getInstance());
        nodeUsersService = new NodeUsersServiceImpl();
    }

    @Test
    public void testGetList() {
        User user = podamFactory.manufacturePojo(User.class);
        List<User> users = Arrays.asList(user, user, user);
        when(nodeUsersDAO.getList()).thenReturn(users);
        assertEquals(users.size(), nodeUsersService.getList().size());
    }

    @Test
    public void testGet() {
        User user = podamFactory.manufacturePojo(User.class);
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

    @Test
    public void testAuth() {
        byte[] hashedPassword = {1, 2, 3};
        String login = "test user";
        User user = new User();
        user.setHashedPassword(hashedPassword);
        when(nodeUsersDAO.get(login)).thenReturn(user);
        assertTrue(nodeUsersService.auth(login, hashedPassword));
    }

    @Test
    public void testAuthNotFoundUser() {
        byte[] hashedPassword = {1, 2, 3};
        String login = "test user";
        when(nodeUsersDAO.get(login)).thenReturn(null);
        assertFalse(nodeUsersService.auth(login, hashedPassword));
    }

    @Test
    public void testAuthBadPassword() {
        byte[] hashedPassword = {1, 2, 3};
        String login = "test user";
        User user = new User();
        user.setHashedPassword(new byte[]{4, 5, 6});
        when(nodeUsersDAO.get(login)).thenReturn(null);
        assertFalse(nodeUsersService.auth(login, hashedPassword));
    }

}
