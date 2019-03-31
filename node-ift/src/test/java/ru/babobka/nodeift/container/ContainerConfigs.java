package ru.babobka.nodeift.container;

import org.testcontainers.containers.Network;
import ru.babobka.nodeconfigs.master.MasterServerConfig;
import ru.babobka.nodeconfigs.slave.SlaveServerConfig;
import ru.babobka.nodeutils.container.Container;
import ru.babobka.nodeutils.time.TimerInvoker;
import ru.babobka.nodeutils.util.JSONUtil;
import ru.babobka.nodeutils.util.StreamUtil;

class ContainerConfigs {

    private static final String CONFIG_FOLDER = System.getProperty("user.dir") + "/config";
    static final Network NETWORK = Network.SHARED;
    private static final StreamUtil streamUtil = new StreamUtil();
    static final String MASTER_CONFIG_PATH = CONFIG_FOLDER + "/master-server-config.json";
    static final String SLAVE_CONFIG_PATH = CONFIG_FOLDER + "/slave-server-config.json";
    static final String SUBMASTER_CONFIG_PATH = CONFIG_FOLDER + "/submaster/submaster-server-config.json";
    static final String SUBMASTER_CONNECT_CONFIG_PATH = CONFIG_FOLDER + "/submaster/connect-config.json";
    static final String SUBMASTER_SLAVE_CONFIG_PATH = CONFIG_FOLDER + "/submaster/slave-server-config.json";
    static final MasterServerConfig masterServerConfig;
    static final MasterServerConfig submasterServerConfig;
    static final SlaveServerConfig submasterConnectConfig;
    static final SlaveServerConfig submasterSlaveConfig;
    static final SlaveServerConfig slaveServerConfig;

    static {
        try {
            masterServerConfig =
                    JSONUtil.readJsonFile(streamUtil, MASTER_CONFIG_PATH, MasterServerConfig.class);
            slaveServerConfig =
                    JSONUtil.readJsonFile(streamUtil, SLAVE_CONFIG_PATH, SlaveServerConfig.class);
            submasterServerConfig =
                    JSONUtil.readJsonFile(streamUtil, SUBMASTER_CONFIG_PATH, MasterServerConfig.class);
            submasterConnectConfig =
                    JSONUtil.readJsonFile(streamUtil, SUBMASTER_CONNECT_CONFIG_PATH, SlaveServerConfig.class);
            submasterSlaveConfig =
                    JSONUtil.readJsonFile(streamUtil, SUBMASTER_SLAVE_CONFIG_PATH, SlaveServerConfig.class);
            Container.getInstance().put(container -> {
                container.put(streamUtil);
                container.put(TimerInvoker.createMaxOneSecondDelay());
            });
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
