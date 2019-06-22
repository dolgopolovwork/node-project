package ru.babobka.nodeconfigs.slave;

import ru.babobka.nodeconfigs.NodeConfiguration;
import ru.babobka.nodesecurity.keypair.Base64KeyPair;

import java.util.Objects;

public class SlaveServerConfig implements NodeConfiguration {

    private static final long serialVersionUID = 9130829367317179440L;
    private int masterServerPort;
    private String masterServerHost;
    private String masterServerBase64PublicKey;
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
        return loggerFolder;
    }

    public void setLoggerFolder(String loggerFolder) {
        this.loggerFolder = loggerFolder;
    }

    public String getTasksFolder() {
        return tasksFolder;
    }

    public void setTasksFolder(String tasksFolder) {
        this.tasksFolder = tasksFolder;
    }

    public int getMasterServerPort() {
        return masterServerPort;
    }

    public void setMasterServerPort(int masterServerPort) {
        this.masterServerPort = masterServerPort;
    }

    public String getMasterServerHost() {
        return masterServerHost;
    }

    public void setMasterServerHost(String masterServerHost) {
        this.masterServerHost = masterServerHost;
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

    public String getMasterServerBase64PublicKey() {
        return masterServerBase64PublicKey;
    }

    public void setMasterServerBase64PublicKey(String masterServerBase64PublicKey) {
        this.masterServerBase64PublicKey = masterServerBase64PublicKey;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SlaveServerConfig that = (SlaveServerConfig) o;
        return masterServerPort == that.masterServerPort &&
                requestTimeoutMillis == that.requestTimeoutMillis &&
                authTimeOutMillis == that.authTimeOutMillis &&
                Objects.equals(masterServerHost, that.masterServerHost) &&
                Objects.equals(masterServerBase64PublicKey, that.masterServerBase64PublicKey) &&
                Objects.equals(loggerFolder, that.loggerFolder) &&
                Objects.equals(tasksFolder, that.tasksFolder) &&
                Objects.equals(slaveLogin, that.slaveLogin) &&
                Objects.equals(keyPair, that.keyPair);
    }

    @Override
    public int hashCode() {
        return Objects.hash(masterServerPort, masterServerHost, masterServerBase64PublicKey, requestTimeoutMillis, authTimeOutMillis, loggerFolder, tasksFolder, slaveLogin, keyPair);
    }

    @Override
    public SlaveServerConfig copy() {
        SlaveServerConfig slaveServerConfig = new SlaveServerConfig();
        slaveServerConfig.setSlaveLogin(this.slaveLogin);
        slaveServerConfig.setRequestTimeoutMillis(this.requestTimeoutMillis);
        if (this.keyPair != null) {
            Base64KeyPair base64KeyPair = new Base64KeyPair();
            base64KeyPair.setPrivKey(this.keyPair.getPrivKey());
            base64KeyPair.setPubKey(this.keyPair.getPubKey());
            slaveServerConfig.setKeyPair(base64KeyPair);
        }
        slaveServerConfig.setAuthTimeOutMillis(this.authTimeOutMillis);
        slaveServerConfig.setMasterServerPort(this.masterServerPort);
        slaveServerConfig.setLoggerFolder(this.loggerFolder);
        slaveServerConfig.setTasksFolder(this.tasksFolder);
        slaveServerConfig.setMasterServerBase64PublicKey(this.masterServerBase64PublicKey);
        slaveServerConfig.setMasterServerHost(this.masterServerHost);
        return slaveServerConfig;
    }
}
