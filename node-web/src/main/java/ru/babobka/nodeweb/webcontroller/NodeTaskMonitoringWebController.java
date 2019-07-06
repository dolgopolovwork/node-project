package ru.babobka.nodeweb.webcontroller;

import com.sun.net.httpserver.HttpExchange;
import ru.babobka.nodebusiness.monitoring.TaskMonitoringService;
import ru.babobka.nodeutils.container.Container;

import java.io.IOException;

public class NodeTaskMonitoringWebController extends WebController {

    private TaskMonitoringService taskMonitoringService = Container.getInstance().get(TaskMonitoringService.class);

    @Override
    public void onGet(HttpExchange httpExchange) throws IOException {
        sendJson(httpExchange, taskMonitoringService.getTaskMonitoringData());
    }

}
