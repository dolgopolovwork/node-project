package ru.babobka.nodeutils.util;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import static java.security.AccessController.doPrivileged;

/**
 * Created by 123 on 25.03.2018.
 */
public class ClassLoaderUtil {

    private ClassLoaderUtil() {
    }

    public static void addPath(String s) throws IOException {
        try {
            File f = new File(s);
            URI u = f.toURI();
            URLClassLoader urlClassLoader = (URLClassLoader) ClassLoader.getSystemClassLoader();
            Class<URLClassLoader> urlClass = URLClassLoader.class;
            Method method = urlClass.getDeclaredMethod("addURL", new Class[]{URL.class});
            doPrivileged((PrivilegedAction) () -> {
                method.setAccessible(true);
                return null;
            });

            method.invoke(urlClassLoader, new Object[]{u.toURL()});
        } catch (NoSuchMethodException | MalformedURLException | InvocationTargetException | IllegalAccessException e) {
            throw new IOException(e);
        }
    }

    public static List<Object> getObjectsFromJar(String jarFilePath, Class<?> clazz) throws IOException {
        List<Object> objects = new ArrayList<>();
        try (JarFile jarFile = new JarFile(jarFilePath);
             URLClassLoader cl = URLClassLoader
                     .newInstance(new URL[]{new URL("jar:file:" + jarFilePath + "!/")})) {
            Enumeration<JarEntry> e = jarFile.entries();
            while (e.hasMoreElements()) {
                JarEntry je = e.nextElement();
                if (je.isDirectory() || !je.getName().endsWith(".class")) {
                    continue;
                }
                String className = je.getName().substring(0, je.getName().length() - 6);
                className = className.replace('/', '.');
                try {
                    Class<?> c = cl.loadClass(className);
                    if (clazz.isAssignableFrom(c) && !clazz.equals(c)) {
                        objects.add(c.newInstance());
                    }
                } catch (NoClassDefFoundError ignored) {
                }
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
            throw new IOException(e);
        }
        return objects;
    }
}
