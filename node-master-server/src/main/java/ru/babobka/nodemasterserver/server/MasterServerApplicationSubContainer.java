package ru.babobka.nodemasterserver.server;

import lombok.NonNull;
import ru.babobka.nodeconfigs.master.MasterServerConfig;
import ru.babobka.nodemasterserver.client.ClientStorage;
import ru.babobka.nodemasterserver.client.IncomingClientListenerThread;
import ru.babobka.nodemasterserver.key.MasterServerKey;
import ru.babobka.nodemasterserver.listener.CacheRequestListener;
import ru.babobka.nodemasterserver.listener.OnRaceStyleTaskIsReady;
import ru.babobka.nodemasterserver.listener.OnTaskIsReady;
import ru.babobka.nodemasterserver.mapper.ResponsesMapper;
import ru.babobka.nodemasterserver.model.ResponseStorage;
import ru.babobka.nodemasterserver.monitoring.TaskMonitoringService;
import ru.babobka.nodemasterserver.service.*;
import ru.babobka.nodemasterserver.slave.IncomingSlaveListenerThread;
import ru.babobka.nodemasterserver.slave.Sessions;
import ru.babobka.nodemasterserver.slave.SlaveFactory;
import ru.babobka.nodemasterserver.slave.SlavesStorage;
import ru.babobka.nodemasterserver.slave.pipeline.SlaveCreatingPipelineFactory;
import ru.babobka.nodemasterserver.thread.HeartBeatingThread;
import ru.babobka.nodetask.TaskPool;
import ru.babobka.nodetask.model.StoppedTasks;
import ru.babobka.nodeutils.container.AbstractApplicationContainer;
import ru.babobka.nodeutils.container.Container;
import ru.babobka.nodeutils.util.StreamUtil;
import ru.babobka.nodeweb.webcontroller.NodeInfoWebController;
import ru.babobka.nodeweb.webcontroller.NodeUsersCRUDWebController;
import ru.babobka.vsjws.webserver.WebServer;
import ru.babobka.vsjws.webserver.WebServerApplicationContainer;
import ru.babobka.vsjws.webserver.WebServerConfig;

import java.io.IOException;
import java.util.concurrent.Executors;

/**
 * Created by 123 on 18.02.2018.
 */
public class MasterServerApplicationSubContainer extends AbstractApplicationContainer {
    private final MasterServerConfig masterServerConfig;

    public MasterServerApplicationSubContainer(@NonNull MasterServerConfig masterServerConfig) {
        this.masterServerConfig = masterServerConfig;
    }

    @Override
    protected void containImpl(Container container) throws Exception {
        StreamUtil streamUtil = container.get(StreamUtil.class);
        container.put(new Sessions());
        container.put(new SlavesStorage());
        container.put(new DistributionService());
        container.put(new ResponseStorage());
        container.put(new SlaveCreatingPipelineFactory());
        container.put(new ClientStorage());
        container.put(MasterServerKey.MASTER_SERVER_TASK_POOL, new TaskPool(
                masterServerConfig.getFolders().getTasksFolder()));
        container.put(new TaskMonitoringService());
        container.put(new ResponsesMapper());
        if (masterServerConfig.getModes().isCacheMode()) {
            container.put(new CacheRequestListener());
            container.put(new TaskServiceCacheProxy(new TaskServiceImpl()));
        } else {
            container.put(new TaskServiceImpl());
        }
        container.put(new StoppedTasks());
        container.put(new SlaveFactory());
        container.put(MasterServerKey.CLIENTS_THREAD_POOL,
                Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors(), r -> {
                    Thread thread = Executors.defaultThreadFactory().newThread(r);
                    thread.setName("client thread pool");
                    thread.setDaemon(true);
                    return thread;
                }));
        container.put(new IncomingClientListenerThread(streamUtil.createServerSocket(
                masterServerConfig.getPorts().getClientListenerPort(), true)));
        container.put(new HeartBeatingThread());
        container.put(new MasterAuthService());
        container.put(new IncomingSlaveListenerThread(streamUtil.createServerSocket(
                masterServerConfig.getPorts().getSlaveListenerPort(), masterServerConfig.getModes().isLocalMachineMode())));
        container.put(new OnTaskIsReady());
        container.put(new OnRaceStyleTaskIsReady());
        container.put(createWebServer(container, masterServerConfig));
    }

    private static WebServer createWebServer(Container container, MasterServerConfig config) throws IOException {
        container.put(new WebServerApplicationContainer());
        container.put(new NodeMasterInfoServiceImpl());
        WebServerConfig webServerConfig = new WebServerConfig();
        webServerConfig.setServerName("node web server");
        webServerConfig.setPort(config.getPorts().getWebListenerPort());
        webServerConfig.setLogFolder(config.getFolders().getLoggerFolder());
        webServerConfig.setSessionTimeoutSeconds(15 * 60);
        WebServer webServer = new WebServer(webServerConfig);
        webServer.addController("users", new NodeUsersCRUDWebController());
        webServer.addController("serverInfo", new NodeInfoWebController());
        return webServer;
    }

}
