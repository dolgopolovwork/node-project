package ru.babobka.nodemasterserver.webcontroller;

import ru.babobka.nodeutils.container.Container;
import ru.babobka.nodemasterserver.task.TaskPool;
import ru.babobka.vsjws.model.JSONRequest;
import ru.babobka.vsjws.model.JSONResponse;
import ru.babobka.vsjws.webcontroller.JSONWebController;

public class AvailableTasksWebController extends JSONWebController {

    private TaskPool taskPool = Container.getInstance().get(TaskPool.class);

    @Override
    public JSONResponse onGet(JSONRequest request) {
	return JSONResponse.ok(taskPool.getTasksMap().keySet());
    }

}
