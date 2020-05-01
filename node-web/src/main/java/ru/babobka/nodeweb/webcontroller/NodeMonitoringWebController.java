package ru.babobka.nodeweb.webcontroller;

import io.javalin.http.Context;
import io.javalin.plugin.openapi.annotations.HttpMethod;
import io.javalin.plugin.openapi.annotations.OpenApi;
import io.javalin.plugin.openapi.annotations.OpenApiContent;
import io.javalin.plugin.openapi.annotations.OpenApiResponse;
import ru.babobka.nodebusiness.monitoring.TaskMonitoringData;
import ru.babobka.nodebusiness.monitoring.TaskMonitoringService;
import ru.babobka.nodebusiness.service.NodeMasterInfoService;
import ru.babobka.nodeutils.container.Container;

public class NodeMonitoringWebController {

    private final TaskMonitoringService taskMonitoringService =
            Container.getInstance().get(TaskMonitoringService.class);
    private final NodeMasterInfoService nodeMasterInfoService =
            Container.getInstance().get(NodeMasterInfoService.class);

    @OpenApi(
            method = HttpMethod.GET,
            path = "/monitoring/clustersize",
            summary = "Get the size of the cluster (how many slave nodes are connected)",
            operationId = "getClusterSize",
            tags = {"Monitoring"},
            responses = {@OpenApiResponse(status = "200", content = {@OpenApiContent(from = Integer.class)})}
    )
    public void getClusterSize(Context context) {
        context.json(nodeMasterInfoService.totalNodes());
    }

    @OpenApi(
            method = HttpMethod.GET,
            path = "/monitoring/tasks",
            summary = "Get brief information about executed tasks",
            operationId = "getTasksMonitoring",
            tags = {"Monitoring"},
            responses = {@OpenApiResponse(status = "200", content = {@OpenApiContent(from = TaskMonitoringData.class)})}
    )
    public void getTasksMonitoringData(Context context) {
        context.json(taskMonitoringService.getTaskMonitoringData());
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
