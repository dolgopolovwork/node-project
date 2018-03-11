package ru.babobka.nodemasterserver.server;

import ru.babobka.nodebusiness.service.MasterAuthService;
import ru.babobka.nodemasterserver.client.ClientStorage;
import ru.babobka.nodemasterserver.client.IncomingClientListenerThread;
import ru.babobka.nodemasterserver.listener.CacheRequestListener;
import ru.babobka.nodemasterserver.listener.OnRaceStyleTaskIsReady;
import ru.babobka.nodemasterserver.listener.OnTaskIsReady;
import ru.babobka.nodemasterserver.model.ResponseStorage;
import ru.babobka.nodemasterserver.service.DistributionService;
import ru.babobka.nodemasterserver.service.TaskMonitoringService;
import ru.babobka.nodemasterserver.service.TaskServiceCacheProxy;
import ru.babobka.nodemasterserver.service.TaskServiceImpl;
import ru.babobka.nodemasterserver.slave.IncomingSlaveListenerThread;
import ru.babobka.nodemasterserver.slave.SlaveFactory;
import ru.babobka.nodemasterserver.slave.SlavesStorage;
import ru.babobka.nodemasterserver.thread.HeartBeatingThread;
import ru.babobka.nodetask.TaskPool;
import ru.babobka.nodetask.model.StoppedTasks;
import ru.babobka.nodeutils.container.ApplicationContainer;
import ru.babobka.nodeutils.container.Container;
import ru.babobka.nodeutils.container.ContainerException;
import ru.babobka.nodeutils.network.NodeConnectionFactory;
import ru.babobka.nodeutils.util.StreamUtil;
import ru.babobka.nodeweb.webcontroller.NodeUsersCRUDWebController;
import ru.babobka.vsjws.mapper.JSONWebControllerMapper;
import ru.babobka.vsjws.validator.config.WebServerConfigValidator;
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
            container.put(new SlavesStorage());
            container.put(new DistributionService());
            container.put(new ResponseStorage());
            container.put(new ClientStorage());
            container.put("masterServerTaskPool", new TaskPool(config.getTasksFolder()));
            container.put(new TaskMonitoringService());
            if (config.isEnableCache()) {
                container.put(new CacheRequestListener());
                container.put(new TaskServiceCacheProxy(new TaskServiceImpl()));
            } else {
                container.put(new TaskServiceImpl());
            }
            container.put(new StoppedTasks());
            container.put(new SlaveFactory());
            container.put("clientsThreadPool", Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors()));
            container.put(new IncomingClientListenerThread(streamUtil.createServerSocket(config.getClientListenerPort(), config.isLocalOnly())));
            container.put(new HeartBeatingThread());
            container.put(new MasterAuthService());
            container.put(new NodeConnectionFactory());
            container.put(new IncomingSlaveListenerThread(streamUtil.createServerSocket(config.getSlaveListenerPort(), config.isLocalOnly())));
            container.put(new OnTaskIsReady());
            container.put(new OnRaceStyleTaskIsReady());
            container.put(createWebServer(config));
        } catch (Exception e) {
            throw new ContainerException(e);
        }
    }

    private WebServer createWebServer(MasterServerConfig config) throws IOException {
        Container.getInstance().put(new WebServerConfigValidator());
        Container.getInstance().put(new JSONWebControllerMapper());
        WebServerConfig webServerConfig = new WebServerConfig();
        webServerConfig.setServerName("node web server");
        webServerConfig.setPort(config.getWebListenerPort());
        webServerConfig.setLogFolder(config.getLoggerFolder());
        webServerConfig.setSessionTimeoutSeconds(15 * 60);
        WebServer webServer = new WebServer(webServerConfig);
        webServer.addController("users", new NodeUsersCRUDWebController());
        return webServer;
    }
}
