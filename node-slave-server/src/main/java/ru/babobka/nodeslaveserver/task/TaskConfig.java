package ru.babobka.nodeslaveserver.task;

import org.json.JSONObject;

import ru.babobka.subtask.model.SubTask;

public class TaskConfig {

	private final String name;

	private final String description;

	private final boolean raceStyle;

	public TaskConfig(String name, String description, boolean raceStyle) {
		super();
		this.name = name;
		this.description = description;
		this.raceStyle = raceStyle;
	}

	public TaskConfig(SubTask subTask) {
		this(subTask.getName(), subTask.getDescription(), subTask.isRaceStyle());
	}

	public TaskConfig(JSONObject jsonObject) {
		this(jsonObject.getString("name"), jsonObject.getString("description"), jsonObject.getBoolean("raceStyle"));
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
