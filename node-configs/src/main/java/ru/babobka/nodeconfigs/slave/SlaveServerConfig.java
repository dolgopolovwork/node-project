package ru.babobka.nodeconfigs.slave;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import ru.babobka.nodeconfigs.NodeConfiguration;
import ru.babobka.nodesecurity.keypair.Base64KeyPair;
import ru.babobka.nodeutils.util.TextUtil;

public class SlaveServerConfig implements NodeConfiguration {

    private static final long serialVersionUID = 9130829367317179440L;
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private int serverPort;
    private String serverHost;
    private String serverBase64PublicKey;
    private int requestTimeoutMillis;
    private int authTimeOutMillis;
    private String loggerFolder;
    private String tasksFolder;
    private String slaveLogin;
    private Base64KeyPair keyPair;

    public int getRequestTimeoutMillis() {
        return requestTimeoutMillis;
    }

    public void setRequestTimeoutMillis(int requestTimeoutMillis) {
        this.requestTimeoutMillis = requestTimeoutMillis;
    }

    public int getAuthTimeOutMillis() {
        return authTimeOutMillis;
    }

    public void setAuthTimeOutMillis(int authTimeOutMillis) {
        this.authTimeOutMillis = authTimeOutMillis;
    }

    public String getLoggerFolder() {
        if (loggerFolder != null && loggerFolder.startsWith("$")) {
            return TextUtil.getEnv(loggerFolder.substring(1));
        }
        return loggerFolder;
    }

    public void setLoggerFolder(String loggerFolder) {
        this.loggerFolder = loggerFolder;
    }

    public String getTasksFolder() {
        if (tasksFolder != null && tasksFolder.startsWith("$")) {
            return TextUtil.getEnv(tasksFolder.substring(1));
        }
        return tasksFolder;
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

    public String getSlaveLogin() {
        return slaveLogin;
    }

    public void setSlaveLogin(String slaveLogin) {
        this.slaveLogin = slaveLogin;
    }

    public Base64KeyPair getKeyPair() {
        return keyPair;
    }

    public void setKeyPair(Base64KeyPair keyPair) {
        this.keyPair = keyPair;
    }

    public String getServerBase64PublicKey() {
        return serverBase64PublicKey;
    }

    public void setServerBase64PublicKey(String serverBase64PublicKey) {
        this.serverBase64PublicKey = serverBase64PublicKey;
    }

    @Override
    public String toString() {
        return GSON.toJson(this);
    }
}
