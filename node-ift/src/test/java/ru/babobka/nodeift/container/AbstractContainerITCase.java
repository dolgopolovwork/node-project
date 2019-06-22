package ru.babobka.nodeift.container;

import com.google.gson.Gson;
import lombok.NonNull;
import org.apache.http.client.fluent.Request;
import org.testcontainers.containers.BindMode;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.output.OutputFrame;
import ru.babobka.nodebusiness.monitoring.TaskMonitoringData;
import ru.babobka.nodeutils.container.Container;
import ru.babobka.nodeutils.enums.Env;
import ru.babobka.nodeutils.log.LoggerInit;
import ru.babobka.nodeutils.time.TimerInvoker;
import ru.babobka.nodeutils.util.StreamUtil;
import ru.babobka.nodeutils.util.TextUtil;

import java.io.IOException;
import java.util.function.Consumer;

public abstract class AbstractContainerITCase {

    private static final Network NETWORK = Network.SHARED;

    protected static final int MASTER_SERVER_WAIT_MILLIS = 5_000;
    protected static final int SLAVE_SERVER_WAIT_MILLIS = 10_000;

    static {
        LoggerInit.initConsoleLogger();
        Container.getInstance().put(container -> {
            container.put(new StreamUtil());
            container.put(TimerInvoker.createMaxOneSecondDelay());
        });
    }

    private static final Gson gson = new Gson();

    protected static GenericContainer createMaster() {
        GenericContainer master = new GenericContainer<>(DockerImage.MASTER.getImageName())
                .withNetwork(NETWORK)
                .withEnv(ContainerConfigs.MASTER_ENV)
                .withNetworkAliases(ContainerConfigs.MASTER_SERVER_NETWORK_ALIAS)
                .withExposedPorts(
                        ContainerConfigs.MASTER_CLIENT_PORT,
                        ContainerConfigs.MASTER_SLAVE_PORT,
                        ContainerConfigs.MASTER_WEB_PORT);
        initLogConsumer(master);
        mountLogsAndTasks(master);
        return master;
    }

    protected static GenericContainer createSubMaster() {
        GenericContainer submaster = new GenericContainer<>(DockerImage.SUBMASTER.getImageName())
                .withEnv(ContainerConfigs.SUBMASTER_ENV)
                .withEnv("WAIT_HOSTS_TIMEOUT", "300")
                .withEnv("WAIT_SLEEP_INTERVAL", "3")
                .withEnv("WAIT_HOSTS",
                        ContainerConfigs.MASTER_SERVER_NETWORK_ALIAS + ":"
                                + ContainerConfigs.MASTER_SLAVE_PORT)
                .withNetwork(NETWORK)
                .withNetworkAliases(ContainerConfigs.SUBMASTER_SERVER_NETWORK_ALIAS)
                .withExposedPorts(
                        ContainerConfigs.SUBMASTER_CLIENT_PORT,
                        ContainerConfigs.SUBMASTER_SLAVE_PORT,
                        ContainerConfigs.SUBMASTER_WEB_PORT);
        initLogConsumer(submaster);
        mountLogsAndTasks(submaster);
        return submaster;
    }

    protected static GenericContainer createSubMasterSlave() {
        GenericContainer slave = new GenericContainer<>(DockerImage.SLAVE.getImageName())
                .withEnv(ContainerConfigs.SUBMASTERSLAVE_ENV)
                .withEnv("WAIT_HOSTS_TIMEOUT", "300")
                .withEnv("WAIT_SLEEP_INTERVAL", "3")
                .withEnv("WAIT_HOSTS",
                        ContainerConfigs.SUBMASTER_SERVER_NETWORK_ALIAS + ":"
                                + ContainerConfigs.SUBMASTER_SLAVE_PORT)
                .withNetwork(NETWORK);
        initLogConsumer(slave);
        mountLogsAndTasks(slave);
        return slave;
    }

    protected static GenericContainer createSlave() {
        GenericContainer slave = new GenericContainer<>(DockerImage.SLAVE.getImageName())
                .withEnv(ContainerConfigs.SLAVE_ENV)
                .withEnv("WAIT_HOSTS_TIMEOUT", "300")
                .withEnv("WAIT_SLEEP_INTERVAL", "3")
                .withEnv("WAIT_HOSTS",
                        ContainerConfigs.MASTER_SERVER_NETWORK_ALIAS + ":" + ContainerConfigs.MASTER_SLAVE_PORT)
                .withNetwork(NETWORK);
        initLogConsumer(slave);
        mountLogsAndTasks(slave);
        return slave;
    }

    private static void mountLogsAndTasks(@NonNull GenericContainer container) {
        container.addFileSystemBind(
                TextUtil.getEnv(Env.NODE_LOGS), "/logs", BindMode.READ_WRITE);
        container.addFileSystemBind(
                TextUtil.getEnv(Env.NODE_TASKS), "/tasks", BindMode.READ_ONLY);
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
        return master.getMappedPort(ContainerConfigs.MASTER_CLIENT_PORT);
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
        return master.getMappedPort(ContainerConfigs.MASTER_WEB_PORT);
    }

    private static int getSubmasterWebPort(@NonNull GenericContainer submaster) {
        return submaster.getMappedPort(ContainerConfigs.SUBMASTER_WEB_PORT);
    }

}
