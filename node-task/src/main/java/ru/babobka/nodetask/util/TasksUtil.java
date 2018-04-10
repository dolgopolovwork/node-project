package ru.babobka.nodetask.util;

import ru.babobka.nodetask.model.TaskFactory;
import ru.babobka.nodeutils.util.ClassLoaderUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by 123 on 19.08.2017.
 */
public class TasksUtil {

    public List<TaskFactory> getFactories(String jarFilePath) throws IOException {
        List<Object> objectsFromJar = ClassLoaderUtil.getObjectsFromJar(jarFilePath, TaskFactory.class);
        List<TaskFactory> taskFactories = new ArrayList<>(objectsFromJar.size());
        for (Object objectFromJar : objectsFromJar) {
            taskFactories.add((TaskFactory) objectFromJar);
        }
        return taskFactories;
    }
}
