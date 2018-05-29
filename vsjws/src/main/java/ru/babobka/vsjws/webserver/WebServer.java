package ru.babobka.vsjws.webserver;

import ru.babobka.nodeutils.container.Container;
import ru.babobka.nodeutils.logger.NodeLogger;
import ru.babobka.nodeutils.util.TextUtil;
import ru.babobka.vsjws.mapper.JSONWebControllerMapper;
import ru.babobka.vsjws.model.http.session.HttpSession;
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

    private static final int SOCKET_READ_TIMEOUT_MILLIS = 2000;
    private final WebServerConfigValidator configValidator = Container.getInstance().get(WebServerConfigValidator.class);
    private final JSONWebControllerMapper jsonWebControllerMapper = Container.getInstance().get(JSONWebControllerMapper.class);
    private final NodeLogger nodeLogger = Container.getInstance().get(NodeLogger.class);
    private final Map<String, HttpWebController> controllerMap = new ConcurrentHashMap<>();
    private final WebServerConfig webServerConfig;
    private final ServerSocket serverSocket;
    private final HttpSession httpSession;
    private final ExecutorService threadPool;

    public WebServer(WebServerConfig config)
            throws IOException {
        setDaemon(true);
        configValidator.validate(config);
        this.webServerConfig = config;
        this.httpSession = new HttpSession(config.getSessionTimeoutSeconds());
        threadPool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors(), r -> {
            Thread t = Executors.defaultThreadFactory().newThread(r);
            t.setDaemon(true);
            return t;
        });
        this.serverSocket = new ServerSocket(webServerConfig.getPort());
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
            nodeLogger.info("run web-server " + getFullName());
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    Socket socket = serverSocket.accept();
                    socket.setSoTimeout(SOCKET_READ_TIMEOUT_MILLIS);
                    threadPool.execute(new SocketProcessorRunnable(httpSession, socket, controllerMap));
                } catch (IOException e) {
                    nodeLogger.error(e);
                }
            }
        } finally {
            clear();
        }
        nodeLogger.info("web-server " + getFullName() + " is done");
    }

    private void clear() {
        threadPool.shutdownNow();
        try {
            serverSocket.close();
        } catch (IOException e) {
            nodeLogger.error(e);
        }
        controllerMap.clear();
        httpSession.clear();
    }

    @Override
    public void interrupt() {
        clear();
        super.interrupt();
    }

    private String getFullName() {
        return TextUtil.beautifyServerName(webServerConfig.getServerName(), webServerConfig.getPort());
    }


}
