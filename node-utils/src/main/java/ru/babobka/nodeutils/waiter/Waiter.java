package ru.babobka.nodeutils.waiter;

public interface Waiter {

    void waitUntilAble();

    void able();

    void disable();
}
