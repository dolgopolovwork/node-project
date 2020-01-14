package ru.babobka.nodeift;

import com.google.gson.Gson;
import org.apache.http.HttpStatus;
import org.apache.http.client.fluent.Request;
import org.apache.http.entity.ContentType;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.testcontainers.containers.GenericContainer;
import ru.babobka.nodebusiness.debug.DebugBase64KeyPair;
import ru.babobka.nodebusiness.dto.UserDTO;
import ru.babobka.nodebusiness.model.User;
import ru.babobka.nodebusiness.service.NodeUsersService;
import ru.babobka.nodebusiness.service.NodeUsersServiceImpl;
import ru.babobka.nodeconfigs.master.MasterServerConfig;
import ru.babobka.nodeift.container.AbstractContainerITCase;
import ru.babobka.nodemasterserver.server.MasterServer;
import ru.babobka.nodetester.master.MasterServerRunner;
import ru.babobka.nodeutils.container.Container;
import ru.babobka.nodeutils.log.LoggerInit;
import ru.babobka.nodeutils.util.TextUtil;

import java.io.IOException;
import java.util.UUID;

import static org.junit.Assert.*;

/**
 * Created by 123 on 01.07.2018.
 */
public class NodeUserRestITCase extends AbstractContainerITCase {

    private static final GenericContainer postgresContainer = createPostgres();
    private static MasterServer masterServer;
    private static NodeUsersService nodeUsersService;
    private static int userCounter;
    private static MasterServerConfig config;
    private static final Gson GSON = new Gson();

    @BeforeClass
    public static void setUp() throws IOException {
        postgresContainer.start();
        LoggerInit.initPersistentConsoleDebugLogger(TextUtil.getLogFolder(), NodeUserRestITCase.class.getSimpleName());
        MasterServerRunner.initWithRealDb();
        masterServer = MasterServerRunner.runMasterServer();
        nodeUsersService = Container.getInstance().get(NodeUsersServiceImpl.class);
        config = Container.getInstance().get(MasterServerConfig.class);
    }

    @Before
    public void before() throws InterruptedException {
        Thread.sleep(1000);
        clearAllUsers();
    }

    @AfterClass
    public static void tearDown() throws InterruptedException {
        postgresContainer.stop();
        Thread.sleep(15_000);
        masterServer.interrupt();
        masterServer.join();
        Container.getInstance().clear();
    }

    @Test
    public void testGetList() throws IOException {
        int users = 10;
        addRandomUsers(users);
        JSONArray result = new JSONArray(Request.Get("http://127.0.0.1:" + config.getPorts().getWebListenerPort() + "/users")
                .execute().returnContent().toString());
        assertEquals(result.length(), users);
    }

    @Test
    public void testGet() throws IOException {
        int users = 10;
        addRandomUsers(users);
        User user = nodeUsersService.getList().get(0);
        JSONObject result = new JSONObject(Request.Get("http://127.0.0.1:" + config.getPorts().getWebListenerPort() + "/users?id=" + user.getId())
                .execute().returnContent().toString());
        assertEquals(result.get("name"), user.getName());
        assertEquals(result.get("id"), user.getId());
    }

    @Test
    public void testGetEmpty() throws IOException {
        JSONArray result = new JSONArray(Request.Get("http://127.0.0.1:" + config.getPorts().getWebListenerPort() + "/users")
                .execute().returnContent().toString());
        assertEquals(result.length(), 0);
    }

    @Test
    public void testDeleteBadRequest() throws IOException {
        int users = 10;
        addRandomUsers(users);
        assertEquals(Request.Delete("http://127.0.0.1:" + config.getPorts().getWebListenerPort() + "/users")
                .execute().returnResponse().getStatusLine().getStatusCode(), HttpStatus.SC_BAD_REQUEST);
        assertEquals(nodeUsersService.getList().size(), users);
    }

    @Test
    public void testDelete() throws IOException {
        int users = 10;
        addRandomUsers(users);
        User user = nodeUsersService.getList().get(0);
        assertEquals(Request.Delete("http://127.0.0.1:" + config.getPorts().getWebListenerPort() + "/users?id=" + user.getId())
                .execute().returnResponse().getStatusLine().getStatusCode(), HttpStatus.SC_OK);
        assertEquals(nodeUsersService.getList().size(), users - 1);
        assertNull(nodeUsersService.get(user.getId()));
    }

