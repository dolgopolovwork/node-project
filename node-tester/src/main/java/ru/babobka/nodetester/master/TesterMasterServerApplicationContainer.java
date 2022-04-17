package ru.babobka.nodetester.master;

import ru.babobka.masternoderun.MasterServerApplicationContainer;
import ru.babobka.nodebusiness.NodeBusinessApplicationContainer;
import ru.babobka.nodebusiness.dao.user.DBNodeUserDAOImpl;
import ru.babobka.nodeconfigs.master.*;
import ru.babobka.nodeconfigs.master.validation.MasterServerConfigValidator;
import ru.babobka.nodemasterserver.server.MasterServerApplicationSubContainer;
import ru.babobka.nodesecurity.SecurityApplicationContainer;
import ru.babobka.nodesecurity.keypair.Base64KeyPair;
import ru.babobka.nodesecurity.keypair.KeyDecoder;
import ru.babobka.nodesecurity.sign.SignatureValidator;
import ru.babobka.nodetask.NodeTaskApplicationContainer;
import ru.babobka.nodetester.dao.DummyNodeUserDAOImpl;
import ru.babobka.nodetester.key.TesterKey;
import ru.babobka.nodeutils.NodeUtilsApplicationContainer;
import ru.babobka.nodeutils.container.AbstractApplicationContainer;
import ru.babobka.nodeutils.container.Container;
import ru.babobka.nodeutils.container.Properties;
import ru.babobka.nodeutils.network.NodeConnectionFactory;
import ru.babobka.nodeutils.util.TextUtil;
import ru.babobka.nodeweb.NodeMasterWebApplicationContainer;

import java.security.KeyPair;

/**
 * Created by 123 on 05.11.2017.
 */
public class TesterMasterServerApplicationContainer extends AbstractApplicationContainer {

    private final boolean realDataBase;

    private TesterMasterServerApplicationContainer(boolean realDataBase) {
        this.realDataBase = realDataBase;
    }

    public static TesterMasterServerApplicationContainer withRealDb() {
        return new TesterMasterServerApplicationContainer(true);
    }

    public static TesterMasterServerApplicationContainer withNoDb() {
        return new TesterMasterServerApplicationContainer(false);
    }

    @Override
    protected void containImpl(Container container) {
        container.put(new NodeUtilsApplicationContainer());
        container.put(new SignatureValidator());
        MasterServerConfig config = createTestConfig();
        container.putIfAbsent(new NodeConnectionFactory());
        new MasterServerConfigValidator().validate(config);
        container.put(config);
        if (realDataBase) {
            container.put(MasterServerApplicationContainer.createSessionFactory(config));
            container.put(new DBNodeUserDAOImpl());
        } else {
            container.put(new DummyNodeUserDAOImpl());
        }

        container.put(new SecurityApplicationContainer());
        container.put(new NodeTaskApplicationContainer());
        container.put(new NodeBusinessApplicationContainer());
        container.put(new NodeMasterWebApplicationContainer());
        container.put(new MasterServerApplicationSubContainer(config));
    }

    private static MasterServerConfig createTestConfig() {
        MasterServerConfig config = new MasterServerConfig();
        FolderConfig folderConfig = new FolderConfig();
        folderConfig.setTasksFolder(TextUtil.getTasksFolder());
        folderConfig.setLoggerFolder(TextUtil.getLogFolder());
        config.setFolders(folderConfig);

        TimeConfig timeConfig = new TimeConfig();
        timeConfig.setAuthTimeOutMillis(5_000);
        timeConfig.setHeartBeatCycleMillis(10_000);
        timeConfig.setRequestReadTimeOutMillis(timeConfig.getHeartBeatCycleMillis() * 3);
        //15 minutes
        timeConfig.setDataOutDateMillis(1000 * 60 * 15);
        config.setTime(timeConfig);

        ModeConfig modeConfig = new ModeConfig();
        modeConfig.setTestUserMode(true);
        modeConfig.setCacheMode(Properties.getBool(TesterKey.ENABLE_CACHE, false));
        modeConfig.setSingleSessionMode(false);
        config.setModes(modeConfig);

        PortConfig portConfig = new PortConfig();
        portConfig.setSlaveListenerPort(19090);
        portConfig.setWebListenerPort(18081);
        portConfig.setClientListenerPort(19999);
        config.setPorts(portConfig);

        Base64KeyPair base64KeyPair = new Base64KeyPair();
        KeyPair keyPair = KeyDecoder.generateKeyPair();
        base64KeyPair.setPrivKey(TextUtil.toBase64(keyPair.getPrivate().getEncoded()));
        base64KeyPair.setPubKey(TextUtil.toBase64(keyPair.getPublic().getEncoded()));
        config.setKeyPair(base64KeyPair);

        DBConfig dbConfig = new DBConfig();
        dbConfig.setUser("test");
        dbConfig.setPassword("test");
        dbConfig.setHost("localhost");
        dbConfig.setPort(5432);
        config.setDbConfig(dbConfig);

        return config;
    }
}
