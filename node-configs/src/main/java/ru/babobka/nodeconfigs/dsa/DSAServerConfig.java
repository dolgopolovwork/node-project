package ru.babobka.nodeconfigs.dsa;

import ru.babobka.nodeconfigs.NodeConfiguration;
import ru.babobka.nodesecurity.keypair.Base64KeyPair;

import java.util.Objects;

public class DSAServerConfig implements NodeConfiguration {
    private static final long serialVersionUID = 156081233106456602L;

    private int port;
    private Base64KeyPair keyPair;
    private String loggerFolder;

    public String getLoggerFolder() {
        return loggerFolder;
    }

    public void setLoggerFolder(String loggerFolder) {
        this.loggerFolder = loggerFolder;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public Base64KeyPair getKeyPair() {
        return keyPair;
    }

    public void setKeyPair(Base64KeyPair keyPair) {
        this.keyPair = keyPair;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DSAServerConfig that = (DSAServerConfig) o;
        return port == that.port &&
                Objects.equals(keyPair, that.keyPair) &&
                Objects.equals(loggerFolder, that.loggerFolder);
    }

    @Override
    public int hashCode() {
        return Objects.hash(port, keyPair, loggerFolder);
    }

    @Override
    public NodeConfiguration copy() {
        DSAServerConfig config = new DSAServerConfig();
        if (this.keyPair != null) {
            Base64KeyPair base64KeyPair = new Base64KeyPair();
            base64KeyPair.setPrivKey(this.keyPair.getPrivKey());
            base64KeyPair.setPubKey(this.keyPair.getPubKey());
            config.setKeyPair(base64KeyPair);
        }
        config.setLoggerFolder(this.loggerFolder);
        config.setPort(this.port);
        return config;
    }
}
