package ru.babobka.nodemasterserver.webcontroller;

import java.util.UUID;

import org.json.JSONException;

import ru.babobka.nodeutils.container.Container;
import ru.babobka.nodemasterserver.model.ResponseStorage;
import ru.babobka.nodemasterserver.model.ResponsesArrayMeta;

import ru.babobka.vsjws.model.HttpResponse.ResponseCode;
import ru.babobka.vsjws.model.JSONRequest;
import ru.babobka.vsjws.model.JSONResponse;
import ru.babobka.vsjws.webcontroller.JSONWebController;

public class TasksInfoWebController extends JSONWebController {

    private final ResponseStorage responseStorage = Container.getInstance().get(ResponseStorage.class);

    @Override
    public JSONResponse onGet(JSONRequest request) throws JSONException {

	String taskIdString = request.getUrlParam("taskId");
	if (!taskIdString.isEmpty()) {
	    UUID taskId = UUID.fromString(taskIdString);
	    ResponsesArrayMeta task = responseStorage.getTaskMeta(taskId);
	    if (task != null) {
		return JSONResponse.ok(task);
	    } else {
		return JSONResponse.code(ResponseCode.NOT_FOUND);
	    }

	} else {
	    return JSONResponse.ok(responseStorage.getRunningTasksMetaMap());
	}
    }

}
