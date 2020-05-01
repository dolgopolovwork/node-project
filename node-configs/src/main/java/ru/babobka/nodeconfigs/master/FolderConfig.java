package ru.babobka.nodeconfigs.master;

import ru.babobka.nodeconfigs.NodeConfiguration;

import java.util.Objects;

/**
 * Created by 123 on 13.05.2018.
 */
public class FolderConfig implements NodeConfiguration {

    private static final long serialVersionUID = 3745117609183677038L;
    private String loggerFolder;
    private String tasksFolder;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FolderConfig that = (FolderConfig) o;
        return Objects.equals(loggerFolder, that.loggerFolder) &&
                Objects.equals(tasksFolder, that.tasksFolder);
    }

    @Override
    public int hashCode() {
        return Objects.hash(loggerFolder, tasksFolder);
    }

    @Override
    public FolderConfig copy() {
        FolderConfig folderConfig = new FolderConfig();
        folderConfig.setLoggerFolder(loggerFolder);
        folderConfig.setTasksFolder(tasksFolder);
        return folderConfig;
    }
}
