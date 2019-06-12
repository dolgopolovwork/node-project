package ru.babobka.nodemasterserver.mapper;

import lombok.NonNull;
import ru.babobka.nodeserials.NodeRequest;
import ru.babobka.nodeserials.NodeResponse;
import ru.babobka.nodeserials.enumerations.ResponseStatus;
import ru.babobka.nodetask.exception.TaskExecutionException;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

// This code is duplicated
public class NodeResponseErrorMapper {

    public NodeResponse createErrorResponse(@NonNull NodeRequest request,
                                            @NonNull TaskExecutionException taskExecutionException) {
        String message = taskExecutionException.getMessage();
        if (taskExecutionException.getExecutionStatus() == ResponseStatus.SYSTEM_ERROR) {
            return NodeResponse.systemError(request, message);
        } else if (taskExecutionException.getExecutionStatus() == ResponseStatus.VALIDATION_ERROR) {
            return NodeResponse.validationError(request, message);
        } else if (taskExecutionException.getExecutionStatus() == ResponseStatus.NO_NODES) {
            return NodeResponse.noNodesError(request, message);
        }
        throw new NotImplementedException();
    }

}
