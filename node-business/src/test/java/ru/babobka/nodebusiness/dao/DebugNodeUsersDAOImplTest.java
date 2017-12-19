package ru.babobka.nodebusiness.dao;

import org.junit.Before;
import org.junit.Test;
import ru.babobka.nodebusiness.model.User;
import uk.co.jemos.podam.api.PodamFactory;
import uk.co.jemos.podam.api.PodamFactoryImpl;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.junit.Assert.*;

/**
 * Created by 123 on 10.08.2017.
 */
public class DebugNodeUsersDAOImplTest {

    private final UUID testUUID = UUID.randomUUID();
    private final String testLogin = "login";
    private final PodamFactory podamFactory = new PodamFactoryImpl();
    private NodeUsersDAO nodeUsersDAO;

    @Before
    public void setUp() {
        Map<UUID, User> userMap = new HashMap<>();
        User user = podamFactory.manufacturePojo(User.class);
        user.setName(testLogin);
        user.setId(testUUID);
        userMap.put(testUUID, user);
        nodeUsersDAO = new DebugNodeUsersDAOImpl(userMap);
    }

    @Test
    public void testGetByUUID() {
        assertNotNull(nodeUsersDAO.get(testUUID));
    }

    @Test
    public void testGetByLogin() {
        assertNotNull(nodeUsersDAO.get(testLogin));
    }

    @Test
    public void testGetList() {
        assertFalse(nodeUsersDAO.getList().isEmpty());
    }

    @Test
    public void testAdd() {
        int oldSize = nodeUsersDAO.getList().size();
        User user = podamFactory.manufacturePojo(User.class);
        nodeUsersDAO.add(user);
        assertEquals(nodeUsersDAO.getList().size(), oldSize + 1);
    }

    @Test
    public void testExists() {
        assertTrue(nodeUsersDAO.exists(testLogin));
    }

    @Test
    public void testDoesntExist() {
        assertFalse(nodeUsersDAO.exists(testLogin + "abc"));
    }

    @Test
    public void testRemove() {
        int oldSize = nodeUsersDAO.getList().size();
        nodeUsersDAO.remove(testUUID);
        assertEquals(nodeUsersDAO.getList().size(), oldSize - 1);
    }

    @Test
    public void testRemoveUnexisting() {
        int oldSize = nodeUsersDAO.getList().size();
        nodeUsersDAO.remove(UUID.randomUUID());
        assertEquals(nodeUsersDAO.getList().size(), oldSize);
    }

    @Test
    public void testIncrTaskCount() {
        int oldTaskCount = nodeUsersDAO.get(testUUID).getTaskCount();
        assertTrue(nodeUsersDAO.incrTaskCount(testUUID));
        assertEquals((int) nodeUsersDAO.get(testUUID).getTaskCount(), oldTaskCount + 1);
    }

    @Test
    public void testIncrTaskCountUnexsitingUser() {
        assertFalse(nodeUsersDAO.incrTaskCount(UUID.randomUUID()));
    }

    @Test
    public void testUpdateEmail() {
        User user = new User();
        user.setEmail("babobka@bk.ru");
        assertTrue(nodeUsersDAO.update(testUUID, user));
        User foundUser = nodeUsersDAO.get(testUUID);
        assertEquals(user.getEmail(), foundUser.getEmail());
        assertNotNull(foundUser.getTaskCount());
        assertNotNull(foundUser.getName());
        assertNotNull(foundUser.getHashedPassword());
        assertNotNull(foundUser.getId());
    }
}