    @Test
    public void testDeleteNotFound() throws IOException {
        int users = 10;
        addRandomUsers(users);
        assertEquals(Request.Delete("http://127.0.0.1:" + config.getPorts().getWebListenerPort() + "/users?id=" + UUID.randomUUID())
                .execute().returnResponse().getStatusLine().getStatusCode(), HttpStatus.SC_NOT_FOUND);
        assertEquals(nodeUsersService.getList().size(), users);
    }

    @Test
    public void testPutInvalidUser() throws IOException {
        UserDTO userDTO = new UserDTO();
        assertEquals(Request.Put("http://127.0.0.1:" + config.getPorts().getWebListenerPort() + "/users")
                .bodyString(GSON.toJson(userDTO), ContentType.APPLICATION_JSON).execute().returnResponse().getStatusLine().getStatusCode(), HttpStatus.SC_BAD_REQUEST);
        assertEquals(nodeUsersService.getList().size(), 0);
    }

    @Test
    public void testPut() throws IOException {
        UserDTO userDTO = new UserDTO();
        userDTO.setEmail("abc@xyz.ru");
        userDTO.setName("abc");
        userDTO.setBase64PubKey(DebugBase64KeyPair.DEBUG_PUB_KEY);
        assertEquals(Request.Put("http://127.0.0.1:" + config.getPorts().getWebListenerPort() + "/users")
                .bodyString(GSON.toJson(userDTO), ContentType.APPLICATION_JSON).execute().returnResponse().getStatusLine().getStatusCode(), HttpStatus.SC_OK);
        assertEquals(nodeUsersService.getList().size(), 1);
        assertNotNull(nodeUsersService.get(userDTO.getName()));
    }

    @Test
    public void testPostNoId() throws IOException {
        assertEquals(Request.Post("http://127.0.0.1:" + config.getPorts().getWebListenerPort() + "/users")
                .execute().returnResponse().getStatusLine().getStatusCode(), HttpStatus.SC_BAD_REQUEST);
    }

    @Test
    public void testPostUserNotFound() throws IOException {
        addRandomUsers(1);
        User user = nodeUsersService.getList().get(0);
        UserDTO userDTO = new UserDTO();
        userDTO.setId(user.getId() + "abcxyz");
        userDTO.setName("abcxyz");
        assertEquals(Request.Post("http://127.0.0.1:" + config.getPorts().getWebListenerPort() + "/users")
                .bodyString(GSON.toJson(userDTO), ContentType.APPLICATION_JSON).execute().returnResponse().getStatusLine().getStatusCode(), HttpStatus.SC_NOT_FOUND);
    }

    @Test
    public void testPostUserInvalid() throws IOException {
        addRandomUsers(1);
        User user = nodeUsersService.getList().get(0);
        UserDTO userDTO = new UserDTO();
        userDTO.setId(user.getId());
        userDTO.setEmail("invalid.email");
        assertEquals(Request.Post("http://127.0.0.1:" + config.getPorts().getWebListenerPort() + "/users")
                .bodyString(GSON.toJson(userDTO), ContentType.APPLICATION_JSON).execute().returnResponse().getStatusLine().getStatusCode(), HttpStatus.SC_BAD_REQUEST);
    }

    @Test
    public void testPost() throws IOException {
        addRandomUsers(1);
        User user = nodeUsersService.getList().get(0);
        UserDTO userDTO = new UserDTO();
        userDTO.setEmail("xyz@abc.com");
        userDTO.setId(user.getId());
        assertEquals(Request.Post("http://127.0.0.1:" + config.getPorts().getWebListenerPort() + "/users")
                .bodyString(GSON.toJson(userDTO), ContentType.APPLICATION_JSON).execute().returnResponse().getStatusLine().getStatusCode(), HttpStatus.SC_OK);
        user = nodeUsersService.getList().get(0);
        assertEquals(user.getEmail(), userDTO.getEmail());
    }

    private static void addRandomUsers(int users) {
        for (int i = 0; i < users; i++) {
            nodeUsersService.add(createRandomUser());
        }
    }

    private static UserDTO createRandomUser() {
        UserDTO user = new UserDTO();
        user.setName("test user " + userCounter++);
        user.setBase64PubKey(DebugBase64KeyPair.DEBUG_PUB_KEY);
        return user;
    }

    private static void clearAllUsers() {
        for (User user : nodeUsersService.getList()) {
            nodeUsersService.remove(UUID.fromString(user.getId()));
        }
    }
}
