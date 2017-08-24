package ru.babobka.nodemasterserver.webcontroller;

import ru.babobka.nodemasterserver.model.ResponseStorage;
import ru.babobka.nodemasterserver.model.ResponsesMeta;
import ru.babobka.nodeutils.container.Container;
import ru.babobka.vsjws.enumerations.ResponseCode;
import ru.babobka.vsjws.model.JSONRequest;
import ru.babobka.vsjws.model.JSONResponse;
import ru.babobka.vsjws.webcontroller.JSONWebController;

import java.io.Serializable;
import java.util.UUID;

public class TasksInfoWebController extends JSONWebController {

    private final ResponseStorage responseStorage = Container.getInstance().get(ResponseStorage.class);

    @Override
    public JSONResponse onGet(JSONRequest request) {
        String taskIdString = request.getUrlParam("taskId");
        if (!taskIdString.isEmpty()) {
            UUID taskId = UUID.fromString(taskIdString);
            ResponsesMeta task = responseStorage.getTaskMeta(taskId);
            if (task != null) {
                return JSONResponse.ok(task);
            } else {
                return JSONResponse.code(ResponseCode.NOT_FOUND);
            }
        } else {
            return JSONResponse.ok((Serializable) responseStorage.getRunningTasksMetaMap());
        }
    }

}
