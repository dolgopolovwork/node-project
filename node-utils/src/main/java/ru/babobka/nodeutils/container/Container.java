package ru.babobka.nodeutils.container;

import java.util.HashMap;
import java.util.Map;

public class Container {

    private final Map<Class<?>, Object> containerMap = new HashMap<>();

    private final Map<String, Object> namedContainerMap = new HashMap<>();

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

    public synchronized void put(ApplicationContainer applicationContainer) {
        if (applicationContainer == null) {
            throw new IllegalArgumentException("applicationContainer is null");
        }
        applicationContainer.contain(this);
    }

    public synchronized void put(String key, Object object) {
        if (key == null) {
            throw new IllegalArgumentException("key is null");
        } else if (object == null) {
            throw new IllegalArgumentException("object to put is null");
        }
        namedContainerMap.put(key, object);
    }


    @SuppressWarnings("unchecked")
    private <T> T getNoException(Class<T> clazz) {
        for (Map.Entry<Class<?>, Object> entry : containerMap.entrySet()) {
            if (clazz.isAssignableFrom(entry.getKey())) {
                return (T) entry.getValue();
            }
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    public synchronized <T> T get(String key) {
        T object = (T) namedContainerMap.get(key);
        if (object == null) {
            throw new ContainerException("Object named " + key + " was not found");
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