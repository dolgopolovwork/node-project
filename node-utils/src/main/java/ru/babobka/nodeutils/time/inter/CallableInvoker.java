package ru.babobka.nodeutils.time.inter;

/**
 * Created by 123 on 07.08.2018.
 */
public interface CallableInvoker<T> {
    T call() throws Exception;
}
