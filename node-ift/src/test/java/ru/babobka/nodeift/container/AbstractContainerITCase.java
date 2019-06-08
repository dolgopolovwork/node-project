package ru.babobka.nodeift.container;

import com.google.gson.Gson;
import lombok.NonNull;
import org.apache.http.client.fluent.Request;
import org.testcontainers.containers.BindMode;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.output.OutputFrame;
import ru.babobka.nodebusiness.monitoring.TaskMonitoringData;
import ru.babobka.nodeutils.enums.Env;
import ru.babobka.nodeutils.log.LoggerInit;
import ru.babobka.nodeutils.util.TextUtil;

import java.io.IOException;
import java.util.function.Consumer;

public abstract class AbstractContainerITCase {

    static {
        LoggerInit.initConsoleLogger();
    }

    private static final Gson gson = new Gson();

    protected static GenericContainer createMaster() {
        GenericContainer master = new GenericContainer<>(DockerImage.MASTER.getImageName())
                .withNetwork(ContainerConfigs.NETWORK)
                .withNetworkAliases(ContainerConfigs.slaveServerConfig.getServerHost())
                .withExposedPorts(
                        ContainerConfigs.masterServerConfig.getPorts().getClientListenerPort(),
                        ContainerConfigs.masterServerConfig.getPorts().getWebListenerPort());
        initLogConsumer(master);
        master.addFileSystemBind(
                ContainerConfigs.MASTER_CONFIG_PATH, "/opt/master/config/master-server-config.json", BindMode.READ_ONLY);
        mountLogsAndTasks(master, "master");
        return master;
    }

    protected static GenericContainer createSubMaster() {
        GenericContainer submaster = new GenericContainer<>(DockerImage.SUBMASTER.getImageName())
                .withEnv("WAIT_HOSTS_TIMEOUT", "300")
                .withEnv("WAIT_SLEEP_INTERVAL", "3")
                .withEnv("WAIT_HOSTS",
                        ContainerConfigs.submasterConnectConfig.getServerHost() + ":"
                                + ContainerConfigs.submasterConnectConfig.getServerPort())
                .withNetwork(ContainerConfigs.NETWORK)
                .withNetworkAliases(ContainerConfigs.submasterSlaveConfig.getServerHost())
                .withExposedPorts(
                        ContainerConfigs.submasterServerConfig.getPorts().getClientListenerPort(),
                        ContainerConfigs.submasterServerConfig.getPorts().getWebListenerPort());
        initLogConsumer(submaster);
        submaster.addFileSystemBind(
                ContainerConfigs.SUBMASTER_CONFIG_PATH, "/opt/submaster/config/submaster-server-config.json", BindMode.READ_ONLY);
        submaster.addFileSystemBind(
                ContainerConfigs.SUBMASTER_CONNECT_CONFIG_PATH, "/opt/submaster/config/connect-config.json", BindMode.READ_ONLY);
        mountLogsAndTasks(submaster, "submaster");
        return submaster;
    }

    protected static GenericContainer createSubMasterSlave() {
        GenericContainer slave = new GenericContainer<>(DockerImage.SLAVE.getImageName())
                .withEnv("WAIT_HOSTS_TIMEOUT", "300")
                .withEnv("WAIT_SLEEP_INTERVAL", "3")
                .withEnv("WAIT_HOSTS",
                        ContainerConfigs.submasterSlaveConfig.getServerHost() + ":"
                                + ContainerConfigs.submasterSlaveConfig.getServerPort())
                .withNetwork(ContainerConfigs.NETWORK);
        initLogConsumer(slave);
        mountLogsAndTasks(slave, "slave");
        slave.addFileSystemBind(
                ContainerConfigs.SUBMASTER_SLAVE_CONFIG_PATH, "/opt/slave/config/slave-server-config.json", BindMode.READ_ONLY);
        return slave;
    }

    protected static GenericContainer createSlave() {
        GenericContainer slave = new GenericContainer<>(DockerImage.SLAVE.getImageName())
                .withEnv("WAIT_HOSTS_TIMEOUT", "300")
                .withEnv("WAIT_SLEEP_INTERVAL", "3")
                .withEnv("WAIT_HOSTS",
                        ContainerConfigs.slaveServerConfig.getServerHost() + ":"
                                + ContainerConfigs.slaveServerConfig.getServerPort())
                .withNetwork(ContainerConfigs.NETWORK);
        initLogConsumer(slave);
        mountLogsAndTasks(slave, "slave");
        slave.addFileSystemBind(
                ContainerConfigs.SLAVE_CONFIG_PATH, "/opt/slave/config/slave-server-config.json", BindMode.READ_ONLY);
        return slave;
    }

    private static void mountLogsAndTasks(@NonNull GenericContainer container, @NonNull String folderName) {
        container.withEnv(Env.NODE_LOGS.name(), "logs").withEnv(Env.NODE_TASKS.name(), "tasks");
        container.addFileSystemBind(
                TextUtil.getEnv(Env.NODE_LOGS), "/opt/" + folderName + "/logs", BindMode.READ_WRITE);
        container.addFileSystemBind(
                TextUtil.getEnv(Env.NODE_TASKS), "/opt/" + folderName + "/tasks", BindMode.READ_ONLY);
    }

    private static void initLogConsumer(@NonNull GenericContainer container) {
        container.withLogConsumer(new Consumer<OutputFrame>() {
            @Override
            public void accept(OutputFrame outputFrame) {
                System.err.print(container.getDockerImageName() + "\t" + outputFrame.getUtf8String());
            }
        });
    }

    protected static int getMasterClientPort(@NonNull GenericContainer master) {
        return master.getMappedPort(ContainerConfigs.masterServerConfig.getPorts().getClientListenerPort());
    }

    protected static boolean isMasterHealthy(@NonNull GenericContainer master) throws IOException {
        return isHealthy(getMasterWebPort(master));
    }

    protected static int getMasterClusterSize(@NonNull GenericContainer master) throws IOException {
        return getClusterSize(getMasterWebPort(master));
    }

    protected static TaskMonitoringData getMasterTaskMonitoring(@NonNull GenericContainer master) throws IOException {
        return getTaskMonitoringData(getMasterWebPort(master));
    }

    protected static boolean isSubmasterHealthy(@NonNull GenericContainer master) throws IOException {
        return isHealthy(getSubmasterWebPort(master));
    }

    protected static int getSubmasterClusterSize(@NonNull GenericContainer submaster) throws IOException {
        return getClusterSize(getSubmasterWebPort(submaster));
    }

    private static boolean isHealthy(int port) throws IOException {
        return Request.Get("http://localhost:" + port + "/healthcheck")
                .execute()
                .returnResponse()
                .getStatusLine()
                .getStatusCode() == 200;
    }

    private static int getClusterSize(int port) throws IOException {
        return Integer.valueOf(Request.Get("http://localhost:" + port + "/clustersize")
                .execute().returnContent().asString());
    }

    private static TaskMonitoringData getTaskMonitoringData(int port) throws IOException {
        return gson.fromJson(Request.Get("http://localhost:" + port + "/monitoring")
                .execute().returnContent().asString(), TaskMonitoringData.class);
    }

    private static int getMasterWebPort(@NonNull GenericContainer master) {
        return master.getMappedPort(ContainerConfigs.masterServerConfig.getPorts().getWebListenerPort());
    }

    private static int getSubmasterWebPort(@NonNull GenericContainer master) {
        return master.getMappedPort(ContainerConfigs.submasterServerConfig.getPorts().getWebListenerPort());
    }
}
