package ru.babobka.container;

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

	private static boolean isInterfaceSuperClassOf(Class<?> clazz, Class<?> interfaze) {
		Class<?>[] interfaces = clazz.getInterfaces();
		for (Class<?> clazzInterfaces : interfaces) {
			if (clazzInterfaces.equals(interfaze)) {
				return true;
			}
		}
		return false;
	}

	@SuppressWarnings("unchecked")
	public <T> T get(Class<?> clazz) {
		Object obj = null;
		if (clazz.isInterface()) {
			for (Map.Entry<Class<?>, Object> entry : containerMap.entrySet()) {
				if (isInterfaceSuperClassOf(entry.getKey(), clazz)) {
					obj = entry.getValue();
				}
			}
		} else {
			obj = containerMap.get(clazz);
			if (obj == null) {
				for (Map.Entry<Class<?>, Object> entry : containerMap.entrySet()) {
					if (entry.getKey().getSuperclass().equals(clazz)) {
						obj = entry.getValue();
					}

				}
			}

		}
		return (T) obj;

	}

	public void clear() {
		containerMap.clear();
	}

}
