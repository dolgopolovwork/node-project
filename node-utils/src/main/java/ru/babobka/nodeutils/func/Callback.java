package ru.babobka.nodeutils.func;

/**
 * Created by 123 on 28.03.2019.
 */
@FunctionalInterface
public interface Callback<T> {

    void callback(T item);
}
