package ru.babobka.masternoderun;

import ru.babobka.nodebusiness.NodeBusinessApplicationContainer;
import ru.babobka.nodemasterserver.server.MasterServerApplicationSubContainer;
import ru.babobka.nodemasterserver.server.config.MasterServerConfig;
import ru.babobka.nodesecurity.SecurityApplicationContainer;
import ru.babobka.nodesecurity.config.SrpConfig;
import ru.babobka.nodetask.NodeTaskApplicationContainer;
import ru.babobka.nodeutils.NodeUtilsApplicationContainer;
import ru.babobka.nodeutils.container.ApplicationContainer;
import ru.babobka.nodeutils.container.Container;
import ru.babobka.nodeutils.container.ContainerException;
import ru.babobka.nodeutils.logger.SimpleLogger;
import ru.babobka.nodeutils.math.Fp;
import ru.babobka.nodeutils.math.SafePrime;
import ru.babobka.nodeutils.network.NodeConnectionFactory;
import ru.babobka.nodeutils.util.MathUtil;
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
            container.put(SimpleLogger.defaultLogger("master-server", config.getFolders().getLoggerFolder()));
            container.put(new SecurityApplicationContainer());
            container.put(createSrpConfig(config));
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

    private SrpConfig createSrpConfig(MasterServerConfig masterServerConfig) {
        SafePrime bigSafePrime = masterServerConfig.getSecurity().getBigSafePrime();
        Fp gen = new Fp(MathUtil.getGenerator(bigSafePrime), bigSafePrime.getPrime());
        return new SrpConfig(gen, masterServerConfig.getSecurity().getChallengeBytes());
    }

}
