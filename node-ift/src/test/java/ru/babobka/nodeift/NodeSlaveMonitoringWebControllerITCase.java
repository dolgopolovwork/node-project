package ru.babobka.nodeift;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.apache.http.client.fluent.Request;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import ru.babobka.nodebusiness.debug.DebugCredentials;
import ru.babobka.nodeconfigs.master.MasterServerConfig;
import ru.babobka.nodeconfigs.slave.SlaveServerConfig;
import ru.babobka.nodemasterserver.server.MasterServer;
import ru.babobka.nodemasterserver.slave.SlavesStorage;
import ru.babobka.nodesecurity.keypair.KeyDecoder;
import ru.babobka.nodetask.TaskPoolReader;
import ru.babobka.nodetester.master.MasterServerRunner;
import ru.babobka.nodetester.slave.SlaveServerRunner;
import ru.babobka.nodetester.slave.cluster.SlaveServerCluster;
import ru.babobka.nodeutils.container.Container;
import ru.babobka.nodeutils.key.MasterServerKey;
import ru.babobka.nodeutils.key.SlaveServerKey;
import ru.babobka.nodeutils.log.LoggerInit;
import ru.babobka.nodeutils.util.TextUtil;
import ru.babobka.slavenoderun.SlaveServerApplicationContainer;

import java.io.IOException;
import java.security.PublicKey;
import java.util.Set;

import static org.junit.Assert.*;

/**
 * Created by 123 on 22.07.2018.
 */
public class NodeSlaveMonitoringWebControllerITCase {
    private static MasterServer masterServer;
    private static MasterServerConfig masterServerConfig;
    private static final Gson gson = new Gson();
    private static TaskPoolReader taskPoolReader;
    private static SlavesStorage slavesStorage;
    private static SlaveServerConfig slaveServerConfig;
    private static SlaveServerCluster slaveServerCluster;

    @BeforeClass
    public static void setUp() throws IOException {
        LoggerInit.initPersistentConsoleDebugLogger(TextUtil.getLogFolder(), NodeSlaveMonitoringWebControllerITCase.class.getSimpleName());
        MasterServerRunner.init();
        masterServerConfig = Container.getInstance().get(MasterServerConfig.class);
        PublicKey serverPublicKey = KeyDecoder.decodePublicKeyUnsafe(masterServerConfig.getKeyPair().getPubKey());
        SlaveServerRunner.init(serverPublicKey);
        masterServer = MasterServerRunner.runMasterServer();
        slaveServerConfig = Container.getInstance().get(SlaveServerConfig.class);
        taskPoolReader = Container.getInstance().get(MasterServerKey.MASTER_SERVER_TASK_POOL);
        slavesStorage = Container.getInstance().get(SlavesStorage.class);
        Container.getInstance().put(SlaveServerKey.SLAVE_WEB, SlaveServerApplicationContainer.createWebServer());
        slaveServerCluster = new SlaveServerCluster(DebugCredentials.USER_NAME, 1);
        slaveServerCluster.start();
    }

    @AfterClass
    public static void tearDown() throws InterruptedException, IOException {
        masterServer.interrupt();
        masterServer.join();
        slaveServerCluster.close();
        Container.getInstance().clear();
    }

    @Test
    public void testHealthCheck() throws IOException {
        int status = Request.Get("http://127.0.0.1:" + slaveServerConfig.getWebPort() + "/monitoring/healthcheck")
                .execute().returnResponse().getStatusLine().getStatusCode();
        assertEquals(200, status);
    }

    @Test
    public void testGetTaskNames() throws IOException {
        Set<String> actualTasks = taskPoolReader.getTaskNames();
        assertFalse(actualTasks.isEmpty());
        String taskNamesJson = Request.Get("http://127.0.0.1:" + slaveServerConfig.getWebPort() + "/monitoring/taskNames")
                .execute().returnContent().toString();
        Set<String> tasksFromResponse = gson.fromJson(taskNamesJson, new TypeToken<Set<String>>() {
        }.getType());
        assertEquals(actualTasks, tasksFromResponse);
    }
}
