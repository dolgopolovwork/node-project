package ru.babobka.masternoderun;

import org.apache.log4j.Logger;
import ru.babobka.nodebusiness.NodeBusinessApplicationContainer;
import ru.babobka.nodeconfigs.master.MasterServerConfig;
import ru.babobka.nodemasterserver.server.MasterServerApplicationSubContainer;
import ru.babobka.nodesecurity.SecurityApplicationContainer;
import ru.babobka.nodetask.NodeTaskApplicationContainer;
import ru.babobka.nodeutils.NodeUtilsApplicationContainer;
import ru.babobka.nodeutils.container.AbstractApplicationContainer;
import ru.babobka.nodeutils.container.Container;
import ru.babobka.nodeutils.network.NodeConnectionFactory;
import ru.babobka.nodeweb.NodeWebApplicationContainer;

/**
 * Created by 123 on 05.11.2017.
 */
public class MasterServerApplicationContainer extends AbstractApplicationContainer {

    private static final Logger logger = Logger.getLogger(MasterServerApplicationContainer.class);

    @Override
    protected void containImpl(Container container) {
        MasterServerConfig config = container.get(MasterServerConfig.class);
        container.put(new SecurityApplicationContainer());
        container.put(new NodeConnectionFactory());
        container.put(new NodeUtilsApplicationContainer());
        container.put(new NodeTaskApplicationContainer());
        container.put(new NodeBusinessApplicationContainer());
        container.put(new NodeWebApplicationContainer());
        container.put(new MasterServerApplicationSubContainer(config));
    }
}
