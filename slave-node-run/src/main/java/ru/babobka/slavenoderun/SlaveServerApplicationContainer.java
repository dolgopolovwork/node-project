package ru.babobka.slavenoderun;

import io.javalin.Javalin;
import io.javalin.plugin.openapi.OpenApiOptions;
import io.javalin.plugin.openapi.OpenApiPlugin;
import io.javalin.plugin.openapi.ui.SwaggerOptions;
import io.swagger.v3.oas.models.info.Info;
import ru.babobka.nodeconfigs.slave.SlaveServerConfig;
import ru.babobka.nodesecurity.SecurityApplicationContainer;
import ru.babobka.nodesecurity.keypair.KeyDecoder;
import ru.babobka.nodesecurity.sign.DefaultDigitalSigner;
import ru.babobka.nodesecurity.sign.SignatureValidator;
import ru.babobka.nodeutils.key.SlaveServerKey;
import ru.babobka.nodeslaveserver.server.pipeline.SlavePipelineFactory;
import ru.babobka.nodeslaveserver.service.SlaveAuthService;
import ru.babobka.nodeslaveserver.task.TaskRunnerService;
import ru.babobka.nodetask.NodeTaskApplicationContainer;
import ru.babobka.nodetask.TaskPool;
import ru.babobka.nodeutils.NodeUtilsApplicationContainer;
import ru.babobka.nodeutils.container.AbstractApplicationContainer;
import ru.babobka.nodeutils.container.Container;
import ru.babobka.nodeutils.container.Properties;
import ru.babobka.nodeutils.key.UtilKey;
import ru.babobka.nodeutils.network.NodeConnectionFactory;
import ru.babobka.nodeutils.thread.ThreadPoolService;
import ru.babobka.nodeweb.webcontroller.slave.NodeSlaveMonitoringWebController;

import java.security.spec.InvalidKeySpecException;

import static io.javalin.apibuilder.ApiBuilder.*;
import static io.javalin.apibuilder.ApiBuilder.get;

/**
 * Created by 123 on 05.11.2017.
 */
public class SlaveServerApplicationContainer extends AbstractApplicationContainer {

    @Override
    protected void containImpl(Container container) throws InvalidKeySpecException {
        SlaveServerConfig config = container.get(SlaveServerConfig.class);
        container.put(new SignatureValidator());
        container.put(SlaveServerKey.SLAVE_DSA_MANAGER, new DefaultDigitalSigner(KeyDecoder.decodePrivateKey(config.getKeyPair().getPrivKey())));
        Properties.put(UtilKey.SERVICE_THREADS_NUM, Runtime.getRuntime().availableProcessors());
        container.put(new NodeUtilsApplicationContainer());
        container.put(new SecurityApplicationContainer());
        container.put(new NodeConnectionFactory());
        container.put(config);
        container.put(new NodeTaskApplicationContainer());
        container.put(new SlavePipelineFactory());
        container.put(new TaskRunnerService());
        container.put(UtilKey.SERVICE_THREAD_POOL, ThreadPoolService.createDaemonPool("service"));
        container.put(SlaveServerKey.SLAVE_SERVER_TASK_POOL, new TaskPool(config.getTasksFolder()));
        container.put(new SlaveAuthService());
        container.put(SlaveServerKey.SLAVE_WEB, createWebServer());
    }

    public static Javalin createWebServer() {
        NodeSlaveMonitoringWebController nodeSlaveMonitoringWebController = new NodeSlaveMonitoringWebController();
        return Javalin.create(conf -> {
            conf.registerPlugin(getConfiguredOpenApiPlugin());
            conf.defaultContentType = "application/json";
        }).routes(() -> {
            path("monitoring", () -> {
                get("taskNames", nodeSlaveMonitoringWebController::getTaskNames);
                get("healthcheck", nodeSlaveMonitoringWebController::healthCheck);
            });
        });
    }

    private static OpenApiPlugin getConfiguredOpenApiPlugin() {
        Info info = new Info().version("1.0").description("Slave-server API");
        OpenApiOptions options = new OpenApiOptions(info)
                .activateAnnotationScanningFor("ru.babobka.nodeweb.webcontroller.slave")
                .path("/swagger-docs")
                .swagger(new SwaggerOptions("/swagger-ui"));
        return new OpenApiPlugin(options);
    }
}
