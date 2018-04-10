package ru.babobka.nodeutils.container;

/**
 * Created by 123 on 09.04.2018.
 */
public interface Properties {

    static void put(String key, int value) {
        Container.getInstance().put(key, value);
    }

    static void put(String key, double value) {
        Container.getInstance().put(key, value);
    }

    static void put(String key, long value) {
        Container.getInstance().put(key, value);
    }

    static void put(String key, boolean value) {
        Container.getInstance().put(key, value);
    }

    static void put(String key, String value) {
        Container.getInstance().put(key, value);
    }

    static int getInt(String key) {
        return Container.getInstance().get(key);
    }

    static int getInt(String key, int def) {
        return Container.getInstance().get(key, def);
    }

    static double getDouble(String key) {
        return Container.getInstance().get(key);
    }

    static double getDouble(String key, double def) {
        return Container.getInstance().get(key, def);
    }

    static long getLong(String key) {
        return Container.getInstance().get(key);
    }

    static long getLong(String key, long def) {
        return Container.getInstance().get(key, def);
    }

    static boolean getBool(String key) {
        return Container.getInstance().get(key);
    }

    static boolean getBool(String key, boolean def) {
        return Container.getInstance().get(key, def);
    }

    static String getString(String key) {
        return Container.getInstance().get(key);
    }

    static String getString(String key, String def) {
        return Container.getInstance().get(key, def);
    }
}
