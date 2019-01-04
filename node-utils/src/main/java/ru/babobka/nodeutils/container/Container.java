package ru.babobka.nodeutils.container;

import java.util.LinkedHashMap;
import java.util.Map;

public class Container {

    private final Map<Class<?>, Object> containerMap = new LinkedHashMap<>();

    private final Map<String, Object> namedContainerMap = new LinkedHashMap<>();

    private Container() {
    }

    public static Container getInstance() {
        return SingletonHolder.HOLDER_INSTANCE;
    }

    public synchronized void put(Object object) {
        if (object == null) {
            throw new IllegalArgumentException("object to put in container is null");
        }
        containerMap.put(object.getClass(), object);
    }

    public synchronized boolean putIfNotExists(Object object) {
        if (object == null) {
            throw new IllegalArgumentException("object to put in container is null");
        }
        if (getNoException(object.getClass()) == null) {
            put(object);
            return true;
        }
        return false;
    }

    public synchronized void put(AbstractApplicationContainer abstractApplicationContainer) {
        if (abstractApplicationContainer == null) {
            throw new IllegalArgumentException("abstractApplicationContainer is null");
        }
        abstractApplicationContainer.contain(this);
    }

    public synchronized void put(LambdaApplicationContainer lambdaApplicationContainer) {
        if (lambdaApplicationContainer == null) {
            throw new IllegalArgumentException("lambdaApplicationContainer is null");
        }
        try {
            lambdaApplicationContainer.contain(this);
        } catch (Exception e) {
            throw new ContainerException(e);
        }
    }

    public synchronized void put(Key key, Object object) {
        if (key == null) {
            throw new IllegalArgumentException("key is null");
        } else if (object == null) {
            throw new IllegalArgumentException("object to put is null");
        }
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
            throw new ContainerException("Object named " + key + " was not found");
        }
        return object;
    }

    @SuppressWarnings("unchecked")
    public synchronized <T> T get(Key key, T defaultValue) {
        T object = (T) namedContainerMap.get(key.name());
        if (object == null) {
            return defaultValue;
        }
        return object;
    }

    public synchronized <T> T get(Class<T> clazz) {
        T object = getNoException(clazz);
        if (object == null) {
            throw new ContainerException("Object inheriting " + clazz + " was not found");
        }
        return object;
    }

    public synchronized void clear() {
        namedContainerMap.clear();
        containerMap.clear();
    }

    @Override
    public synchronized String toString() {
        return "Container{" +
                "containerMap=" + containerMap +
                ", namedContainerMap=" + namedContainerMap +
                '}';
    }

    private static class SingletonHolder {
        static final Container HOLDER_INSTANCE = new Container();
    }
}