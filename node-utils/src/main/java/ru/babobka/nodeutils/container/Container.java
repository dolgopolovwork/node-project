package ru.babobka.nodeutils.container;

import lombok.NonNull;

import java.io.Closeable;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;

public class Container {

    private final Map<Class<?>, Object> containerMap = new LinkedHashMap<>();

    private final Map<String, Object> namedContainerMap = new LinkedHashMap<>();

    private Container() {
    }

    public static Container getInstance() {
        return SingletonHolder.HOLDER_INSTANCE;
    }

    public synchronized void put(@NonNull Object object) {
        containerMap.put(object.getClass(), object);
    }

    public synchronized boolean putIfAbsent(@NonNull Object object) {
        if (getNoException(object.getClass()) == null) {
            put(object);
            return true;
        }
        return false;
    }

    synchronized boolean isEmpty() {
        return containerMap.isEmpty() && namedContainerMap.isEmpty();
    }

    public synchronized void put(@NonNull AbstractApplicationContainer abstractApplicationContainer) {
        abstractApplicationContainer.contain(this);
    }

    public synchronized void put(@NonNull LambdaApplicationContainer lambdaApplicationContainer) {
        try {
            lambdaApplicationContainer.contain(this);
        } catch (Exception e) {
            throw new ContainerException(e);
        }
    }

    public synchronized void put(@NonNull Key key, @NonNull Object object) {
        namedContainerMap.put(key.name(), object);
    }

    @SuppressWarnings("unchecked")
    private <T> T getNoException(Class<T> clazz) {
        T component = null;
        for (Map.Entry<Class<?>, Object> entry : containerMap.entrySet()) {
            if (clazz.isAssignableFrom(entry.getKey())) {
                if (component != null) {
                    throw new IllegalStateException("container has at least two objects of the class " + clazz.getSimpleName() + ". try use get(Key key)");
                }
                component = (T) entry.getValue();
            }
        }
        return component;
    }

    @SuppressWarnings("unchecked")
    public synchronized <T> T get(Key key) {
        T object = (T) namedContainerMap.get(key.name());
        if (object == null) {
            throw new ContainerException("Object named '" + key + "' was not found");
        }
        return object;
    }

    @SuppressWarnings("unchecked")
    public synchronized <T> T get(@NonNull Key key, @NonNull T defaultValue) {
        T object = (T) namedContainerMap.get(key.name());
        if (object == null) {
            return defaultValue;
        }
        return object;
    }

    public synchronized <T> T get(@NonNull Class<T> clazz) {
        T object = getNoException(clazz);
        if (object == null) {
            throw new ContainerException("Object inheriting '" + clazz + "' was not found");
        }
        return object;
    }

    public synchronized <T> T get(@NonNull Class<T> clazz, T defaultValue) {
        T object = getNoException(clazz);
        if (object == null) {
            return defaultValue;
        }
        return object;
    }

    public synchronized void clear() {
        killObjectsSilently(namedContainerMap.values());
        killObjectsSilently(containerMap.values());
        namedContainerMap.clear();
        containerMap.clear();
    }

    private void killObjectsSilently(Collection<Object> objects) {
        objects.forEach(object -> {
            if (Closeable.class.isAssignableFrom(object.getClass())) {
                try {
                    ((Closeable) object).close();
                } catch (Exception ignored) {

                }
            } else if (Thread.class.isAssignableFrom(object.getClass())) {
                try {
                    ((Thread) object).interrupt();
                } catch (Exception ignored) {

                }
            } else if (ExecutorService.class.isAssignableFrom(object.getClass())) {
                try {
                    ((ExecutorService) object).shutdownNow();
                } catch (Exception ignored) {

                }
            }
        });
    }

    private static class SingletonHolder {
        static {
            Runtime.getRuntime().addShutdownHook(new Thread() {
                @Override
                public void run() {
                    HOLDER_INSTANCE.clear();
                }
            });
        }
        static final Container HOLDER_INSTANCE = new Container();
    }
}
