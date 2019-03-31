package ru.babobka.nodeweb.webcontroller;

import ru.babobka.nodebusiness.monitoring.TaskMonitoringService;
import ru.babobka.nodeutils.container.Container;
import ru.babobka.vsjws.model.http.HttpRequest;
import ru.babobka.vsjws.model.http.HttpResponse;
import ru.babobka.vsjws.model.http.ResponseFactory;
import ru.babobka.vsjws.webcontroller.HttpWebController;

public class NodeTaskMonitoringWebController extends HttpWebController {

    private TaskMonitoringService taskMonitoringService = Container.getInstance().get(TaskMonitoringService.class);

    @Override
    public HttpResponse onGet(HttpRequest request) {
        return ResponseFactory.json(taskMonitoringService.getTaskMonitoringData());
    }
}
