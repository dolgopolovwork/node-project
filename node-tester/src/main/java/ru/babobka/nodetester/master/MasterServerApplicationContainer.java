package ru.babobka.nodetester.master;

import ru.babobka.nodebusiness.NodeBusinessApplicationContainer;
import ru.babobka.nodemasterserver.server.MasterServerApplicationSubContainer;
import ru.babobka.nodemasterserver.server.MasterServerConfig;
import ru.babobka.nodemasterserver.validation.config.MasterServerConfigValidator;
import ru.babobka.nodesecurity.SecurityApplicationContainer;
import ru.babobka.nodesecurity.config.SrpConfig;
import ru.babobka.nodetask.NodeTaskApplicationContainer;
import ru.babobka.nodeutils.NodeUtilsApplicationContainer;
import ru.babobka.nodeutils.container.ApplicationContainer;
import ru.babobka.nodeutils.container.Container;
import ru.babobka.nodeutils.container.ContainerException;
import ru.babobka.nodeutils.container.Properties;
import ru.babobka.nodeutils.enums.Env;
import ru.babobka.nodeutils.logger.SimpleLogger;
import ru.babobka.nodeutils.math.Fp;
import ru.babobka.nodeutils.math.SafePrime;
import ru.babobka.nodeutils.network.NodeConnectionFactory;
import ru.babobka.nodeutils.util.MathUtil;
import ru.babobka.nodeweb.NodeWebApplicationContainer;

/**
 * Created by 123 on 05.11.2017.
 */
public class MasterServerApplicationContainer implements ApplicationContainer {

    @Override
    public void contain(Container container) {
        try {
            container.put(new NodeUtilsApplicationContainer());
            MasterServerConfig config = createTestConfig();
            container.putIfNotExists(new NodeConnectionFactory());
            new MasterServerConfigValidator().validate(config);
            container.put(config);
            container.put(new SecurityApplicationContainer());
            container.put(createSrpConfig(config));
            container.putIfNotExists(SimpleLogger.defaultLogger("master-server", config.getLoggerFolder()));
            container.put(new NodeTaskApplicationContainer());
            container.put(new NodeBusinessApplicationContainer());
            container.put(new NodeWebApplicationContainer());
            container.put(new MasterServerApplicationSubContainer());
            SimpleLogger logger = container.get(SimpleLogger.class);
            logger.debug("container was successfully created");
        } catch (Exception e) {
            throw new ContainerException(e);
        }
    }

    private static MasterServerConfig createTestConfig() {
        MasterServerConfig config = new MasterServerConfig();
        config.setTasksFolderEnv(Env.NODE_TASKS.name());
        config.setLoggerFolderEnv(Env.NODE_LOGS.name());
        config.setAuthTimeOutMillis(2000);
        config.setClientListenerPort(9999);
        config.setDebugMode(true);
        config.setEnableCache(Properties.getBool("enableCache", false));
        config.setHeartBeatTimeOutMillis(5000);
        config.setRequestTimeOutMillis(15000);
        config.setSlaveListenerPort(9090);
        config.setWebListenerPort(8080);
        config.setBigSafePrime(MathUtil.getSafePrime(128));
        config.setChallengeBytes(16);
        return config;
    }

    private SrpConfig createSrpConfig(MasterServerConfig masterServerConfig) {
        SafePrime bigSafePrime = masterServerConfig.getBigSafePrime();
        Fp gen = new Fp(MathUtil.getGenerator(bigSafePrime), bigSafePrime.getPrime());
        return new SrpConfig(gen, masterServerConfig.getChallengeBytes());
    }
}
