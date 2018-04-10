package ru.babobka.vsjws.webserver;

import ru.babobka.nodeutils.container.Container;
import ru.babobka.nodeutils.logger.SimpleLogger;
import ru.babobka.nodeutils.util.TextUtil;
import ru.babobka.vsjws.listener.OnServerStartListener;
import ru.babobka.vsjws.mapper.JSONWebControllerMapper;
import ru.babobka.vsjws.model.http.HttpSession;
import ru.babobka.vsjws.runnable.SocketProcessorRunnable;
import ru.babobka.vsjws.validator.config.WebServerConfigValidator;
import ru.babobka.vsjws.webcontroller.HttpWebController;
import ru.babobka.vsjws.webcontroller.JSONWebController;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by dolgopolov.a on 30.12.15.
 */
public class WebServer extends Thread {

    private final WebServerConfigValidator configValidator = Container.getInstance().get(WebServerConfigValidator.class);
    private final JSONWebControllerMapper jsonWebControllerMapper = Container.getInstance().get(JSONWebControllerMapper.class);
    private static final int SOCKET_READ_TIMEOUT_MILLIS = 2000;
    private static final int THREAD_POOL_SIZE = 10;
    private final Map<String, HttpWebController> controllerMap = new ConcurrentHashMap<>();
    private final String name;
    private final ServerSocket serverSocket;
    private final HttpSession httpSession;
    private final SimpleLogger logger = Container.getInstance().get(SimpleLogger.class);
    private final ExecutorService threadPool;
    private final int port;
    private volatile OnServerStartListener onServerStartListener;

    public WebServer(WebServerConfig config)
            throws IOException {
        setDaemon(true);
        configValidator.validate(config);
        this.name = config.getServerName();
        this.port = config.getPort();
        this.httpSession = new HttpSession(config.getSessionTimeoutSeconds());
        logger.debug("debug mode is on");
        threadPool = Executors.newFixedThreadPool(THREAD_POOL_SIZE);
        this.serverSocket = new ServerSocket(port);
    }

    public HttpWebController addController(String uri, HttpWebController httpWebController) {
        if (uri.startsWith("/")) {
            controllerMap.put(uri, httpWebController);
        } else {
            controllerMap.put("/" + uri, httpWebController);
        }
        return httpWebController;
    }

    public HttpWebController addController(String uri, JSONWebController jsonWebController) {
        return addController(uri, jsonWebControllerMapper.map(httpSession, jsonWebController));
    }

    public void run() {
        try {
            logger.info("run web-server " + getFullName());
            OnServerStartListener listener = onServerStartListener;
            if (listener != null) {
                listener.onStart(name, port);
            }
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    Socket s = serverSocket.accept();
                    s.setSoTimeout(SOCKET_READ_TIMEOUT_MILLIS);
                    threadPool.execute(new SocketProcessorRunnable(s, controllerMap, httpSession));
                } catch (IOException e) {
                    if (!serverSocket.isClosed()) {
                        logger.error(e);
                    }
                }
            }
        } finally {
            clear();
        }
        logger.info("web-server " + getFullName() + " is done");
    }

    private void clear() {
        threadPool.shutdownNow();
        try {
            serverSocket.close();
        } catch (IOException e) {
            logger.error(e);
        }
        controllerMap.clear();
        httpSession.clear();
    }

    @Override
    public void interrupt() {
        clear();
        super.interrupt();
    }

    public void setOnServerStartListener(OnServerStartListener onServerStartListener) {
        this.onServerStartListener = onServerStartListener;
    }

    private String getFullName() {
        return TextUtil.beautifyServerName(name, port);
    }


}
