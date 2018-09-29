package ru.babobka.nodeconfigs.master;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import ru.babobka.nodeconfigs.NodeConfiguration;

public class MasterServerConfig implements NodeConfiguration {

    private static final long serialVersionUID = 156081573106293600L;
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private ModeConfig modes;
    private PortConfig ports;
    private TimeConfig time;
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

    public TimeConfig getTime() {
        return time;
    }

    public void setTime(TimeConfig time) {
        this.time = time;
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

    @Override
    public String toString() {
        return GSON.toJson(this);
    }
}
