package ru.babobka.nodeconfigs.master;

import java.io.Serializable;

/**
 * Created by 123 on 13.05.2018.
 */
public class ModeConfig implements Serializable {
    private static final long serialVersionUID = 7927649430932591211L;
    private boolean testUserMode;
    private boolean localMachineMode;
    private boolean cacheMode;
    private boolean singleSessionMode;

    public boolean isTestUserMode() {
        return testUserMode;
    }

    public void setTestUserMode(boolean testUserMode) {
        this.testUserMode = testUserMode;
    }

    public boolean isLocalMachineMode() {
        return localMachineMode;
    }

    public void setLocalMachineMode(boolean localMachineMode) {
        this.localMachineMode = localMachineMode;
    }

    public boolean isCacheMode() {
        return cacheMode;
    }

    public void setCacheMode(boolean cacheMode) {
        this.cacheMode = cacheMode;
    }

    public boolean isSingleSessionMode() {
        return singleSessionMode;
    }

    public void setSingleSessionMode(boolean singleSessionMode) {
        this.singleSessionMode = singleSessionMode;
    }
}
