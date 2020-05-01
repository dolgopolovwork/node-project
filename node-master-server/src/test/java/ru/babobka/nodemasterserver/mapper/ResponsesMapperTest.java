package ru.babobka.nodemasterserver.mapper;

import org.junit.Test;
import ru.babobka.nodemasterserver.model.Responses;
import ru.babobka.nodeserials.data.Data;
import ru.babobka.nodetask.exception.ReducingException;
import ru.babobka.nodetask.model.Reducer;
import ru.babobka.nodetask.model.SubTask;
import ru.babobka.nodetask.service.TaskExecutionResult;
import ru.babobka.nodeutils.time.Timer;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;

import static org.junit.Assert.*;
import static org.mockito.Matchers.anyList;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by 123 on 27.05.2018.
 */
public class ResponsesMapperTest {
    private ResponsesMapper responsesMapper = new ResponsesMapper();

    @Test(expected = NullPointerException.class)
    public void testMapNullResponses() throws TimeoutException, ReducingException {
        responsesMapper.map(null, mock(Timer.class), mock(SubTask.class));
    }

    @Test(expected = NullPointerException.class)
    public void testMapNullTimer() throws TimeoutException, ReducingException {
        responsesMapper.map(mock(Responses.class), null, mock(SubTask.class));
    }

    @Test(expected = NullPointerException.class)
    public void testMapNullSubTask() throws TimeoutException, ReducingException {
        responsesMapper.map(mock(Responses.class), mock(Timer.class), null);
    }


    @Test
    public void testMapStopped() throws TimeoutException, ReducingException {
        Responses responses = mock(Responses.class);
        when(responses.isStopped()).thenReturn(true);
        assertTrue(responsesMapper.map(responses, mock(Timer.class), mock(SubTask.class)).wasStopped());
    }

    @Test
    public void testMap() throws ReducingException, TimeoutException {
        Timer timer = new Timer("test timer");
        Reducer reducer = mock(Reducer.class);
        Map<String, Serializable> data = new HashMap<>();
        Data reducingResult = new Data().put("key", "value");
        when(reducer.reduce(anyList())).thenReturn(reducingResult);
        Responses responses = mock(Responses.class);
        when(responses.isStopped()).thenReturn(false);
        when(responses.getResponseList()).thenReturn(new ArrayList<>());
        SubTask subTask = mock(SubTask.class);
        when(subTask.getReducer()).thenReturn(reducer);
        TaskExecutionResult taskExecutionResult = responsesMapper.map(responses, timer, subTask);
        assertFalse(taskExecutionResult.wasStopped());
        assertNotNull(taskExecutionResult.getData().get("key"));
    }
}
