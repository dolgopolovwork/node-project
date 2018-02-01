package ru.babobka.nodetester.master;

import ru.babobka.nodebusiness.NodeBusinessApplicationContainer;
import ru.babobka.nodebusiness.service.MasterAuthService;
import ru.babobka.nodemasterserver.client.ClientStorage;
import ru.babobka.nodemasterserver.client.IncomingClientListenerThread;
import ru.babobka.nodemasterserver.listener.OnRaceStyleTaskIsReady;
import ru.babobka.nodemasterserver.listener.OnTaskIsReady;
import ru.babobka.nodemasterserver.model.ResponseStorage;
import ru.babobka.nodemasterserver.server.MasterServerConfig;
import ru.babobka.nodemasterserver.service.DistributionService;
import ru.babobka.nodemasterserver.service.TaskServiceImpl;
import ru.babobka.nodemasterserver.slave.IncomingSlaveListenerThread;
import ru.babobka.nodemasterserver.slave.SlaveFactory;
import ru.babobka.nodemasterserver.slave.SlavesStorage;
import ru.babobka.nodemasterserver.thread.HeartBeatingThread;
import ru.babobka.nodemasterserver.validation.config.MasterServerConfigValidator;
import ru.babobka.nodemasterserver.webcontroller.AvailableTasksWebController;
import ru.babobka.nodemasterserver.webcontroller.ClusterInfoWebController;
import ru.babobka.nodemasterserver.webcontroller.TasksInfoWebController;
import ru.babobka.nodetask.NodeTaskApplicationContainer;
import ru.babobka.nodetask.TaskPool;
import ru.babobka.nodetask.model.StoppedTasks;
import ru.babobka.nodeutils.NodeUtilsApplicationContainer;
import ru.babobka.nodeutils.container.ApplicationContainer;
import ru.babobka.nodeutils.container.Container;
import ru.babobka.nodeutils.container.ContainerException;
import ru.babobka.nodeutils.logger.SimpleLogger;
import ru.babobka.nodeutils.network.NodeConnectionFactory;
import ru.babobka.nodeutils.util.HashUtil;
import ru.babobka.nodeutils.util.StreamUtil;
import ru.babobka.nodeweb.NodeWebApplicationContainer;
import ru.babobka.nodeweb.webcontroller.NodeUsersCRUDWebController;
import ru.babobka.nodeweb.webfilter.AuthWebFilter;
import ru.babobka.vsjws.webserver.WebServer;
import ru.babobka.vsjws.webserver.WebServerConfig;

import java.io.IOException;
import java.util.concurrent.Executors;

/**
 * Created by 123 on 05.11.2017.
 */
public class MasterServerApplicationContainer implements ApplicationContainer {
    private static final String REST_HASHED_PASSWORD = HashUtil.hexSha2("123");
    private static final String REST_LOGIN = "abc";

    @Override
    public void contain(Container container) {
        try {
            container.put(new NodeUtilsApplicationContainer());
            StreamUtil streamUtil = container.get(StreamUtil.class);
            MasterServerConfig config = createTestConfig();
            new MasterServerConfigValidator().validate(config);
            container.put(config);
            container.putIfNotExists(SimpleLogger.defaultLogger("master-server", config.getLoggerFolder(), "master"));
            container.put(new NodeTaskApplicationContainer());
            container.put(new NodeBusinessApplicationContainer());
            container.put(new NodeWebApplicationContainer());
            container.put(new SlavesStorage());
            container.put(new DistributionService());
            container.put(new ResponseStorage());
            container.put(new ClientStorage());
            container.put("masterServerTaskPool", new TaskPool(config.getTasksFolder()));
            container.put(new TaskServiceImpl());
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

    private MasterServerConfig createTestConfig() {
        MasterServerConfig config = new MasterServerConfig();
        config.setTasksFolderEnv("NODE_IFT_TASKS");
        config.setLoggerFolderEnv("NODE_IFT_LOGS");
        config.setAuthTimeOutMillis(2000);
        config.setClientListenerPort(9999);
        config.setDebugMode(true);
        config.setHeartBeatTimeOutMillis(5000);
        config.setRequestTimeOutMillis(15000);
        config.setSlaveListenerPort(9090);
        config.setWebListenerPort(8080);
        config.setRestServiceLogin(REST_LOGIN);
        config.setRestServiceHashedPassword(REST_HASHED_PASSWORD);
        return config;
    }

    private WebServer createWebServer(MasterServerConfig config) throws IOException {
        WebServerConfig webServerConfig = new WebServerConfig();
        webServerConfig.setServerName("node web server");
        webServerConfig.setPort(config.getWebListenerPort());
        webServerConfig.setLogFolder(config.getLoggerFolder());
        webServerConfig.setSessionTimeoutSeconds(15 * 60);
        WebServer webServer = new WebServer(webServerConfig);
        AuthWebFilter authWebFilter = new AuthWebFilter(config.getRestServiceLogin(), config.getRestServiceHashedPassword());
        webServer.addController("users", new NodeUsersCRUDWebController()).addWebFilter(authWebFilter);
        webServer.addController("availableTasks", new AvailableTasksWebController()).addWebFilter(authWebFilter);
        webServer.addController("clusterInfo", new ClusterInfoWebController()).addWebFilter(authWebFilter);
        webServer.addController("taskInfo", new TasksInfoWebController()).addWebFilter(authWebFilter);
        return webServer;
    }
}
