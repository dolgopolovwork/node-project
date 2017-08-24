package ru.babobka.nodemasterserver.webcontroller;

import ru.babobka.nodetask.TaskPool;
import ru.babobka.nodeutils.container.Container;
import ru.babobka.vsjws.model.JSONRequest;
import ru.babobka.vsjws.model.JSONResponse;
import ru.babobka.vsjws.webcontroller.JSONWebController;

import java.io.Serializable;

public class AvailableTasksWebController extends JSONWebController {

    private final TaskPool taskPool = Container.getInstance().get("masterServerTaskPool");

    @Override
    public JSONResponse onGet(JSONRequest request) {
        return JSONResponse.ok((Serializable) taskPool.getTaskNames());
    }

}
