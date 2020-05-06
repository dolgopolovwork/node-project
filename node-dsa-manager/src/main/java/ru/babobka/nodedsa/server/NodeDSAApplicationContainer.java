package ru.babobka.nodedsa.server;

import io.javalin.Javalin;
import io.javalin.plugin.openapi.OpenApiOptions;
import io.javalin.plugin.openapi.OpenApiPlugin;
import io.javalin.plugin.openapi.ui.SwaggerOptions;
import io.swagger.v3.oas.models.info.Info;
import ru.babobka.nodeconfigs.ConfigsApplicationContainer;
import ru.babobka.nodeconfigs.dsa.validation.DSAServerConfigValidator;
import ru.babobka.nodedsa.server.webcontroller.NodeDataSignerWebController;
import ru.babobka.nodesecurity.SecurityApplicationContainer;
import ru.babobka.nodeutils.container.AbstractApplicationContainer;
import ru.babobka.nodeutils.container.Container;

import static io.javalin.apibuilder.ApiBuilder.*;

public class NodeDSAApplicationContainer extends AbstractApplicationContainer {

    @Override
    protected void containImpl(Container container) {
        container.put(new ConfigsApplicationContainer());

        container.put(new DSAServerConfigValidator());
        container.put(new SecurityApplicationContainer());
    }

    public static Javalin createWebServer() {
        NodeDataSignerWebController nodeDataSignerWebController = new NodeDataSignerWebController();
        return Javalin.create(conf -> {
            conf.registerPlugin(getConfiguredOpenApiPlugin());
        }).routes(() -> {
            path("dsa", () -> {
                get("pubKey", nodeDataSignerWebController::getPublicKey);
                post("sign", nodeDataSignerWebController::signData);
            });
        });
    }

    public static OpenApiPlugin getConfiguredOpenApiPlugin() {
        Info info = new Info().version("1.0").description("DSA-manager API");
        OpenApiOptions options = new OpenApiOptions(info)
                .activateAnnotationScanningFor("ru.babobka.nodedsa.server")
                .path("/swagger-docs")
                .swagger(new SwaggerOptions("/swagger-ui"));
        return new OpenApiPlugin(options);
    }
}
