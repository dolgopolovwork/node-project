package ru.babobka.nodemasterserver.webcontroller;

import ru.babobka.nodetask.TaskPool;
import ru.babobka.nodeutils.container.Container;
import ru.babobka.vsjws.model.http.HttpResponse;
import ru.babobka.vsjws.model.http.ResponseFactory;
import ru.babobka.vsjws.model.json.JSONRequest;
import ru.babobka.vsjws.webcontroller.JSONWebController;

public class AvailableTasksWebController extends JSONWebController {

    private final TaskPool taskPool = Container.getInstance().get("masterServerTaskPool");

    @Override
    public HttpResponse onGet(JSONRequest request) {
        return ResponseFactory.json(taskPool.getTaskNames());
    }

}
