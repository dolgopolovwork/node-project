package ru.babobka.nodemasterserver.server;

import io.javalin.Javalin;
import io.javalin.plugin.openapi.OpenApiOptions;
import io.javalin.plugin.openapi.OpenApiPlugin;
import io.javalin.plugin.openapi.ui.SwaggerOptions;
import io.swagger.v3.oas.models.info.Info;
import lombok.NonNull;
import ru.babobka.nodebusiness.monitoring.TaskMonitoringService;
import ru.babobka.nodeconfigs.master.MasterServerConfig;
import ru.babobka.nodemasterserver.client.ClientStorage;
import ru.babobka.nodemasterserver.client.IncomingClientListenerThread;
import ru.babobka.nodesecurity.keypair.KeyDecoder;
import ru.babobka.nodesecurity.sign.DefaultDigitalSigner;
import ru.babobka.nodesecurity.sign.SignatureValidator;
import ru.babobka.nodeutils.key.MasterServerKey;
import ru.babobka.nodemasterserver.listener.CacheRequestListener;
import ru.babobka.nodemasterserver.listener.OnRaceStyleTaskIsReady;
import ru.babobka.nodemasterserver.listener.OnTaskIsReady;
import ru.babobka.nodemasterserver.mapper.NodeResponseErrorMapper;
import ru.babobka.nodemasterserver.mapper.ResponsesMapper;
import ru.babobka.nodemasterserver.model.ResponseStorage;
import ru.babobka.nodemasterserver.service.*;
import ru.babobka.nodemasterserver.slave.IncomingSlaveListenerThread;
import ru.babobka.nodemasterserver.slave.Sessions;
import ru.babobka.nodemasterserver.slave.SlaveFactory;
import ru.babobka.nodemasterserver.slave.SlavesStorage;
import ru.babobka.nodemasterserver.slave.pipeline.SlaveCreatingPipelineFactory;
import ru.babobka.nodemasterserver.thread.HeartBeatingThread;
import ru.babobka.nodetask.TaskPool;
import ru.babobka.nodetask.model.StoppedTasks;
import ru.babobka.nodeutils.container.AbstractApplicationContainer;
import ru.babobka.nodeutils.container.Container;
import ru.babobka.nodeutils.thread.PrettyNamedThreadPoolFactory;
import ru.babobka.nodeutils.util.StreamUtil;
import ru.babobka.nodeweb.webcontroller.NodeMonitoringWebController;
import ru.babobka.nodeweb.webcontroller.NodeUsersCRUDWebController;

import static io.javalin.apibuilder.ApiBuilder.*;

/**
 * Created by 123 on 18.02.2018.
 */
public class MasterServerApplicationSubContainer extends AbstractApplicationContainer {
    private final MasterServerConfig masterServerConfig;

    public MasterServerApplicationSubContainer(@NonNull MasterServerConfig masterServerConfig) {
        this.masterServerConfig = masterServerConfig;
    }

    @Override
    protected void containImpl(Container container) throws Exception {
        StreamUtil streamUtil = container.get(StreamUtil.class);
        container.put(new SignatureValidator());
        container.put(new NodeResponseErrorMapper());
        container.put(new Sessions());
        container.put(new SlavesStorage());
        container.put(new DistributionService());
        container.put(new ResponseStorage());
        container.put(new SlaveCreatingPipelineFactory());
        container.put(new ClientStorage());
        container.put(MasterServerKey.MASTER_SERVER_TASK_POOL, new TaskPool(
                masterServerConfig.getFolders().getTasksFolder()));
        container.put(new TaskMonitoringService());
        container.put(new ResponsesMapper());
        if (masterServerConfig.getModes().isCacheMode()) {
            container.put(new CacheRequestListener());
            container.put(new MasterTaskServiceCacheProxy(new MasterTaskService()));
        } else {
            container.put(new MasterTaskService());
        }
        container.put(MasterServerKey.MASTER_DSA_MANAGER, new DefaultDigitalSigner(KeyDecoder.decodePrivateKey(masterServerConfig.getKeyPair().getPrivKey())));
        container.put(new StoppedTasks());
        container.put(new SlaveFactory());
        container.put(MasterServerKey.CLIENTS_THREAD_POOL,
                PrettyNamedThreadPoolFactory.fixedDaemonThreadPool("client_thread_pool")
        );
        container.put(new IncomingClientListenerThread(streamUtil.createServerSocket(
                masterServerConfig.getPorts().getClientListenerPort())));
        container.put(new HeartBeatingThread());
        container.put(new MasterAuthService());
        container.put(new IncomingSlaveListenerThread(streamUtil.createServerSocket(
                masterServerConfig.getPorts().getSlaveListenerPort())));
        container.put(new OnTaskIsReady());
        container.put(new OnRaceStyleTaskIsReady());
        container.put(new NodeMasterInfoServiceImpl());
        container.put(createWebServer());
    }

    private static Javalin createWebServer() {
        NodeUsersCRUDWebController nodeUsersCRUDWebController = new NodeUsersCRUDWebController();
        NodeMonitoringWebController nodeMonitoringWebController = new NodeMonitoringWebController();
        return Javalin.create(conf -> {
            conf.registerPlugin(getConfiguredOpenApiPlugin());
            conf.defaultContentType = "application/json";
        }).routes(() -> {
            path("users", () -> {
                get("all", nodeUsersCRUDWebController::getAllUsers);
                path(":id", () -> {
                    get(nodeUsersCRUDWebController::getUser);
                    delete(nodeUsersCRUDWebController::deleteUser);
                });
                post(nodeUsersCRUDWebController::updateUser);
                put(nodeUsersCRUDWebController::createUser);
            });
            path("monitoring", () -> {
                get("tasksStats", nodeMonitoringWebController::getTasksMonitoringData);
                get("clustersize", nodeMonitoringWebController::getClusterSize);
                get("healthcheck", nodeMonitoringWebController::healthCheck);
                get("ready", nodeMonitoringWebController::readinessCheck);
                get("startTime", nodeMonitoringWebController::getStartTime);
                get("taskNames", nodeMonitoringWebController::getTaskNames);
                get("slaves", nodeMonitoringWebController::getSlaves);
            });
        });
    }

    private static OpenApiPlugin getConfiguredOpenApiPlugin() {
        Info info = new Info().version("1.0").description("Master-server API");
        OpenApiOptions options = new OpenApiOptions(info)
                .activateAnnotationScanningFor("ru.babobka.nodeweb")
                .path("/swagger-docs")
                .swagger(new SwaggerOptions("/swagger-ui"));
        return new OpenApiPlugin(options);
    }
}
