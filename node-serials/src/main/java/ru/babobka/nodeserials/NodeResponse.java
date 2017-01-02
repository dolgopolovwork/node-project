package ru.babobka.nodeserials;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class NodeResponse implements Serializable {

	private static final long serialVersionUID = 8L;

	private final String taskName;

	private final UUID taskId;

	private final UUID responseId;

	private final long timeTakes;

	private final long timeStamp;

	private final Status status;

	private final String message;

	private final Map<String, Serializable> addition = new HashMap<>();

	public NodeResponse(UUID taskId, UUID responseId, long timeTakes, Status status, String message,
			Map<String, Serializable> addition, String taskName) {
		this.taskId = taskId;
		this.responseId = responseId;
		this.timeTakes = timeTakes;
		this.status = status;
		this.message = message;
		this.taskName = taskName;
		if (addition != null) {
			this.addition.putAll(addition);
		}
		this.timeStamp = System.currentTimeMillis();
	}

	public NodeResponse(UUID taskId, long timeTakes, Status status, String message, Map<String, Serializable> addition,
			String taskName) {
		this(taskId, UUID.randomUUID(), timeTakes, status, message, addition, taskName);
	}

	public NodeResponse(UUID taskId, Status status) {
		this(taskId, UUID.randomUUID(), -1, status, null, null, null);
	}

	public NodeResponse(UUID taskId, Status status, String taskName) {
		this(taskId, UUID.randomUUID(), 0, status, null, null, taskName);
	}

	public static NodeResponse badResponse(UUID taskId) {
		return new NodeResponse(taskId, NodeResponse.Status.FAILED);
	}

	public static NodeResponse dummyResponse(UUID taskId) {
		return new NodeResponse(taskId, NodeResponse.Status.NORMAL);
	}

	public static NodeResponse stoppedResponse(UUID taskId) {
		return new NodeResponse(taskId, NodeResponse.Status.STOPPED);
	}


	public <T> T getAdditionValue(String key) {
		Serializable value = addition.get(key);
		if (value != null)
			return (T) value;
		return null;
	}

	public <T> T getAdditionValue(String key, T defaultValue) {
		Serializable value = addition.get(key);
		if (value == null) {
			return defaultValue;
		}
		return (T) value;
	}


	public enum Status {
		NORMAL, STOPPED, FAILED
	}

	public long getTimeTakes() {
		return timeTakes;
	}

	public String getMessage() {
		return message;
	}

	public UUID getTaskId() {
		return taskId;
	}

	public UUID getResponseId() {
		return responseId;
	}

	public Status getStatus() {
		return status;
	}

	public String getTaskName() {
		return taskName;
	}

	public boolean isHeartBeatingResponse() {
		if (taskName.equals(Mappings.HEART_BEAT_TASK_NAME)) {
			return true;
		}
		return false;
	}

	public boolean isAuthResponse() {
		if (taskName.equals(Mappings.AUTH_TASK_NAME)) {
			return true;
		}
		return false;
	}

	public long getTimeStamp() {
		return timeStamp;
	}

	public Map<String, Serializable> getAddition() {
		return addition;
	}

	@Override
	public String toString() {
		return "NodeResponse [taskName=" + taskName + ", taskId=" + taskId + ", responseId=" + responseId
				+ ", timeTakes=" + timeTakes + ", timeStamp=" + timeStamp + ", status=" + status + ", message="
				+ message + ", addition=" + addition + "]";
	}

	public boolean isStopped() {
		if (this.status == Status.STOPPED) {
			return true;
		}
		return false;
	}
}
