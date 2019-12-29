package ru.babobka.nodeift.container;

import com.google.gson.Gson;
import lombok.NonNull;
import org.apache.http.client.fluent.Request;
import org.mockito.stubbing.Answer;
import org.testcontainers.containers.BindMode;
import org.testcontainers.containers.FixedHostPortGenericContainer;
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

import static org.mockito.Mockito.*;

public abstract class AbstractContainerITCase {

    private static final Network NETWORK = Network.SHARED;
    protected static final int MASTER_SERVER_WAIT_MILLIS = 5_000;
    protected static final int SLAVE_SERVER_WAIT_MILLIS = 10_000;
    protected static final String RPC_REPLY_QUEUE = "rpc_reply_queue";

    {
        LoggerInit.initConsoleLogger();
        Container.getInstance().put(container -> {
            container.put(new StreamUtil());
            container.put(TimerInvoker.createMaxOneSecondDelay());
        });
    }

    private static final Gson gson = new Gson();

    protected static GenericContainer createRMQ() {
        return new GenericContainer("rabbitmq:3-management")
                .withExposedPorts(ContainerConfigs.DEFAULT_RMQ_PORT)
                .withNetwork(NETWORK)
                .withNetworkAliases(ContainerConfigs.RMQ_NETWORK_ALIAS);
    }

    protected static GenericContainer createPostgres() {
        GenericContainer container = spy(new FixedHostPortGenericContainer("postgres:9.5")
                .withFixedExposedPort(ContainerConfigs.DEFAULT_POSTGRES_PORT, ContainerConfigs.DEFAULT_POSTGRES_PORT));
        container
                .withNetworkAliases(ContainerConfigs.POSTGRES_NETWORK_ALIAS)
                .withNetwork(NETWORK)
                .withEnv("POSTGRES_PASSWORD", "test")
                .withEnv("POSTGRES_USER", "test");
        container.addFileSystemBind(TextUtil.getEnv(Env.NODE_PROJECT_FOLDER) + "/init.sql", "/docker-entrypoint-initdb.d/init.sql", BindMode.READ_ONLY);
        initLogConsumer(container);
        return container;
    }

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

    protected static GenericContainer createMasterWithRMQ() {
        return createMaster().withEnv(ContainerConfigs.MASTER_RMQ_ENV)
                .withEnv("WAIT_HOSTS_TIMEOUT", "300")
                .withEnv("WAIT_SLEEP_INTERVAL", "3")
                .withEnv("WAIT_HOSTS",
                        ContainerConfigs.RMQ_NETWORK_ALIAS + ":"
                                + ContainerConfigs.DEFAULT_RMQ_PORT);
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
                TextUtil.getLogFolder(), "/logs", BindMode.READ_WRITE);
        container.addFileSystemBind(
                TextUtil.getTasksFolder(), "/tasks", BindMode.READ_ONLY);
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

    protected static int getRmqPort(@NonNull GenericContainer rmq) {
        return rmq.getMappedPort(ContainerConfigs.DEFAULT_RMQ_PORT);
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
        return Integer.parseInt(Request.Get("http://localhost:" + port + "/clustersize")
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
