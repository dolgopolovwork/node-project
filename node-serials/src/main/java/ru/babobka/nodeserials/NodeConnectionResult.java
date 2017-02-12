package ru.babobka.nodeserials;

public class NodeConnectionResult {

    public enum Result {
	OK, CLUSTER_FULL, AUTH_FAIL
    };

    private final Result result;

    private final long time;

    public NodeConnectionResult(Result result) {
	this.result = result;
	this.time = System.currentTimeMillis();

    }

    public Result getResult() {
	return result;
    }

    public long getTime() {
	return time;
    }

}
