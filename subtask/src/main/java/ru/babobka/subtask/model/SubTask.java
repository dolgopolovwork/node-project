package ru.babobka.subtask.model;


import ru.babobka.nodeserials.NodeRequest;

/**
 * Created by dolgopolov.a on 08.12.15.
 */
public interface SubTask{

	public ExecutionResult execute(NodeRequest request);

	public void stopTask();

	public ValidationResult validateRequest(NodeRequest request);

	public boolean isRequestDataTooSmall(NodeRequest request);

	public RequestDistributor getDistributor();

	public Reducer getReducer();
	
	public SubTask newInstance();
	
	public boolean isStopped();


}
