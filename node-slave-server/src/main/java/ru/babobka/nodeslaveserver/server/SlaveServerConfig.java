package ru.babobka.nodeslaveserver.server;

import ru.babobka.nodesecurity.rsa.RSAPublicKey;
import ru.babobka.nodeutils.config.NodeConfiguration;
import ru.babobka.nodeutils.util.TextUtil;

public class SlaveServerConfig implements NodeConfiguration {

    private static final long serialVersionUID = 9130829367317179440L;
    private int serverPort;
    private String serverHost;
    private RSAPublicKey serverPublicKey;
    private int requestTimeoutMillis;
    private int authTimeoutMillis;
    private String loggerFolder;
    private String tasksFolder;
    private String loggerFolderEnv;
    private String tasksFolderEnv;

    public int getRequestTimeoutMillis() {
        return requestTimeoutMillis;
    }

    public void setRequestTimeoutMillis(int requestTimeoutMillis) {
        this.requestTimeoutMillis = requestTimeoutMillis;
    }

    public int getAuthTimeoutMillis() {
        return authTimeoutMillis;
    }

    public void setAuthTimeoutMillis(int authTimeoutMillis) {
        this.authTimeoutMillis = authTimeoutMillis;
    }

    public String getLoggerFolder() {
        return TextUtil.getFirstNonNull(loggerFolder, TextUtil.getEnv(loggerFolderEnv));
    }

    public void setLoggerFolder(String loggerFolder) {
        this.loggerFolder = loggerFolder;
    }

    public String getTasksFolder() {
        return TextUtil.getFirstNonNull(tasksFolder, TextUtil.getEnv(tasksFolderEnv));
    }

    public void setTasksFolder(String tasksFolder) {
        this.tasksFolder = tasksFolder;
    }

    public int getServerPort() {
        return serverPort;
    }

    public void setServerPort(int serverPort) {
        this.serverPort = serverPort;
    }

    public String getServerHost() {
        return serverHost;
    }

    public void setServerHost(String serverHost) {
        this.serverHost = serverHost;
    }

    public String getLoggerFolderEnv() {
        return loggerFolderEnv;
    }

    public void setLoggerFolderEnv(String loggerFolderEnv) {
        this.loggerFolderEnv = loggerFolderEnv;
    }

    public String getTasksFolderEnv() {
        return tasksFolderEnv;
    }

    public void setTasksFolderEnv(String tasksFolderEnv) {
        this.tasksFolderEnv = tasksFolderEnv;
    }

    public RSAPublicKey getServerPublicKey() {
        return serverPublicKey;
    }

    public void setServerPublicKey(RSAPublicKey serverPublicKey) {
        this.serverPublicKey = serverPublicKey;
    }
}
