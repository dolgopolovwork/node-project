package ru.babobka.nodeconfigs.master;

import ru.babobka.nodeconfigs.NodeConfiguration;

import java.util.Objects;

/**
 * Created by 123 on 13.05.2018.
 */
public class ModeConfig implements NodeConfiguration {
    private static final long serialVersionUID = 7927649430932591211L;
    private boolean testUserMode;
    private boolean cacheMode;
    private boolean singleSessionMode;

    public boolean isTestUserMode() {
        return testUserMode;
    }

    public void setTestUserMode(boolean testUserMode) {
        this.testUserMode = testUserMode;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ModeConfig that = (ModeConfig) o;
        return testUserMode == that.testUserMode &&
                cacheMode == that.cacheMode &&
                singleSessionMode == that.singleSessionMode;
    }

    @Override
    public int hashCode() {
        return Objects.hash(testUserMode, cacheMode, singleSessionMode);
    }

    @Override
    public ModeConfig copy() {
        ModeConfig modeConfig = new ModeConfig();
        modeConfig.setSingleSessionMode(this.singleSessionMode);
        modeConfig.setCacheMode(this.cacheMode);
        modeConfig.setTestUserMode(this.testUserMode);
        return modeConfig;
    }
}
