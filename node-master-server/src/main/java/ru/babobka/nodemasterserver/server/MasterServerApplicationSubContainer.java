package ru.babobka.nodemasterserver.server;

import ru.babobka.nodemasterserver.client.ClientStorage;
import ru.babobka.nodemasterserver.client.IncomingClientListenerThread;
import ru.babobka.nodemasterserver.key.MasterServerKey;
import ru.babobka.nodemasterserver.listener.CacheRequestListener;
import ru.babobka.nodemasterserver.listener.OnRaceStyleTaskIsReady;
import ru.babobka.nodemasterserver.listener.OnTaskIsReady;
import ru.babobka.nodemasterserver.mapper.ResponsesMapper;
import ru.babobka.nodemasterserver.model.ResponseStorage;
import ru.babobka.nodemasterserver.monitoring.TaskMonitoringService;
import ru.babobka.nodemasterserver.server.config.MasterServerConfig;
import ru.babobka.nodemasterserver.service.DistributionService;
import ru.babobka.nodemasterserver.service.MasterAuthService;
import ru.babobka.nodemasterserver.service.TaskServiceCacheProxy;
import ru.babobka.nodemasterserver.service.TaskServiceImpl;
import ru.babobka.nodemasterserver.slave.IncomingSlaveListenerThread;
import ru.babobka.nodemasterserver.slave.Sessions;
import ru.babobka.nodemasterserver.slave.SlaveFactory;
import ru.babobka.nodemasterserver.slave.SlavesStorage;
import ru.babobka.nodemasterserver.thread.HeartBeatingThread;
import ru.babobka.nodetask.TaskPool;
import ru.babobka.nodetask.model.StoppedTasks;
import ru.babobka.nodeutils.container.ApplicationContainer;
import ru.babobka.nodeutils.container.Container;
import ru.babobka.nodeutils.container.ContainerException;
import ru.babobka.nodeutils.util.StreamUtil;
import ru.babobka.nodeweb.webcontroller.NodeUsersCRUDWebController;
import ru.babobka.vsjws.mapper.JSONWebControllerMapper;
import ru.babobka.vsjws.validator.config.WebServerConfigValidator;
import ru.babobka.vsjws.validator.request.RequestValidator;
import ru.babobka.vsjws.webserver.WebServer;
import ru.babobka.vsjws.webserver.WebServerConfig;

import java.io.IOException;
import java.util.concurrent.Executors;

/**
 * Created by 123 on 18.02.2018.
 */
public class MasterServerApplicationSubContainer implements ApplicationContainer {
    @Override
    public void contain(Container container) {
        try {
            StreamUtil streamUtil = container.get(StreamUtil.class);
            MasterServerConfig config = container.get(MasterServerConfig.class);
            container.put(new Sessions());
            container.put(new SlavesStorage());
            container.put(new DistributionService());
            container.put(new ResponseStorage());
            container.put(new ClientStorage());
            container.put(MasterServerKey.MASTER_SERVER_TASK_POOL, new TaskPool(
                    config.getFolders().getTasksFolder()));
            container.put(new TaskMonitoringService());
            container.put(new ResponsesMapper());
            if (config.getModes().isCacheMode()) {
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
                    config.getPorts().getClientListenerPort(), true)));
            container.put(new HeartBeatingThread());
            container.put(new MasterAuthService());
            container.put(new IncomingSlaveListenerThread(streamUtil.createServerSocket(
                    config.getPorts().getSlaveListenerPort(), config.getModes().isLocalMachineMode())));
            container.put(new OnTaskIsReady());
            container.put(new OnRaceStyleTaskIsReady());
            container.put(createWebServer(container, config));
        } catch (RuntimeException | IOException e) {
            throw new ContainerException(e);
        }
    }

    private static WebServer createWebServer(Container container, MasterServerConfig config) throws IOException {
        container.put(new RequestValidator());
        container.put(new WebServerConfigValidator());
        container.put(new JSONWebControllerMapper());
        WebServerConfig webServerConfig = new WebServerConfig();
        webServerConfig.setServerName("node web server");
        webServerConfig.setPort(config.getPorts().getWebListenerPort());
        webServerConfig.setLogFolder(config.getFolders().getLoggerFolder());
        webServerConfig.setSessionTimeoutSeconds(15 * 60);
        WebServer webServer = new WebServer(webServerConfig);
        webServer.addController("users", new NodeUsersCRUDWebController());
        return webServer;
    }

}
