package ru.babobka.nodeift;

import org.junit.*;
import ru.babobka.nodeconfigs.master.MasterServerConfig;
import ru.babobka.nodemasterserver.key.MasterServerKey;
import ru.babobka.nodemasterserver.server.MasterServer;
import ru.babobka.nodesecurity.keypair.KeyDecoder;
import ru.babobka.nodeslaveserver.exception.SlaveStartupException;
import ru.babobka.nodeslaveserver.key.SlaveServerKey;
import ru.babobka.nodetask.TaskPool;
import ru.babobka.nodetester.master.MasterServerRunner;
import ru.babobka.nodetester.slave.SlaveServerRunner;
import ru.babobka.nodeutils.container.Container;
import ru.babobka.nodeutils.enums.Env;
import ru.babobka.nodeutils.log.LoggerInit;
import ru.babobka.nodeutils.util.TextUtil;

import java.io.IOException;
import java.security.PublicKey;
import java.util.HashSet;
import java.util.Set;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by 123 on 07.11.2017.
 */
public class AuthCommonTasksITCase {
    protected static MasterServer masterServer;
    private TaskPool taskPool;

    @BeforeClass
    public static void setUp() {

        LoggerInit.initPersistentConsoleDebugLogger(TextUtil.getEnv(Env.NODE_LOGS), AuthCommonTasksITCase.class.getSimpleName());
        MasterServerRunner.init();
        MasterServerConfig masterServerConfig = Container.getInstance().get(MasterServerConfig.class);
        PublicKey serverPublicKey = KeyDecoder.decodePublicKeyUnsafe(masterServerConfig.getKeyPair().getPubKey());
        SlaveServerRunner.init(serverPublicKey);
        masterServer = MasterServerRunner.runMasterServer();
    }

    @AfterClass
    public static void tearDown() throws InterruptedException {
        masterServer.interrupt();
        masterServer.join();
        Container.getInstance().clear();
    }

    @Before
    public void setUpMocks() {
        taskPool = mock(TaskPool.class);
        Container.getInstance().put(SlaveServerKey.SLAVE_SERVER_TASK_POOL, taskPool);
    }

    @Test(expected = SlaveStartupException.class)
    public void testNoTasks() throws IOException {
        when(taskPool.getTaskNames()).thenReturn(new HashSet<>());
        SlaveServerRunner.runSlaveServer(TestCredentials.USER_NAME, TestCredentials.PRIV_KEY);
    }

    @Test(expected = SlaveStartupException.class)
    public void testNoCommonTasks() throws IOException {
        Set<String> availableTasks = new HashSet<>();
        availableTasks.add("abc");
        availableTasks.add("xyz");
        when(taskPool.getTaskNames()).thenReturn(availableTasks);
        SlaveServerRunner.runSlaveServer(TestCredentials.USER_NAME, TestCredentials.PRIV_KEY);
    }

    @Test
    public void testOneCommonTask() throws IOException {
        Set<String> availableTasks = new HashSet<>();
        TaskPool masterSlaveTaskPool = Container.getInstance().get(MasterServerKey.MASTER_SERVER_TASK_POOL);
        availableTasks.add(masterSlaveTaskPool.getTaskNames().iterator().next());
        when(taskPool.getTaskNames()).thenReturn(availableTasks);
        SlaveServerRunner.runSlaveServer(TestCredentials.USER_NAME, TestCredentials.PRIV_KEY);
    }

}
