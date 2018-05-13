package ru.babobka.nodemasterserver.server.config;

import java.io.Serializable;

public class MasterServerConfig implements Serializable {

    private static final long serialVersionUID = 156081573106293600L;
    private ModeConfig modes;
    private PortConfig ports;
    private TimeoutConfig timeouts;
    private SecurityConfig security;
    private FolderConfig folders;

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

    public TimeoutConfig getTimeouts() {
        return timeouts;
    }

    public void setTimeouts(TimeoutConfig timeouts) {
        this.timeouts = timeouts;
    }

    public SecurityConfig getSecurity() {
        return security;
    }

    public void setSecurity(SecurityConfig security) {
        this.security = security;
    }

    public FolderConfig getFolders() {
        return folders;
    }

    public void setFolders(FolderConfig folders) {
        this.folders = folders;
    }
}
