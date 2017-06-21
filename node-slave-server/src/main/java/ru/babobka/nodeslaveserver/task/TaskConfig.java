package ru.babobka.nodeslaveserver.task;

import ru.babobka.subtask.model.SubTask;

public class TaskConfig {

    private final String name;

    private final String description;

    private final boolean raceStyle;

    public TaskConfig(String name, String description, boolean raceStyle) {
        this.name = name;
        this.description = description;
        this.raceStyle = raceStyle;
    }

    public TaskConfig(SubTask subTask) {
        this(subTask.getName(), subTask.getDescription(), subTask.isRaceStyle());
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public boolean isRaceStyle() {
        return raceStyle;
    }

    public TaskConfig newInstance() {
        return new TaskConfig(name, description, raceStyle);
    }

}
