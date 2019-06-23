package ru.babobka.nodeconfigs.master;

import ru.babobka.nodeconfigs.NodeConfiguration;
import ru.babobka.nodesecurity.keypair.Base64KeyPair;

import java.util.Objects;

public class MasterServerConfig implements NodeConfiguration {

    private static final long serialVersionUID = 156081573106293600L;
    private ModeConfig modes;
    private PortConfig ports;
    private TimeConfig time;
    private Base64KeyPair keyPair;
    private FolderConfig folders;
    private RmqConfig rmq;

    public ModeConfig getModes() {
        return modes;
    }

    public void setModes(ModeConfig modes) {
        this.modes = modes;
    }

    public PortConfig getPorts() {
        return ports;
    }

    public void setPorts(PortConfig ports) {
        this.ports = ports;
    }

    public TimeConfig getTime() {
        return time;
    }

    public void setTime(TimeConfig time) {
        this.time = time;
    }

    public FolderConfig getFolders() {
        return folders;
    }

    public void setFolders(FolderConfig folders) {
        this.folders = folders;
    }

    public Base64KeyPair getKeyPair() {
        return keyPair;
    }

    public void setKeyPair(Base64KeyPair keyPair) {
        this.keyPair = keyPair;
    }

    public RmqConfig getRmq() {
        return rmq;
    }

    public void setRmq(RmqConfig rmq) {
        this.rmq = rmq;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MasterServerConfig that = (MasterServerConfig) o;
        return Objects.equals(modes, that.modes) &&
                Objects.equals(ports, that.ports) &&
                Objects.equals(time, that.time) &&
                Objects.equals(keyPair, that.keyPair) &&
                Objects.equals(folders, that.folders);
    }

    @Override
    public int hashCode() {
        return Objects.hash(modes, ports, time, keyPair, folders);
    }

    @Override
    public MasterServerConfig copy() {
        MasterServerConfig masterServerConfig = new MasterServerConfig();
        if (this.keyPair != null) {
            Base64KeyPair base64KeyPair = new Base64KeyPair();
            base64KeyPair.setPrivKey(this.keyPair.getPrivKey());
            base64KeyPair.setPubKey(this.keyPair.getPubKey());
            masterServerConfig.setKeyPair(base64KeyPair);
        }
        if (this.ports != null) {
            masterServerConfig.setPorts(this.ports.copy());
        }
        if (this.folders != null) {
            masterServerConfig.setFolders(this.folders.copy());
        }
        if (this.modes != null) {
            masterServerConfig.setModes(this.modes.copy());
        }
        if (this.time != null) {
            masterServerConfig.setTime(this.time.copy());
        }
        if (this.rmq != null) {
            masterServerConfig.setRmq(this.rmq.copy());
        }
        return masterServerConfig;
    }
}
