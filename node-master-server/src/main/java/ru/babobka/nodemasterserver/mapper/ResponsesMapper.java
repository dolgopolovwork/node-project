package ru.babobka.nodemasterserver.mapper;

import ru.babobka.nodemasterserver.model.Responses;
import ru.babobka.nodemasterserver.task.TaskExecutionResult;
import ru.babobka.nodeserials.NodeResponse;
import ru.babobka.nodetask.exception.ReducingException;
import ru.babobka.nodetask.model.SubTask;
import ru.babobka.nodeutils.time.Timer;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeoutException;

/**
 * Created by 123 on 01.04.2018.
 */
public class ResponsesMapper {
    public TaskExecutionResult map(Responses responses, Timer timer, SubTask task) throws TimeoutException, ReducingException {
        List<NodeResponse> responseList = responses.getResponseList();
        if (responses.isStopped()) {
            return TaskExecutionResult.stopped();
        }
        Map<String, Serializable> resultMap = task.getReducer().reduce(responseList).map();
        return TaskExecutionResult.normal(timer, resultMap);
    }
}
