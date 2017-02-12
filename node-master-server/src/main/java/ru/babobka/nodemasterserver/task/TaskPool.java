package ru.babobka.nodemasterserver.task;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import ru.babobka.nodeutils.container.Container;
import ru.babobka.nodemasterserver.exception.CanNotInitTaskFactoryException;
import ru.babobka.nodemasterserver.server.MasterServerConfig;
import ru.babobka.nodeutils.logger.SimpleLogger;
import ru.babobka.nodeutils.util.StreamUtil;
import ru.babobka.nodeutils.util.TextUtil;
import ru.babobka.subtask.model.SubTask;

/**
 * Created by dolgopolov.a on 09.07.15.
 */
public class TaskPool {

    private final Map<String, TaskContext> tasksMap = new ConcurrentHashMap<>();

    private final SimpleLogger logger = Container.getInstance().get(SimpleLogger.class);

    private final MasterServerConfig masterServerConfig = Container.getInstance().get(MasterServerConfig.class);

    public TaskPool() throws CanNotInitTaskFactoryException {
	init();
    }

    private void init() throws CanNotInitTaskFactoryException {
	try {
	    File tasksFolder = new File(masterServerConfig.getTasksFolder());
	    String taskFolder = tasksFolder.getAbsolutePath();
	    List<String> files = StreamUtil.getJarFileListFromFolder(taskFolder);
	    for (String file : files) {
		try {
		    String jarFilePath = taskFolder + File.separator + file;
		    List<SubTask> subTasks = StreamUtil.getSubtasks(jarFilePath);
		    for (SubTask subTask : subTasks) {
			TaskConfig config = new TaskConfig(subTask);
			tasksMap.put(TextUtil.toURL(config.getName()), new TaskContext(subTask, config));
		    }
		} catch (Exception e) {
		    logger.error("Can not init factory with file " + file, e);
		    throw new CanNotInitTaskFactoryException(e);
		}
	    }

	} catch (RuntimeException e) {
	    throw new CanNotInitTaskFactoryException(
		    "Can not init factory pool. Try to redownload new jars to node-slave-server task folder", e);
	}
	if (tasksMap.isEmpty()) {
	    throw new CanNotInitTaskFactoryException(
		    "Can not init factory pool. No task to run. Try to redownload new jars to node-slave-server task folder");
	}

    }

    public Map<String, TaskContext> getTasksMap() {
	return tasksMap;
    }

    public TaskContext get(String name) throws IOException {

	TaskContext taskContext = tasksMap.get(TextUtil.toURL(name));
	if (taskContext != null) {
	    return taskContext.newInstance();
	} else {
	    throw new IOException("Task " + name + " was not found");
	}

    }

    public boolean isEmpty() {
	return tasksMap.isEmpty();
    }

}
