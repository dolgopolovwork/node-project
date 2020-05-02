package ru.babobka.nodeweb.webcontroller;

import io.javalin.http.Context;
import io.javalin.plugin.openapi.annotations.HttpMethod;
import io.javalin.plugin.openapi.annotations.OpenApi;
import io.javalin.plugin.openapi.annotations.OpenApiContent;
import io.javalin.plugin.openapi.annotations.OpenApiResponse;
import ru.babobka.nodebusiness.monitoring.TaskMonitoringData;
import ru.babobka.nodebusiness.monitoring.TaskMonitoringService;
import ru.babobka.nodebusiness.service.NodeMasterInfoService;
import ru.babobka.nodetask.TaskPoolReader;
import ru.babobka.nodeutils.container.Container;
import ru.babobka.nodeutils.key.MasterServerKey;
import ru.babobka.nodeweb.dto.ConnectedSlaveDTO;
import ru.babobka.nodeweb.mapper.ConnectedSlaveToDTOMapper;

import java.util.stream.Collectors;

public class NodeMonitoringWebController {

    private final ConnectedSlaveToDTOMapper connectedSlaveToDTOMapper = Container.getInstance().get(ConnectedSlaveToDTOMapper.class);
    private final TaskMonitoringService taskMonitoringService =
            Container.getInstance().get(TaskMonitoringService.class);
    private final NodeMasterInfoService nodeMasterInfoService =
            Container.getInstance().get(NodeMasterInfoService.class);
    private final TaskPoolReader taskPoolReader =
            Container.getInstance().get(MasterServerKey.MASTER_SERVER_TASK_POOL);

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
            path = "/monitoring/slaves",
            summary = "Get brief information about connected slaves",
            operationId = "getSlaves",
            tags = {"Monitoring"},
            responses = {@OpenApiResponse(status = "200", content = {@OpenApiContent(from = ConnectedSlaveDTO[].class)})}
    )
    public void getSlaves(Context context) {
        context.json(nodeMasterInfoService.getConnectedSlaves()
                .stream()
                .map(connectedSlaveToDTOMapper::map)
                .collect(Collectors.toList()));
    }

    @OpenApi(
            method = HttpMethod.GET,
            path = "/monitoring/startTime",
            summary = "Get current master's start time in milliseconds(UTC)",
            operationId = "getStartTime",
            tags = {"Monitoring"},
            responses = {@OpenApiResponse(status = "200", content = {@OpenApiContent(from = Integer.class)})}
    )
    public void getStartTime(Context context) {
        context.result(Long.toString(nodeMasterInfoService.getMasterStartTime()));
    }

    @OpenApi(
            method = HttpMethod.GET,
            path = "/monitoring/taskNames",
            summary = "Get current master's registered tasks",
            operationId = "getTaskNames",
            tags = {"Monitoring"},
            responses = {@OpenApiResponse(status = "200", content = {@OpenApiContent(from = String[].class)})}
    )
    public void getTaskNames(Context context) {
        context.json(taskPoolReader.getTaskNames());
    }

    @OpenApi(
            method = HttpMethod.GET,
            path = "/monitoring/tasksStats",
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
