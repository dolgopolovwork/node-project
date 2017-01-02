package ru.babobka.nodemasterserver.model;

public class NodeConnectionResult {

	
	public enum Status{OK, CLUSTER_FULL, AUTH_FAIL};
	
	private Status status;
	
	private long time;
	
	public NodeConnectionResult(Status status)
	{
		this.status=status;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public long getTime() {
		return time;
	}

	public void setTime(long time) {
		this.time = time;
	}
	
	
	
}
