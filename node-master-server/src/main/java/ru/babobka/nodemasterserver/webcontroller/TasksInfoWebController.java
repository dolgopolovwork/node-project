package ru.babobka.nodemasterserver.webcontroller;

import ru.babobka.nodemasterserver.model.ResponseStorage;
import ru.babobka.nodemasterserver.model.ResponsesMeta;
import ru.babobka.nodeutils.container.Container;
import ru.babobka.vsjws.enumerations.ResponseCode;
import ru.babobka.vsjws.model.http.HttpResponse;
import ru.babobka.vsjws.model.http.ResponseFactory;
import ru.babobka.vsjws.model.json.JSONRequest;
import ru.babobka.vsjws.webcontroller.JSONWebController;

import java.util.UUID;

public class TasksInfoWebController extends JSONWebController {

    private final ResponseStorage responseStorage = Container.getInstance().get(ResponseStorage.class);

    @Override
    public HttpResponse onGet(JSONRequest request) {
        String taskIdString = request.getUrlParam("taskId");
        if (!taskIdString.isEmpty()) {
            UUID taskId = UUID.fromString(taskIdString);
            ResponsesMeta task = responseStorage.getTaskMeta(taskId);
            if (task != null) {
                return ResponseFactory.json(task);
            } else {
                return ResponseFactory.code(ResponseCode.NOT_FOUND);
            }
        } else {
            return ResponseFactory.json(responseStorage.getRunningTasksMetaMap());
        }
    }

}
