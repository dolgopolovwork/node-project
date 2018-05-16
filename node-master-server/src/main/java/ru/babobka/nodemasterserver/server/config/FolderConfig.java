package ru.babobka.nodemasterserver.server.config;

import ru.babobka.nodeutils.util.TextUtil;

import java.io.Serializable;

/**
 * Created by 123 on 13.05.2018.
 */
public class FolderConfig implements Serializable {

    private static final long serialVersionUID = 3745117609183677038L;
    private String loggerFolder;
    private String tasksFolder;
    private String loggerFolderEnv;
    private String tasksFolderEnv;

    public String getLoggerFolder() {
        return TextUtil.getFirstNonNull(loggerFolder, TextUtil.getEnv(loggerFolderEnv));
    }

    public void setLoggerFolder(String loggerFolder) {
        this.loggerFolder = loggerFolder;
    }

    public String getTasksFolder() {
        return TextUtil.getFirstNonNull(tasksFolder, TextUtil.getEnv(tasksFolderEnv));
    }

    public void setTasksFolder(String tasksFolder) {
        this.tasksFolder = tasksFolder;
    }

    public String getLoggerFolderEnv() {
        return loggerFolderEnv;
    }

    public void setLoggerFolderEnv(String loggerFolderEnv) {
        this.loggerFolderEnv = loggerFolderEnv;
    }

    public String getTasksFolderEnv() {
        return tasksFolderEnv;
    }

    public void setTasksFolderEnv(String tasksFolderEnv) {
        this.tasksFolderEnv = tasksFolderEnv;
    }
}
