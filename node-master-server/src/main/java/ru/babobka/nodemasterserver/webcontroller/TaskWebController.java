package ru.babobka.nodemasterserver.webcontroller;

import java.io.IOException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeoutException;

import ru.babobka.nodeutils.container.Container;
import ru.babobka.nodemasterserver.service.TaskService;
import ru.babobka.nodemasterserver.task.TaskContext;
import ru.babobka.nodemasterserver.task.TaskPool;
import ru.babobka.nodeutils.util.TextUtil;
import ru.babobka.vsjws.model.HttpRequest;
import ru.babobka.vsjws.model.HttpResponse;
import ru.babobka.vsjws.model.Param;
import ru.babobka.vsjws.webcontroller.HttpWebController;

public class TaskWebController extends HttpWebController {

    private TaskService taskService = Container.getInstance().get(TaskService.class);

    private TaskPool taskPool = Container.getInstance().get(TaskPool.class);

    @Override
    public HttpResponse onGet(HttpRequest request) throws IOException, TimeoutException {
        String taskName = request.getUri().replaceFirst("/", "");
        taskName = taskName.substring(taskName.indexOf('/') + 1, taskName.indexOf('?'));
        TaskContext taskContext = taskPool.get(URLDecoder.decode(taskName, "UTF-8"));
        int maxNodes = TextUtil.tryParseInt("maxNodes", -1);
        return HttpResponse
                .jsonResponse(taskService.getResult(paramListToMap(request.getUrlParams()), taskContext, maxNodes));
    }

    @Override
    public HttpResponse onHead(HttpRequest request) {
        return HttpResponse.ok();
    }

    private static Map<String, String> paramListToMap(List<Param> params) {
        Map<String, String> paramMaps = new HashMap<>();
        for (Param param : params) {
            paramMaps.put(param.getKey(), param.getValue());
        }
        return paramMaps;
    }
}
