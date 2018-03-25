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
}
