package ru.babobka.nodemasterserver.server;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.concurrent.TimeoutException;

import ru.babobka.nodeutils.container.Container;
import ru.babobka.nodeutils.container.ContainerStrategyException;
import ru.babobka.nodemasterserver.datasource.RedisDatasource;
import ru.babobka.nodemasterserver.exception.TaskNotFoundException;
import ru.babobka.nodemasterserver.listener.OnIllegalArgumentExceptionListener;
import ru.babobka.nodemasterserver.listener.OnIllegalStateExceptionListener;
import ru.babobka.nodemasterserver.listener.OnTaskNotFoundExceptionListener;
import ru.babobka.nodemasterserver.listener.OnTimeoutExceptionListener;
import ru.babobka.nodeutils.logger.SimpleLogger;
import ru.babobka.nodeutils.util.StreamUtil;
import ru.babobka.nodeutils.util.TextUtil;
import ru.babobka.nodemasterserver.runnable.HeartBeatingRunnable;
import ru.babobka.nodemasterserver.service.NodeUsersService;
import ru.babobka.nodemasterserver.slave.IncomingSlaveListenerThread;
import ru.babobka.nodemasterserver.slave.SlavesStorage;
import ru.babobka.nodemasterserver.task.TaskPool;
import ru.babobka.nodemasterserver.webcontroller.AvailableTasksWebController;
import ru.babobka.nodemasterserver.webcontroller.CancelTaskWebController;
import ru.babobka.nodemasterserver.webcontroller.ClusterInfoWebController;
import ru.babobka.nodemasterserver.webcontroller.NodeUsersCRUDWebController;
import ru.babobka.nodemasterserver.webcontroller.TaskWebController;
import ru.babobka.nodemasterserver.webcontroller.TasksInfoWebController;
import ru.babobka.nodemasterserver.webfilter.AuthWebFilter;
import ru.babobka.nodemasterserver.webfilter.CacheWebFilter;
import ru.babobka.nodemasterserver.webfilter.JSONWebFilter;
import ru.babobka.vsjws.model.HttpRequest;
import ru.babobka.vsjws.webcontroller.WebFilter;
import ru.babobka.vsjws.webserver.WebServer;

/**
 * Created by dolgopolov.a on 16.07.15.
 */
public final class MasterServer extends Thread {

    private static final String MASTER_SERVER_TEST_CONFIG = "master_config.json";

    private final NodeUsersService userService = Container.getInstance().get(NodeUsersService.class);

    private final TaskPool taskPool = Container.getInstance().get(TaskPool.class);

    private final Thread heartBeatingThread;

    private final Thread listenerThread;

    private final WebServer webServer;

    private final SlavesStorage slavesStorage = Container.getInstance().get(SlavesStorage.class);

    private final MasterServerConfig config = Container.getInstance().get(MasterServerConfig.class);

    private final RedisDatasource datasource = Container.getInstance().get(RedisDatasource.class);

    private final SimpleLogger logger = Container.getInstance().get(SimpleLogger.class);

    public MasterServer() throws IOException {

	if (!config.isDebugDataBase() && !datasource.getPool().getResource().isConnected()) {
	    throw new IOException("Database is not connected");
	}
	if (!config.isProductionDataBase()) {
	    userService.addTestUser();
	}

	listenerThread = new IncomingSlaveListenerThread(config.getMainServerPort());
	heartBeatingThread = new Thread(new HeartBeatingRunnable());
	webServer = new WebServer("rest server", config.getWebPort(),
		config.getLoggerFolder() + File.separator + "rest_log");

	WebFilter authWebFilter = new AuthWebFilter();
	WebFilter cacheWebFilter = new CacheWebFilter();
	for (String taskName : taskPool.getTasksMap().keySet()) {
	    webServer.addController("task/" + TextUtil.toURL(taskName),
		    new TaskWebController().addWebFilter(authWebFilter).addWebFilter(cacheWebFilter));
	}
	webServer.addController("cancelTask", new CancelTaskWebController().addWebFilter(authWebFilter));
	webServer.addController("clusterInfo", new ClusterInfoWebController().addWebFilter(authWebFilter));
	webServer.addController("users", new NodeUsersCRUDWebController().addWebFilter(authWebFilter)
		.addWebFilter(new JSONWebFilter(HttpRequest.HttpMethod.PATCH, HttpRequest.HttpMethod.POST)));
	webServer.addController("tasksInfo", new TasksInfoWebController().addWebFilter(authWebFilter));
	webServer.addController("availableTasks", new AvailableTasksWebController().addWebFilter(authWebFilter));
	webServer.addExceptionListener(IllegalArgumentException.class, new OnIllegalArgumentExceptionListener());
	webServer.addExceptionListener(IllegalStateException.class, new OnIllegalStateExceptionListener());
	webServer.addExceptionListener(TaskNotFoundException.class, new OnTaskNotFoundExceptionListener());
	webServer.addExceptionListener(TimeoutException.class, new OnTimeoutExceptionListener());
    }

    public static void initTestContainer() throws ContainerStrategyException, FileNotFoundException {
	new MasterServerContainerStrategy(
		StreamUtil.getLocalResource(MasterServer.class, MasterServer.MASTER_SERVER_TEST_CONFIG))
			.contain(Container.getInstance());
    }

    @Override
    public void run() {
	try {
	    listenerThread.start();
	    heartBeatingThread.start();
	    webServer.start();
	} catch (Exception e) {
	    logger.error(e);
	    clear();
	}

    }

    @Override
    public void interrupt() {
	super.interrupt();
	clear();
    }

    private synchronized void clear() {
	interruptAndJoin(webServer);
	interruptAndJoin(listenerThread);
	interruptAndJoin(listenerThread);
	if (slavesStorage != null) {
	    slavesStorage.clear();
	}
	if (datasource != null) {
	    datasource.getPool().close();
	}

    }

    private void interruptAndJoin(Thread thread) {
	thread.interrupt();
	try {
	    thread.join();
	} catch (InterruptedException e) {
	    thread.interrupt();
	    logger.error(e);
	}

    }

    public static void main(String[] args) throws IOException, ContainerStrategyException {
	/*
	 * new MasterServerContainerStrategy( new
	 * FileInputStream(MASTER_SERVER_TEST_CONFIG))
	 * .contain(Container.getInstance());
	 */

	new MasterServerContainerStrategy(
		StreamUtil.getLocalResource(MasterServer.class, MasterServer.MASTER_SERVER_TEST_CONFIG))
			.contain(Container.getInstance());

	MasterServer server = new MasterServer();
	server.start();

    }

}