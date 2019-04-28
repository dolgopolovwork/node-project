package ru.babobka.nodemasterserver.mapper;

import lombok.NonNull;
import ru.babobka.nodemasterserver.model.Responses;
import ru.babobka.nodetask.service.TaskExecutionResult;
import ru.babobka.nodeserials.NodeResponse;
import ru.babobka.nodeserials.data.Data;
import ru.babobka.nodetask.exception.ReducingException;
import ru.babobka.nodetask.model.SubTask;
import ru.babobka.nodeutils.time.Timer;

import java.util.List;
import java.util.concurrent.TimeoutException;

/**
 * Created by 123 on 01.04.2018.
 */
public class ResponsesMapper {
    public TaskExecutionResult map(@NonNull Responses responses,
                                   @NonNull Timer timer,
                                   @NonNull SubTask task) throws TimeoutException, ReducingException {
        //важно. порядок не менять
        List<NodeResponse> responseList = responses.getResponseList();
        if (responses.isStopped()) {
            return TaskExecutionResult.stopped();
        }
        Data data = task.getReducer().reduce(responseList);
        return TaskExecutionResult.normal(timer, data);
    }
}
