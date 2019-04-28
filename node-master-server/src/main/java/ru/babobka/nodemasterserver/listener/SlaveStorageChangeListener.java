package ru.babobka.nodemasterserver.listener;

@FunctionalInterface
public interface SlaveStorageChangeListener {

    void onChange(SlaveStorageChangeType changeType, int currentSize);

    enum SlaveStorageChangeType {
        ADD, REMOVE, CLEAR
    }
}
