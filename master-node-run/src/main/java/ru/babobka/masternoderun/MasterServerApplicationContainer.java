package ru.babobka.masternoderun;

import ru.babobka.nodebusiness.NodeBusinessApplicationContainer;
import ru.babobka.nodemasterserver.server.MasterServerApplicationSubContainer;
import ru.babobka.nodemasterserver.server.MasterServerConfig;
import ru.babobka.nodetask.NodeTaskApplicationContainer;
import ru.babobka.nodeutils.NodeUtilsApplicationContainer;
import ru.babobka.nodeutils.container.ApplicationContainer;
import ru.babobka.nodeutils.container.Container;
import ru.babobka.nodeutils.container.ContainerException;
import ru.babobka.nodeutils.logger.SimpleLogger;
import ru.babobka.nodeutils.network.NodeConnectionFactory;
import ru.babobka.nodeweb.NodeWebApplicationContainer;

import java.io.IOException;

/**
 * Created by 123 on 05.11.2017.
 */
public class MasterServerApplicationContainer implements ApplicationContainer {

    @Override
    public void contain(Container container) {
        try {
            MasterServerConfig config = container.get(MasterServerConfig.class);
            container.put(SimpleLogger.defaultLogger("master-server", config.getLoggerFolder()));
            container.put(new NodeConnectionFactory());
            container.put(new NodeUtilsApplicationContainer());
            container.put(new NodeTaskApplicationContainer());
            container.put(new NodeBusinessApplicationContainer());
            container.put(new NodeWebApplicationContainer());
            container.put(new MasterServerApplicationSubContainer());
        } catch (IOException | RuntimeException e) {
            throw new ContainerException(e);
        }
    }

}
