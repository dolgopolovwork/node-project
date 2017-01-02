package ru.babobka.primecounter.util;

import org.json.JSONObject;

public class ResourceUtil {

	public static JSONObject getTaskConfig123() {
		return new JSONObject(
				StreamUtil.readFile(ResourceUtil.class.getClassLoader().getResourceAsStream("task.json")));
	}
	

}
