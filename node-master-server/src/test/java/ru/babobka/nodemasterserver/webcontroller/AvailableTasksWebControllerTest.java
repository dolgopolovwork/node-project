package ru.babobka.nodemasterserver.webcontroller;

import org.junit.Before;
import org.junit.Test;
import ru.babobka.nodetask.TaskPool;
import ru.babobka.nodeutils.container.ApplicationContainer;
import ru.babobka.nodeutils.container.Container;
import ru.babobka.vsjws.enumerations.ResponseCode;
import ru.babobka.vsjws.model.http.HttpResponse;
import ru.babobka.vsjws.model.json.JSONRequest;

import java.util.HashSet;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

/**
 * Created by 123 on 08.09.2017.
 */
public class AvailableTasksWebControllerTest {

    private TaskPool taskPool;
    private AvailableTasksWebController availableTasksWebController;

    @Before
    public void setUp() {
        taskPool = mock(TaskPool.class);
        new ApplicationContainer() {
            @Override
            public void contain(Container container) {
                container.put("masterServerTaskPool", taskPool);
            }
        }.contain(Container.getInstance());
        availableTasksWebController = new AvailableTasksWebController();
    }

    @Test
    public void testGet() {
        when(taskPool.getTaskNames()).thenReturn(new HashSet<>());
        JSONRequest request = mock(JSONRequest.class);
        HttpResponse response = availableTasksWebController.onGet(request);
        assertEquals(response.getResponseCode(), ResponseCode.OK);
        verify(taskPool).getTaskNames();
    }
}
