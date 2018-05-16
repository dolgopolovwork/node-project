package ru.babobka.nodemasterserver.server.config;

import java.io.Serializable;

/**
 * Created by 123 on 13.05.2018.
 */
public class ModeConfig implements Serializable {
    private static final long serialVersionUID = 7927649430932591211L;
    private boolean debugMode;
    private boolean localMode;
    private boolean cacheMode;

    public boolean isDebugMode() {
        return debugMode;
    }

    public void setDebugMode(boolean debugMode) {
        this.debugMode = debugMode;
    }

    public boolean isLocalMode() {
        return localMode;
    }

    public void setLocalMode(boolean localMode) {
        this.localMode = localMode;
    }

    public boolean isCacheMode() {
        return cacheMode;
    }

    public void setCacheMode(boolean cacheMode) {
        this.cacheMode = cacheMode;
    }
}
