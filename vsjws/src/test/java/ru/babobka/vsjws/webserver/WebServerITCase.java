package ru.babobka.vsjws.webserver;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import ru.babobka.nodeutils.container.Container;
import ru.babobka.vsjws.mapper.JSONWebControllerMapper;
import ru.babobka.vsjws.validator.config.WebServerConfigValidator;
import ru.babobka.vsjws.validator.request.RequestValidator;
import ru.babobka.vsjws.webcontroller.it.CookieTimeController;
import ru.babobka.vsjws.webcontroller.it.InternalErrorController;
import ru.babobka.vsjws.webcontroller.it.JsonTestController;
import ru.babobka.vsjws.webcontroller.it.XmlTestController;

import java.io.IOException;

import static org.junit.Assert.assertEquals;

public class WebServerITCase {

    private static final int PORT = 2512;

    private static final int SESSION_TIMEOUT_SECS = 15 * 60;

    private static final String SERVER_NAME = "Sample server";

    private static final String LOG_FOLDER = "server_log";

    private static final HttpClient HTTP_CLIENT = HttpClientBuilder.create().build();
    private static WebServer webServer;

    @BeforeClass
    public static void setUp() throws IOException {
        Container.getInstance().put(new WebServerConfigValidator());
        Container.getInstance().put(new JSONWebControllerMapper());
        Container.getInstance().put(new RequestValidator());
        WebServerConfig config = new WebServerConfig();
        config.setServerName(SERVER_NAME);
        config.setPort(PORT);
        config.setSessionTimeoutSeconds(SESSION_TIMEOUT_SECS);
        config.setLogFolder(LOG_FOLDER);
        webServer = new WebServer(config);
        // Adding controllers for a specified URLs
        webServer.addController("json", new JsonTestController());
        webServer.addController("xml", new XmlTestController());
        webServer.addController("error", new InternalErrorController());
        webServer.addController("time", new CookieTimeController());
        webServer.start();
    }

    @AfterClass
    public static void tearDown() {
        webServer.interrupt();
    }


    @Test
    public void testJsonController() throws IOException {
        BodyAndStatus bodyAndStatus = get("http://localhost:" + PORT + "/json");
        assertEquals(bodyAndStatus.getStatus(), 200);
        assertEquals(new JSONObject(bodyAndStatus.getBody()).toString(),
                new JSONObject(JsonTestController.JSON_RESPONSE).toString());
    }

    @Test
    public void testXMLController() throws IOException {
        BodyAndStatus bodyAndStatus = get("http://localhost:" + PORT + "/xml");
        assertEquals(bodyAndStatus.getStatus(), 200);
        assertEquals(bodyAndStatus.getBody(), XmlTestController.XML_RESPONSE);
    }

    @Test
    public void testErrorController() throws IOException {
        BodyAndStatus bodyAndStatus = get("http://localhost:" + PORT + "/error");
        assertEquals(bodyAndStatus.getStatus(), 500);
    }

    @Test
    public void testCookieTimeController() throws IOException, InterruptedException {
        BodyAndStatus bodyAndStatus = get("http://localhost:" + PORT + "/time");
        String timeFirst = bodyAndStatus.getBody();
        Thread.sleep(1000);
        bodyAndStatus = get("http://localhost:" + PORT + "/time");
        String timeSecond = bodyAndStatus.getBody();
        assertEquals(timeFirst, timeSecond);

    }


    private BodyAndStatus get(String url) throws IOException {
        HttpGet get = null;
        try {
            get = new HttpGet(url);
            HttpResponse response = HTTP_CLIENT.execute(get);
            HttpEntity entity = response.getEntity();
            String responseString = EntityUtils.toString(entity, "UTF-8");
            return new BodyAndStatus(responseString, response.getStatusLine().getStatusCode());
        } finally {
            if (get != null)
                get.releaseConnection();
        }
    }

    private class BodyAndStatus {
        private final String body;

        private final int status;

        public BodyAndStatus(String body, int status) {
            super();
            this.body = body;
            this.status = status;
        }

        public String getBody() {
            return body;
        }

        public int getStatus() {
            return status;
        }

    }


}
