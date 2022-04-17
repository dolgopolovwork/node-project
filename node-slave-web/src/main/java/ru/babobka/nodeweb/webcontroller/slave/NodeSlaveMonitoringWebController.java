package ru.babobka.nodeweb.webcontroller.slave;

import io.javalin.http.Context;
import io.javalin.plugin.openapi.annotations.HttpMethod;
import io.javalin.plugin.openapi.annotations.OpenApi;
import io.javalin.plugin.openapi.annotations.OpenApiContent;
import io.javalin.plugin.openapi.annotations.OpenApiResponse;
import ru.babobka.nodetask.TaskPoolReader;
import ru.babobka.nodeutils.container.Container;
import ru.babobka.nodeutils.key.SlaveServerKey;

public class NodeSlaveMonitoringWebController {

    private final TaskPoolReader taskPoolReader = Container.getInstance().get(SlaveServerKey.SLAVE_SERVER_TASK_POOL);

    @OpenApi(
            method = HttpMethod.GET,
            path = "/monitoring/taskNames",
            summary = "Get current slave's registered tasks",
            operationId = "getTaskNames",
            tags = {"Monitoring"},
            responses = {@OpenApiResponse(status = "200", content = {@OpenApiContent(from = String[].class)})}
    )
    public void getTaskNames(Context context) {
        context.json(taskPoolReader.getTaskNames());
    }

    @OpenApi(
            method = HttpMethod.GET,
            path = "/monitoring/healthcheck",
            summary = "Health check",
            operationId = "getHealthCheck",
            tags = {"Monitoring"},
            responses = {@OpenApiResponse(status = "200")}
    )
    public void healthCheck(Context context) {
        context.result("ok");
    }
}
