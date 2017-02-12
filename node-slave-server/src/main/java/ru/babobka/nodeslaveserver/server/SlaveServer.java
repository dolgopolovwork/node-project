package ru.babobka.nodeslaveserver.server;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

import ru.babobka.nodeslaveserver.controller.SocketController;
import ru.babobka.nodeslaveserver.controller.SocketControllerImpl;
import ru.babobka.nodeslaveserver.exception.SlaveAuthFailException;
import ru.babobka.nodeslaveserver.model.CommandLineArgs;
import ru.babobka.nodeslaveserver.runnable.GlitchRunnable;
import ru.babobka.nodeslaveserver.service.AuthService;
import ru.babobka.nodeslaveserver.task.TasksStorage;
import ru.babobka.nodeutils.container.Container;
import ru.babobka.nodeutils.container.ContainerStrategyException;
import ru.babobka.nodeutils.logger.SimpleLogger;
import ru.babobka.nodeutils.util.StreamUtil;

public class SlaveServer extends Thread {

    private static final String SLAVE_SERVER_TEST_CONFIG = "slave_config.json";

    private final AuthService authService = Container.getInstance().get(AuthService.class);

    private final Thread glitchThread;

    private final Socket socket;

    private final SimpleLogger logger = Container.getInstance().get(SimpleLogger.class);

    private final TasksStorage tasksStorage;

    public SlaveServer(String serverHost, int port, String login, String password) throws IOException {
	this(serverHost, port, login, password, false);
    }

    public SlaveServer(String serverHost, int port, String login, String password, boolean glitchy) throws IOException {
	socket = new Socket(InetAddress.getByName(serverHost), port);
	logger.info("Connection was successfully established");
	if (!authService.auth(socket, login, password)) {
	    logger.error("Auth fail");
	    throw new SlaveAuthFailException();
	} else {
	    logger.info("Auth success");
	}
	tasksStorage = new TasksStorage();
	if (glitchy) {
	    glitchThread = new Thread(new GlitchRunnable(socket));
	} else {
	    glitchThread = null;
	}
    }

    public static void initTestContainer() throws ContainerStrategyException, FileNotFoundException {
	new SlaveServerContainerStrategy(
		StreamUtil.getLocalResource(SlaveServer.class, SlaveServer.SLAVE_SERVER_TEST_CONFIG))
			.contain(Container.getInstance());
    }

    @Override
    public void run() {
	if (glitchThread != null)
	    glitchThread.start();
	try (SocketController controller = new SocketControllerImpl(tasksStorage);) {
	    while (!Thread.currentThread().isInterrupted()) {
		controller.control(socket);
	    }
	} catch (IOException e) {
	    if (!socket.isClosed()) {
		logger.error(e);
	    } else {
		logger.info("Slave server is done");
	    }

	} finally {
	    clear();
	}
    }

    @Override
    public void interrupt() {

	super.interrupt();
	clear();
    }

    private void clear() {
	if (glitchThread != null)
	    glitchThread.interrupt();
	tasksStorage.stopAllTheTasks();
	if (socket != null) {
	    try {
		socket.close();
	    } catch (IOException e) {
		logger.error(e);
	    }
	}
    }

    public static void main(String[] args)
	    throws InterruptedException, ContainerStrategyException, FileNotFoundException {
	new SlaveServerContainerStrategy(new FileInputStream(SLAVE_SERVER_TEST_CONFIG))
		.contain(Container.getInstance());
	SimpleLogger mainLogger = Container.getInstance().get(SimpleLogger.class);
	CommandLineArgs command = new CommandLineArgs(args);
	while (!Thread.currentThread().isInterrupted()) {
	    try {
		SlaveServer slaveSever = new SlaveServer(command.getHost(), command.getPort(), command.getLogin(),
			command.getPassword());
		slaveSever.start();
		slaveSever.join();

	    } catch (SlaveAuthFailException e) {
		mainLogger.error(e);
		return;
	    } catch (IOException e) {
		mainLogger.error(e);
		mainLogger.warning("Reconnecting");
		Thread.sleep(1000);
	    }
	}

    }
}
