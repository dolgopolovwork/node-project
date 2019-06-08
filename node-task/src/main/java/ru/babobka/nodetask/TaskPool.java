package ru.babobka.nodetask;

import org.apache.log4j.Logger;
import ru.babobka.nodetask.exception.CanNotInitTaskFactoryException;
import ru.babobka.nodetask.model.SubTask;
import ru.babobka.nodetask.model.TaskFactory;
import ru.babobka.nodetask.util.TasksUtil;
import ru.babobka.nodeutils.container.Container;
import ru.babobka.nodeutils.util.StreamUtil;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * Created by dolgopolov.a on 09.07.15.
 */
public class TaskPool {

    private static final Logger logger = Logger.getLogger(TaskPool.class);
    private final Map<String, TaskFactory> tasksMap = new HashMap<>();
    private final StreamUtil streamUtil = Container.getInstance().get(StreamUtil.class);
    private final TasksUtil tasksUtil = Container.getInstance().get(TasksUtil.class);

    public TaskPool(String tasksFolderPath) {
        try {
            logger.info("reading tasks from '" + tasksFolderPath + "'");
            File tasksFolder = new File(tasksFolderPath);
            String absoluteTasksFolderPath = tasksFolder.getAbsolutePath();
            List<String> files = streamUtil.getJarFileListFromFolder(absoluteTasksFolderPath);
            for (String file : files) {
                String jarFilePath = absoluteTasksFolderPath + File.separator + file;
                List<TaskFactory> taskFactories = tasksUtil.getFactories(jarFilePath);
                for (TaskFactory factory : taskFactories) {
                    factory.getApplicationContainer().contain(Container.getInstance());
                    tasksMap.put(factory.getTaskType().getName(), factory);
                }
            }
        } catch (IOException e) {
            throw new CanNotInitTaskFactoryException(e);
        } catch (RuntimeException e) {
            throw new CanNotInitTaskFactoryException(
                    "cannot init factory pool. try to re-download new jars to node-slave-server task folder", e);
        }
        if (tasksMap.isEmpty()) {
            throw new CanNotInitTaskFactoryException(
                    "cannot init factory pool. no task to run. try to re-download new jars to node-slave-server task folder");
        }
    }

    public Set<String> getTaskNames() {
        return new HashSet<>(tasksMap.keySet());
    }

    public boolean containsAnyOfTask(Set<String> taskNames) {
        if (taskNames == null) {
            throw new IllegalArgumentException("taskNames is null");
        }
        for (String taskName : taskNames) {
            if (tasksMap.containsKey(taskName)) {
                return true;
            }
        }
        return false;
    }

    public SubTask get(String name) throws IOException {
        TaskFactory taskFactory = tasksMap.get(name);
        if (taskFactory != null) {
            return taskFactory.createTask();
        } else {
            throw new IOException("task " + name + " was not found");
        }
    }

    boolean isEmpty() {
        return tasksMap.isEmpty();
    }
}
