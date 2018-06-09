package ru.babobka.nodetester.master;

import ru.babobka.nodebusiness.NodeBusinessApplicationContainer;
import ru.babobka.nodemasterserver.server.MasterServerApplicationSubContainer;
import ru.babobka.nodemasterserver.server.config.*;
import ru.babobka.nodemasterserver.validation.config.MasterServerConfigValidator;
import ru.babobka.nodesecurity.SecurityApplicationContainer;
import ru.babobka.nodesecurity.config.SrpConfig;
import ru.babobka.nodesecurity.rsa.RSAConfigFactory;
import ru.babobka.nodetask.NodeTaskApplicationContainer;
import ru.babobka.nodetester.key.TesterKey;
import ru.babobka.nodeutils.NodeUtilsApplicationContainer;
import ru.babobka.nodeutils.container.ApplicationContainer;
import ru.babobka.nodeutils.container.Container;
import ru.babobka.nodeutils.container.ContainerException;
import ru.babobka.nodeutils.container.Properties;
import ru.babobka.nodeutils.enums.Env;
import ru.babobka.nodeutils.logger.NodeLogger;
import ru.babobka.nodeutils.logger.SimpleLoggerFactory;
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
            container.putIfNotExists(SimpleLoggerFactory.defaultLogger("master-server", config.getFolders().getLoggerFolder()));
            container.put(new NodeTaskApplicationContainer());
            container.put(new NodeBusinessApplicationContainer());
            container.put(new NodeWebApplicationContainer());
            container.put(new MasterServerApplicationSubContainer());
            NodeLogger nodeLogger = container.get(NodeLogger.class);
            nodeLogger.debug("container was successfully created");
        } catch (Exception e) {
            throw new ContainerException(e);
        }
    }

    private static MasterServerConfig createTestConfig() {
        MasterServerConfig config = new MasterServerConfig();

        FolderConfig folderConfig = new FolderConfig();
        folderConfig.setTasksFolderEnv(Env.NODE_TASKS.name());
        folderConfig.setLoggerFolderEnv(Env.NODE_LOGS.name());
        config.setFolders(folderConfig);

        TimeConfig timeConfig = new TimeConfig();
        timeConfig.setAuthTimeOutMillis(5000);
        timeConfig.setHeartBeatCycleMillis(7000);
        timeConfig.setRequestReadTimeOutMillis(15000);
        //15 minutes
        timeConfig.setDataOutDateMillis(1000 * 60 * 15);
        config.setTime(timeConfig);

        ModeConfig modeConfig = new ModeConfig();
        modeConfig.setTestUserMode(true);
        modeConfig.setCacheMode(Properties.getBool(TesterKey.ENABLE_CACHE, false));
        modeConfig.setSingleSessionMode(false);
        config.setModes(modeConfig);

        PortConfig portConfig = new PortConfig();
        portConfig.setSlaveListenerPort(9090);
        portConfig.setWebListenerPort(8080);
        portConfig.setClientListenerPort(9999);
        config.setPorts(portConfig);

        SecurityConfig securityConfig = new SecurityConfig();
        securityConfig.setBigSafePrime(SafePrime.random((128)).getPrime());
        securityConfig.setChallengeBytes(16);
        securityConfig.setRsaConfig(RSAConfigFactory.create(128));
        config.setSecurity(securityConfig);
        return config;
    }

    private static SrpConfig createSrpConfig(MasterServerConfig masterServerConfig) {
        SecurityConfig securityConfig = masterServerConfig.getSecurity();
        SafePrime safePrime = new SafePrime(securityConfig.getBigSafePrime());
        Fp gen = new Fp(MathUtil.getGenerator(safePrime), safePrime.getPrime());
        return new SrpConfig(gen, securityConfig.getChallengeBytes());
    }
}
