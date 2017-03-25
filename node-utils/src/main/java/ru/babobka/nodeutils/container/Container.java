package ru.babobka.nodeutils.container;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Container {

    private final Map<Class<?>, Object> containerMap = new ConcurrentHashMap<>();

    private Container() {

    }

    private static class SingletonHolder {
	public static final Container HOLDER_INSTANCE = new Container();
    }

    public static Container getInstance() {
	return SingletonHolder.HOLDER_INSTANCE;
    }

    public void put(Object object) {
	containerMap.put(object.getClass(), object);

    }

    @SuppressWarnings("unchecked")
    public <T> T get(Class<?> clazz) {

	for (Map.Entry<Class<?>, Object> entry : containerMap.entrySet()) {
	    if (clazz.isAssignableFrom(entry.getKey())) {
		return (T) entry.getValue();
	    }
	}

	throw new ContainerException("Object inheriting " + clazz + " was not found");

    }

    public void clear() {
	containerMap.clear();
    }

}
