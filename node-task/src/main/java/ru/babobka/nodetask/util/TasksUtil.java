package ru.babobka.nodetask.util;

import ru.babobka.nodetask.model.TaskFactory;

import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * Created by 123 on 19.08.2017.
 */
public class TasksUtil {

    public List<TaskFactory> getFactories(String jarFilePath) throws IOException {
        List<TaskFactory> taskFactories = new LinkedList<>();
        try (JarFile jarFile = new JarFile(jarFilePath);
             URLClassLoader cl = URLClassLoader
                     .newInstance(new URL[]{new URL("jar:file:" + jarFilePath + "!/")})) {
            Enumeration<JarEntry> e = jarFile.entries();
            while (e.hasMoreElements()) {
                JarEntry je = e.nextElement();
                if (je.isDirectory() || !je.getName().endsWith(".class")) {
                    continue;
                }
                // -6 because of .class
                String className = je.getName().substring(0, je.getName().length() - 6);
                className = className.replace('/', '.');
                Class<?> c = cl.loadClass(className);
                if (TaskFactory.class.isAssignableFrom(c) && !TaskFactory.class.equals(c)) {
                    taskFactories.add((TaskFactory) c.newInstance());
                }
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
            throw new IOException(e);
        }
        return taskFactories;
    }
}
