package ru.babobka.masternoderun;

import ru.babobka.nodebusiness.NodeBusinessApplicationContainer;
import ru.babobka.nodebusiness.service.MasterAuthService;
import ru.babobka.nodemasterserver.client.ClientStorage;
import ru.babobka.nodemasterserver.client.IncomingClientListenerThread;
import ru.babobka.nodemasterserver.listener.OnRaceStyleTaskIsReady;
import ru.babobka.nodemasterserver.listener.OnTaskIsReady;
import ru.babobka.nodemasterserver.model.ResponseStorage;
import ru.babobka.nodemasterserver.server.MasterServerConfig;
import ru.babobka.nodemasterserver.service.DistributionService;
import ru.babobka.nodemasterserver.service.TaskMonitoringService;
import ru.babobka.nodemasterserver.service.TaskServiceImpl;
import ru.babobka.nodemasterserver.slave.IncomingSlaveListenerThread;
import ru.babobka.nodemasterserver.slave.SlaveFactory;
import ru.babobka.nodemasterserver.slave.SlavesStorage;
import ru.babobka.nodemasterserver.thread.HeartBeatingThread;
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
import ru.babobka.nodeweb.NodeWebApplicationContainer;
import ru.babobka.nodeweb.webcontroller.NodeUsersCRUDWebController;
import ru.babobka.nodeweb.webfilter.AuthWebFilter;
import ru.babobka.vsjws.webserver.WebServer;
import ru.babobka.vsjws.webserver.WebServerConfig;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.concurrent.Executors;

/**
 * Created by 123 on 05.11.2017.
 */
public class MasterServerApplicationContainer implements ApplicationContainer {

    @Override
    public void contain(Container container) {
        try {
            MasterServerConfig config = container.get(MasterServerConfig.class);
            container.put(SimpleLogger.defaultLogger("master-server", config.getLoggerFolder(), "master"));
            container.put(new NodeUtilsApplicationContainer());
            container.put(new NodeTaskApplicationContainer());
            container.put(new NodeBusinessApplicationContainer());
            container.put(new NodeWebApplicationContainer());
            container.put(new SlavesStorage());
            container.put(new DistributionService());
            container.put(new ResponseStorage());
            container.put(new ClientStorage());
            container.put("masterServerTaskPool", new TaskPool(config.getTasksFolder()));
            container.put(new TaskMonitoringService());
            container.put(new TaskServiceImpl());
            container.put(new StoppedTasks());
            container.put(new SlaveFactory());
            container.put("clientsThreadPool", Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors()));
            container.put(new IncomingClientListenerThread(new ServerSocket(config.getClientListenerPort())));
            container.put(new HeartBeatingThread());
            container.put(new MasterAuthService());
            container.put(new NodeConnectionFactory());
            container.put(new IncomingSlaveListenerThread(new ServerSocket(config.getSlaveListenerPort())));
            container.put(new OnTaskIsReady());
            container.put(new OnRaceStyleTaskIsReady());
            container.put(createWebServer(config));
        } catch (IOException | RuntimeException e) {
            throw new ContainerException(e);
        }
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
