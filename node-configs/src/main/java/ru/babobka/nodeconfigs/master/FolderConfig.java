package ru.babobka.nodeconfigs.master;

import ru.babobka.nodeutils.util.TextUtil;

import java.io.Serializable;

/**
 * Created by 123 on 13.05.2018.
 */
public class FolderConfig implements Serializable {

    private static final long serialVersionUID = 3745117609183677038L;
    private String loggerFolder;
    private String tasksFolder;

    public String getLoggerFolder() {
        if (loggerFolder != null && loggerFolder.startsWith("$")) {
            return TextUtil.getEnv(loggerFolder.substring(1));
        }
        return loggerFolder;
    }

    public void setLoggerFolder(String loggerFolder) {
        this.loggerFolder = loggerFolder;
    }

    public String getTasksFolder() {
        if (tasksFolder != null && tasksFolder.startsWith("$")) {
            return TextUtil.getEnv(tasksFolder.substring(1));
        }
        return tasksFolder;
    }

    public void setTasksFolder(String tasksFolder) {
        this.tasksFolder = tasksFolder;
    }

}
