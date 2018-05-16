package ru.babobka.nodeutils.container;

/**
 * Created by 123 on 09.04.2018.
 */
public interface Properties {

    static void put(Key key, int value) {
        Container.getInstance().put(key, value);
    }

    static void put(Key key, double value) {
        Container.getInstance().put(key, value);
    }

    static void put(Key key, long value) {
        Container.getInstance().put(key, value);
    }

    static void put(Key key, boolean value) {
        Container.getInstance().put(key, value);
    }

    static void put(Key key, String value) {
        Container.getInstance().put(key, value);
    }

    static int getInt(Key key) {
        return Container.getInstance().get(key);
    }

    static int getInt(Key key, int def) {
        return Container.getInstance().get(key, def);
    }

    static double getDouble(Key key) {
        return Container.getInstance().get(key);
    }

    static double getDouble(Key key, double def) {
        return Container.getInstance().get(key, def);
    }

    static long getLong(Key key) {
        return Container.getInstance().get(key);
    }

    static long getLong(Key key, long def) {
        return Container.getInstance().get(key, def);
    }

    static boolean getBool(Key key) {
        return Container.getInstance().get(key);
    }

    static boolean getBool(Key key, boolean def) {
        return Container.getInstance().get(key, def);
    }

    static String getString(Key key) {
        return Container.getInstance().get(key);
    }

    static String getString(Key key, String def) {
        return Container.getInstance().get(key, def);
    }
}
