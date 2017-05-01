package ru.babobka.nodemasterserver.webcontroller;

import ru.babobka.nodeutils.container.Container;
import ru.babobka.nodemasterserver.task.TaskPool;
import ru.babobka.vsjws.webcontroller.JSONWebController;
import ru.babobka.vsjws.webserver.JSONRequest;
import ru.babobka.vsjws.webserver.JSONResponse;

import java.io.Serializable;

public class AvailableTasksWebController extends JSONWebController {

    private TaskPool taskPool = Container.getInstance().get(TaskPool.class);

    @Override
    public JSONResponse onGet(JSONRequest request) {
        return JSONResponse.ok((Serializable) taskPool.getTasksMap().keySet());
    }

}
