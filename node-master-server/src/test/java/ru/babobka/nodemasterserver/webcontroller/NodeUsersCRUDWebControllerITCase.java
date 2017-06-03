package ru.babobka.nodemasterserver.webcontroller;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.apache.http.HttpMessage;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPatch;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import ru.babobka.nodemasterserver.server.MasterServer;
import ru.babobka.nodemasterserver.server.MasterServerConfig;
import ru.babobka.nodeslaveserver.server.SlaveServer;
import ru.babobka.nodeutils.container.Container;
import ru.babobka.vsjws.enumerations.ResponseCode;
import ru.babobka.vsjws.webserver.HttpResponse;

public class NodeUsersCRUDWebControllerITCase {

    private static MasterServer masterServer;

    private static MasterServerConfig config;

    private static int restPort;

    private static String restURL;

    private static String restLogin;

    private static String restPassword;

    private static final String USER_NAME = "test_rest_user";

    private static JSONObject normalUserJson;

    private static JSONObject badEmailUserJson;

    private static final String LOGIN_HEADER = "X-Login";

    private static final String PASSWORD_HEADER = "X-Password";

    private static final HttpClient httpClient = HttpClientBuilder.create().build();

    @BeforeClass
    public static void setUp() throws IOException {
        MasterServer.initTestContainer();
        SlaveServer.initTestContainer();

        config = Container.getInstance().get(MasterServerConfig.class);

        restPort = config.getWebPort();

        restURL = "http://localhost:" + restPort + "/users";

        restLogin = config.getRestServiceLogin();

        restPassword = config.getRestServicePassword();

        normalUserJson = new JSONObject();
        normalUserJson.put("name", USER_NAME);
        normalUserJson.put("taskCount", 0);
        normalUserJson.put("password", "abc");
        normalUserJson.put("email", "babobka@bk.ru");
        badEmailUserJson = new JSONObject(normalUserJson.toString());
        badEmailUserJson.put("email", "abc");
        masterServer = new MasterServer();
        masterServer.start();
    }

    @AfterClass
    public static void closeServer() {
        masterServer.interrupt();
    }

    @After
    public void tearDown() throws IOException {
        delete(USER_NAME);
    }

    @Test
    public void testDelete() throws  IOException {
        add(normalUserJson);
        assertEquals(delete(USER_NAME), ResponseCode.OK.getCode());
        assertEquals(get(USER_NAME), ResponseCode.NOT_FOUND.getCode());
    }

    @Test
    public void testGet() throws IOException {
        assertEquals(get(USER_NAME), ResponseCode.NOT_FOUND.getCode());
        add(normalUserJson);
        assertEquals(get(USER_NAME), ResponseCode.OK.getCode());
        assertEquals(get(USER_NAME + "abc"), ResponseCode.NOT_FOUND.getCode());
    }

    @Test
    public void testBadAdd() throws IOException {
        assertEquals(add(badEmailUserJson), ResponseCode.BAD_REQUEST.getCode());
        assertEquals(get(USER_NAME), ResponseCode.NOT_FOUND.getCode());

    }

    @Test
    public void tesAdd() throws IOException {
        assertEquals(add(normalUserJson), ResponseCode.OK.getCode());
        assertEquals(get(USER_NAME), ResponseCode.OK.getCode());
    }

    @Test
    public void testDoubleAdd() throws ClientProtocolException, IOException {
        add(normalUserJson);
        assertEquals(add(normalUserJson), ResponseCode.BAD_REQUEST.getCode());
    }

    @Test
    public void testAuth() throws ClientProtocolException, JSONException, IOException {
        assertEquals(get(USER_NAME), ResponseCode.NOT_FOUND.getCode());
    }

    @Test
    public void testBadAuth() throws ClientProtocolException, JSONException, IOException {
        assertEquals(badGet(USER_NAME), ResponseCode.UNAUTHORIZED.getCode());
    }

    private int add(JSONObject userJSON) throws ClientProtocolException, IOException {
        HttpPatch patch = null;
        try {
            patch = new HttpPatch(restURL);
            setCredentialHeaders(patch);
            StringEntity entity = new StringEntity(userJSON.toString());
            patch.setEntity(entity);
            return httpClient.execute(patch).getStatusLine().getStatusCode();
        } finally {
            if (patch != null) {
                patch.releaseConnection();
            }
        }
    }

    private int delete(String userName) throws ClientProtocolException, IOException {
        HttpDelete delete = null;
        try {
            delete = new HttpDelete(restURL + "?userName=" + userName);
            setCredentialHeaders(delete);
            return httpClient.execute(delete).getStatusLine().getStatusCode();
        } finally {
            if (delete != null) {
                delete.releaseConnection();
            }
        }
    }

    private int get(String userName) throws ClientProtocolException, IOException {
        HttpGet get = null;
        try {
            get = new HttpGet(restURL + "?userName=" + userName);
            setCredentialHeaders(get);
            return httpClient.execute(get).getStatusLine().getStatusCode();
        } finally {
            if (get != null)
                get.releaseConnection();
        }
    }

    private void setCredentialHeaders(HttpMessage httpMessage) {
        httpMessage.setHeader(LOGIN_HEADER, restLogin);
        httpMessage.setHeader(PASSWORD_HEADER, restPassword);
    }

    private int badGet(String userName) throws IOException {
        HttpGet get = null;
        try {
            get = new HttpGet(restURL + "?userName=" + userName);
            setBadCredentialHeaders(get);
            return httpClient.execute(get).getStatusLine().getStatusCode();
        } finally {
            if (get != null)
                get.releaseConnection();
        }
    }

    private void setBadCredentialHeaders(HttpMessage httpMessage) {
        httpMessage.setHeader(LOGIN_HEADER, restLogin + "abc");
        httpMessage.setHeader(PASSWORD_HEADER + "abc", restPassword);
    }

}
