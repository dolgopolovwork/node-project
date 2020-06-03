package ru.babobka.nodeift;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.apache.http.client.fluent.Request;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import ru.babobka.nodebusiness.debug.DebugCredentials;
import ru.babobka.nodeconfigs.master.MasterServerConfig;
import ru.babobka.nodemasterserver.server.MasterServer;
import ru.babobka.nodemasterserver.slave.Slave;
import ru.babobka.nodemasterserver.slave.SlavesStorage;
import ru.babobka.nodesecurity.keypair.KeyDecoder;
import ru.babobka.nodetask.TaskPoolReader;
import ru.babobka.nodetester.master.MasterServerRunner;
import ru.babobka.nodetester.slave.SlaveServerRunner;
import ru.babobka.nodetester.slave.cluster.SlaveServerCluster;
import ru.babobka.nodeutils.container.Container;
import ru.babobka.nodeutils.key.MasterServerKey;
import ru.babobka.nodeutils.log.LoggerInit;
import ru.babobka.nodeutils.util.TextUtil;
import ru.babobka.nodeweb.dto.ConnectedSlaveDTO;

import java.io.IOException;
import java.security.PublicKey;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

/**
 * Created by 123 on 22.07.2018.
 */
public class NodeMasterMonitoringWebControllerITCase {
    private static MasterServer masterServer;
    private static MasterServerConfig config;
    private static final Gson gson = new Gson();
    private static TaskPoolReader taskPoolReader;
    private static SlavesStorage slavesStorage;

    @BeforeClass
    public static void setUp() throws IOException {
        LoggerInit.initPersistentConsoleDebugLogger(TextUtil.getLogFolder(), NodeMasterMonitoringWebControllerITCase.class.getSimpleName());
        MasterServerRunner.init();
        MasterServerConfig masterServerConfig = Container.getInstance().get(MasterServerConfig.class);
        PublicKey serverPublicKey = KeyDecoder.decodePublicKeyUnsafe(masterServerConfig.getKeyPair().getPubKey());
        SlaveServerRunner.init(serverPublicKey);
        masterServer = MasterServerRunner.runMasterServer();
        config = Container.getInstance().get(MasterServerConfig.class);
        taskPoolReader = Container.getInstance().get(MasterServerKey.MASTER_SERVER_TASK_POOL);
        slavesStorage = Container.getInstance().get(SlavesStorage.class);
    }

    @AfterClass
    public static void tearDown() throws InterruptedException {
        masterServer.interrupt();
        masterServer.join();
        Container.getInstance().clear();
    }

    @Test
    public void testGetClusterSize() throws IOException {
        int slaves = 5;
        try (SlaveServerCluster slaveServerCluster = new SlaveServerCluster(DebugCredentials.USER_NAME, slaves)) {
            slaveServerCluster.start();
            String content = Request.Get("http://127.0.0.1:" + config.getPorts().getWebListenerPort() + "/monitoring/clustersize")
                    .execute().returnContent().toString();
            assertEquals(slaves, Integer.valueOf(content).intValue());
        }
    }

    @Test
    public void testHealthCheck() throws IOException {
        int slaves = 5;
        try (SlaveServerCluster slaveServerCluster = new SlaveServerCluster(DebugCredentials.USER_NAME, slaves)) {
            slaveServerCluster.start();
            int status = Request.Get("http://127.0.0.1:" + config.getPorts().getWebListenerPort() + "/monitoring/healthcheck")
                    .execute().returnResponse().getStatusLine().getStatusCode();
            assertEquals(200, status);
        }
    }

    @Test
    public void testStartTime() throws IOException {
        int slaves = 5;
        try (SlaveServerCluster slaveServerCluster = new SlaveServerCluster(DebugCredentials.USER_NAME, slaves)) {
            slaveServerCluster.start();
            long startTime = Long.parseLong(Request.Get("http://127.0.0.1:" + config.getPorts().getWebListenerPort() + "/monitoring/startTime")
                    .execute().returnContent().toString());
            long currentTime = System.currentTimeMillis();
            // the event has happen before this test
            assertTrue(currentTime > startTime);
            // but it has to be somewhere near "now".
            assertTrue((currentTime - 5 * 60_000) < startTime);
        }
    }

    @Test
    public void testGetTaskNames() throws IOException {
        int slaves = 5;
        try (SlaveServerCluster slaveServerCluster = new SlaveServerCluster(DebugCredentials.USER_NAME, slaves)) {
            slaveServerCluster.start();
            Set<String> actualTasks = taskPoolReader.getTaskNames();
            assertFalse(actualTasks.isEmpty());
            String taskNamesJson = Request.Get("http://127.0.0.1:" + config.getPorts().getWebListenerPort() + "/monitoring/taskNames")
                    .execute().returnContent().toString();
            Set<String> tasksFromResponse = gson.fromJson(taskNamesJson, new TypeToken<Set<String>>() {
            }.getType());
            assertEquals(actualTasks, tasksFromResponse);
        }
    }

    @Test
    public void testGetSlaves() throws IOException {
        int slaves = 5;
        try (SlaveServerCluster slaveServerCluster = new SlaveServerCluster(DebugCredentials.USER_NAME, slaves)) {
            slaveServerCluster.start();
            String connectedSlavesJson = Request.Get("http://127.0.0.1:" + config.getPorts().getWebListenerPort() + "/monitoring/slaves")
                    .execute().returnContent().toString();
            List<ConnectedSlaveDTO> slavesResponse = gson.fromJson(connectedSlavesJson, new TypeToken<List<ConnectedSlaveDTO>>() {
            }.getType());
            Map<String, Slave> actualSlaves = slavesStorage.getFullList().stream().collect(
                    Collectors.toMap(slave -> slave.getSlaveId().toString(), slave -> slave));
            assertEquals(slaves, slavesResponse.size());
            assertEquals(actualSlaves.size(), slavesResponse.size());
            for (ConnectedSlaveDTO connectedSlave : slavesResponse) {
                Slave actualSlave = actualSlaves.get(connectedSlave.getId());
                assertEquals(connectedSlave.getUserName(), actualSlave.getUserName());
                assertEquals(connectedSlave.getId(), actualSlave.getSlaveId().toString());
                assertEquals(connectedSlave.getAddress(), actualSlave.getAddress().getHostAddress());
            }
        }
    }
}
