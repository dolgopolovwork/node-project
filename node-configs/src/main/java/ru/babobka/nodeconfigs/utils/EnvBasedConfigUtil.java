package ru.babobka.nodeconfigs.utils;

import lombok.NonNull;
import ru.babobka.nodeconfigs.NodeConfiguration;
import ru.babobka.nodeconfigs.exception.EnvConfigCreationException;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.Locale;
import java.util.Map;

public class EnvBasedConfigUtil {

    private EnvBasedConfigUtil() {

    }

    public static <T extends NodeConfiguration> T buildFromEnv(@NonNull T configuration) throws EnvConfigCreationException {
        return buildFromEnv(configuration, System.getenv());
    }

    static <T extends NodeConfiguration> T buildFromEnv(
            T configuration,
            @NonNull Map<String, String> env) throws EnvConfigCreationException {
        try {
            NodeConfiguration copy = configuration.copy();
            if (env.isEmpty() || !hasDesiredEnvVariables(configuration.getClass().getSimpleName().toUpperCase(Locale.getDefault()), env)) {
                return (T) copy;
            }
            buildFromEnv(configuration.getClass().getSimpleName(), copy, env);
            return (T) copy;
        } catch (Exception e) {
            throw new EnvConfigCreationException("Cannot build config using env variables", e);
        }
    }

    static boolean hasDesiredEnvVariables(@NonNull String prefix, Map<String, String> env) {
        if (prefix.isEmpty()) {
            return false;
        }
        for (String key : env.keySet()) {
            if (key.startsWith(prefix)) {
                return true;
            }
        }
        return false;
    }

    private static boolean buildFromEnv(
            String prefix,
            Object object,
            Map<String, String> env) throws InvocationTargetException, IllegalAccessException, InstantiationException {
        boolean valueWasSet = false;
        Method[] methods = object.getClass().getDeclaredMethods();
        Field[] fields = object.getClass().getDeclaredFields();
        for (Field field : fields) {
            if (!Modifier.isStatic(field.getModifiers())) {
                Method setter = getSetter(field, methods);
                String envVar = prefix.toUpperCase(Locale.getDefault()) + "_" + field.getName().toUpperCase(Locale.getDefault());
                String envValue = env.get(envVar);
                if (field.getType().isPrimitive() || field.getType().equals(String.class)) {
                    if (envValue == null) {
                        continue;
                    }
                    if (field.getType().equals(String.class)) {
                        setter.invoke(object, envValue);
                        valueWasSet = true;
                    } else if (field.getType().equals(Integer.TYPE)) {
                        setter.invoke(object, Integer.valueOf(envValue));
                        valueWasSet = true;
                    } else if (field.getType().equals(Long.TYPE)) {
                        setter.invoke(object, Long.valueOf(envValue));
                        valueWasSet = true;
                    } else if (field.getType().equals(Boolean.TYPE)) {
                        setter.invoke(object, Boolean.valueOf(envValue));
                        valueWasSet = true;
                    } else if (field.getType().equals(Byte.TYPE)) {
                        setter.invoke(object, Byte.valueOf(envValue));
                        valueWasSet = true;
                    } else if (field.getType().equals(Short.TYPE)) {
                        setter.invoke(object, Short.valueOf(envValue));
                        valueWasSet = true;
                    } else if (field.getType().equals(Double.TYPE)) {
                        setter.invoke(object, Double.valueOf(envValue));
                        valueWasSet = true;
                    } else if (field.getType().equals(Float.TYPE)) {
                        setter.invoke(object, Float.valueOf(envValue));
                        valueWasSet = true;
                    }
                } else {
                    validateField(field.getType(), field.getName());
                    Method getter = getGetter(field, methods);
                    Object originalValue = getter.invoke(object);
                    Object mutatedObject = originalValue;
                    if (originalValue == null) {
                        mutatedObject = getter.getReturnType().newInstance();
                    }
                    if (buildFromEnv(envVar, mutatedObject, env)) {
                        setter.invoke(object, mutatedObject);
                        valueWasSet = true;
                    }
                }

            }
        }
        return valueWasSet;
    }

    static void validateField(@NonNull Class fieldClass, @NonNull String fieldName) {
        //TODO add generic check
        if (!hasDefaultConstructor(fieldClass)) {
            throw new IllegalArgumentException("Cannot set '" + fieldName + "' because it doesn't have a default public constructor");
        } else if (Modifier.isAbstract(fieldClass.getModifiers())) {
            throw new IllegalArgumentException("Cannot set '" + fieldName + "' because it's an abstract class");
        } else if (fieldClass.isInterface()) {
            throw new IllegalArgumentException("Cannot set '" + fieldName + "' because it's an interface");
        } else if (fieldClass.isEnum()) {
            throw new IllegalArgumentException("Cannot set '" + fieldName + "' because it's an enumeration");
        } else if (fieldClass.isArray()) {
            throw new IllegalArgumentException("Cannot set '" + fieldName + "' because it's an array");
        } else if (Collection.class.isAssignableFrom(fieldClass) || Map.class.isAssignableFrom(fieldClass)) {
            throw new IllegalArgumentException("Cannot set '" + fieldName + "' because it's a collection");
        }
    }

    static boolean hasDefaultConstructor(@NonNull Class<?> clazz) throws SecurityException {
        Class<?>[] empty = {};
        try {
            return Modifier.isPublic(clazz.getConstructor(empty).getModifiers());
        } catch (NoSuchMethodException e) {
            return false;
        }
    }

    static Method getSetter(@NonNull Field field, @NonNull Method[] methods) {
        String fieldName = field.getName();
        String setterName = "set" + fieldName.replaceFirst(
                String.valueOf(fieldName.charAt(0)),
                String.valueOf(fieldName.charAt(0)).toUpperCase(Locale.getDefault()));
        for (Method method : methods) {
            if (method.getName().equals(setterName)) {
                return method;
            }
        }
        throw new IllegalArgumentException("No setter for field " + fieldName);
    }

    static Method getGetter(@NonNull Field field, @NonNull Method[] methods) {
        String fieldName = field.getName();
        String getterName = "get" + fieldName.replaceFirst(
                String.valueOf(fieldName.charAt(0)),
                String.valueOf(fieldName.charAt(0)).toUpperCase(Locale.getDefault()));
        for (Method method : methods) {
            if (method.getName().equals(getterName)) {
                return method;
            }
        }
        throw new IllegalArgumentException("No getter for field " + fieldName);
    }
}
