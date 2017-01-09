package ru.babobka.nodemasterserver.webcontroller;

import ru.babobka.nodeutils.container.Container;
import ru.babobka.nodemasterserver.task.TaskPool;
import ru.babobka.vsjws.model.HttpRequest;
import ru.babobka.vsjws.model.HttpResponse;
import ru.babobka.vsjws.webcontroller.WebController;


public class AvailableTasksWebController extends WebController {

	private TaskPool taskPool = Container.getInstance().get(TaskPool.class);

	@Override
	public HttpResponse onGet(HttpRequest request) {
		return HttpResponse.jsonResponse(taskPool.getTasksMap().keySet());
	}

}
