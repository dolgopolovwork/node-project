package ru.babobka.nodetester.slave;

import lombok.NonNull;
import ru.babobka.nodebusiness.debug.DebugBase64KeyPair;
import ru.babobka.nodebusiness.debug.DebugCredentials;
import ru.babobka.nodeconfigs.slave.SlaveServerConfig;
import ru.babobka.nodeconfigs.slave.validation.SlaveServerConfigValidator;
import ru.babobka.nodesecurity.SecurityApplicationContainer;
import ru.babobka.nodesecurity.keypair.Base64KeyPair;
import ru.babobka.nodeslaveserver.key.SlaveServerKey;
import ru.babobka.nodeslaveserver.server.pipeline.SlavePipelineFactory;
import ru.babobka.nodeslaveserver.service.SlaveAuthService;
import ru.babobka.nodeslaveserver.task.TaskRunnerService;
import ru.babobka.nodetask.NodeTaskApplicationContainer;
import ru.babobka.nodetask.TaskPool;
import ru.babobka.nodeutils.NodeUtilsApplicationContainer;
import ru.babobka.nodeutils.container.AbstractApplicationContainer;
import ru.babobka.nodeutils.container.Container;
import ru.babobka.nodeutils.container.ContainerException;
import ru.babobka.nodeutils.container.Properties;
import ru.babobka.nodeutils.enums.Env;
import ru.babobka.nodeutils.key.UtilKey;
import ru.babobka.nodeutils.network.NodeConnectionFactory;
import ru.babobka.nodeutils.thread.ThreadPoolService;
import ru.babobka.nodeutils.util.TextUtil;

import java.security.PublicKey;

/**
 * Created by 123 on 05.11.2017.
 */
public class TesterSlaveServerApplicationContainer extends AbstractApplicationContainer {

    private final PublicKey serverPubKey;

    public TesterSlaveServerApplicationContainer(@NonNull PublicKey serverPubKey) {
        this.serverPubKey = serverPubKey;
    }

    @Override
    protected void containImpl(Container container) {
        try {
            Properties.put(UtilKey.SERVICE_THREADS_NUM, Runtime.getRuntime().availableProcessors());
            container.put(new NodeUtilsApplicationContainer());
            container.put(new SecurityApplicationContainer());
            container.putIfAbsent(new NodeConnectionFactory());
            SlaveServerConfig config = createTestConfig();
            new SlaveServerConfigValidator().validate(config);
            container.put(config);
            container.put(UtilKey.SERVICE_THREAD_POOL, ThreadPoolService.createDaemonPool("service"));
            container.put(new NodeTaskApplicationContainer());
            container.put(new SlavePipelineFactory());
            container.put(new TaskRunnerService());
            container.put(SlaveServerKey.SLAVE_SERVER_TASK_POOL, new TaskPool(config.getTasksFolder()));
            container.put(new SlaveAuthService());
        } catch (Exception e) {
            throw new ContainerException(e);
        }
    }

    private SlaveServerConfig createTestConfig() {
        SlaveServerConfig config = new SlaveServerConfig();
        config.setSlaveLogin(DebugCredentials.USER_NAME);
        Base64KeyPair keyPair = new Base64KeyPair();
        keyPair.setPrivKey(DebugBase64KeyPair.DEBUG_PRIV_KEY);
        keyPair.setPubKey(DebugBase64KeyPair.DEBUG_PUB_KEY);
        config.setKeyPair(keyPair);
        config.setTasksFolder(TextUtil.getEnv(Env.NODE_TASKS));
        config.setLoggerFolder(TextUtil.getEnv(Env.NODE_LOGS));
        config.setAuthTimeOutMillis(15_000);
        config.setRequestTimeoutMillis(30_000);
        config.setMasterServerHost("localhost");
        config.setMasterServerPort(19090);
        config.setMasterServerBase64PublicKey(TextUtil.toBase64(serverPubKey.getEncoded()));
        return config;
    }
}
