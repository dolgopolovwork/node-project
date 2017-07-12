package ru.babobka.nodeserials;

public enum SystemTaskName {

    AUTH_TASK_NAME("auth"),

    HEART_BEAT_TASK_NAME("heartBeat");

    private final String name;

    SystemTaskName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
