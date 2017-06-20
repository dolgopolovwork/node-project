package ru.babobka.nodemasterserver.webcontroller;

import java.util.UUID;

import ru.babobka.nodeutils.container.Container;
import ru.babobka.nodemasterserver.service.TaskService;
import ru.babobka.vsjws.enumerations.ResponseCode;
import ru.babobka.vsjws.webserver.HttpRequest;
import ru.babobka.vsjws.webserver.HttpResponse;
import ru.babobka.vsjws.webcontroller.HttpWebController;


public class CancelTaskWebController extends HttpWebController {

    private final TaskService taskService = Container.getInstance().get(TaskService.class);

    private static final String UUID_REGULAR = "[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}";

    @Override
    public HttpResponse onDelete(HttpRequest request) {
	String taskIdParam = request.getUrlParam("taskId");
	if (!taskIdParam.matches(UUID_REGULAR)) {
	    return HttpResponse.text("Invalid 'taskId'", ResponseCode.BAD_REQUEST);
	}
	UUID taskId = UUID.fromString(taskIdParam);
	return HttpResponse.json(taskService.cancelTask(taskId));
    }

}
