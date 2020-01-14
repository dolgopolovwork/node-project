package ru.babobka.nodeift;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import ru.babobka.nodebusiness.debug.DebugCredentials;
import ru.babobka.nodeclient.Client;
import ru.babobka.nodeconfigs.master.MasterServerConfig;
import ru.babobka.nodeconfigs.slave.SlaveServerConfig;
import ru.babobka.nodemasterserver.server.MasterServer;
import ru.babobka.nodesecurity.keypair.KeyDecoder;
import ru.babobka.nodeserials.NodeRequest;
import ru.babobka.nodeserials.NodeResponse;
import ru.babobka.nodeserials.enumerations.ResponseStatus;
import ru.babobka.nodetester.master.MasterServerRunner;
import ru.babobka.nodetester.slave.SlaveServerRunner;
import ru.babobka.nodetester.slave.cluster.SlaveServerCluster;
import ru.babobka.nodeutils.container.Container;
import ru.babobka.nodeutils.log.LoggerInit;
import ru.babobka.nodeutils.util.TextUtil;

import java.io.IOException;
import java.math.BigInteger;
import java.security.PublicKey;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static ru.babobka.nodeift.EllipticCurveITCase.createFactorTest;
import static ru.babobka.nodeift.PrimeCounterITCase.PRIME_COUNTER_LARGE_RANGE_ANSWER;
import static ru.babobka.nodeift.PrimeCounterITCase.getLargeRangeRequest;

/**
 * Created by 123 on 12.12.2017.
 */
public class ClientITCase {

    protected static MasterServer masterServer;

    @BeforeClass
    public static void setUp() throws IOException {
        Container.getInstance().clear();
        LoggerInit.initPersistentConsoleDebugLogger(TextUtil.getLogFolder(), ClientITCase.class.getSimpleName());
        MasterServerRunner.init();
        MasterServerConfig masterServerConfig = Container.getInstance().get(MasterServerConfig.class);
        PublicKey serverPublicKey = KeyDecoder.decodePublicKeyUnsafe(masterServerConfig.getKeyPair().getPubKey());
        SlaveServerRunner.init(serverPublicKey);
        masterServer = MasterServerRunner.runMasterServer();
    }

    @AfterClass
    public static void tearDown() throws InterruptedException {
        masterServer.interrupt();
        masterServer.join();
        Container.getInstance().clear();
    }

    @Test
    public void testPrimeCountNoSlaves() throws IOException, InterruptedException, ExecutionException {
        MasterServerConfig masterConfig = Container.getInstance().get(MasterServerConfig.class);
        SlaveServerConfig slaveServerConfig = Container.getInstance().get(SlaveServerConfig.class);
        try (Client client = new Client(slaveServerConfig.getMasterServerHost(), masterConfig.getPorts().getClientListenerPort())) {
            Future<NodeResponse> future = client.executeTask(getLargeRangeRequest());
            NodeResponse response = future.get();
            assertEquals(response.getStatus(), ResponseStatus.NO_NODES);
        }
    }

    @Test
    public void testPrimeCountTwoSlaves() throws IOException, InterruptedException, ExecutionException {
        MasterServerConfig masterConfig = Container.getInstance().get(MasterServerConfig.class);
        SlaveServerConfig slaveServerConfig = Container.getInstance().get(SlaveServerConfig.class);
        try (SlaveServerCluster slaveServerCluster = new SlaveServerCluster(DebugCredentials.USER_NAME, DebugCredentials.PRIV_KEY, 2);
             Client client = new Client(slaveServerConfig.getMasterServerHost(), masterConfig.getPorts().getClientListenerPort())) {
            slaveServerCluster.start();
            Future<NodeResponse> future = client.executeTask(getLargeRangeRequest());
            NodeResponse response = future.get();
            assertEquals((int) response.getDataValue("primeCount"), PRIME_COUNTER_LARGE_RANGE_ANSWER);
        }
    }

    @Test
    public void testPrimeCountTwoSlavesMassive() throws IOException, InterruptedException, ExecutionException {
        MasterServerConfig masterConfig = Container.getInstance().get(MasterServerConfig.class);
        SlaveServerConfig slaveServerConfig = Container.getInstance().get(SlaveServerConfig.class);
        try (SlaveServerCluster slaveServerCluster = new SlaveServerCluster(DebugCredentials.USER_NAME, DebugCredentials.PRIV_KEY, 2);
             Client client = new Client(slaveServerConfig.getMasterServerHost(), masterConfig.getPorts().getClientListenerPort())) {
            slaveServerCluster.start();
            for (int i = 0; i < getTests(); i++) {
                Future<NodeResponse> future = client.executeTask(getLargeRangeRequest());
                NodeResponse response = future.get();
                assertEquals((int) response.getDataValue("primeCount"), PRIME_COUNTER_LARGE_RANGE_ANSWER);
            }
        }
    }

    @Test
    public void testFactorTwoSlaves() throws IOException, InterruptedException, ExecutionException {
        MasterServerConfig masterConfig = Container.getInstance().get(MasterServerConfig.class);
        SlaveServerConfig slaveServerConfig = Container.getInstance().get(SlaveServerConfig.class);
        try (SlaveServerCluster slaveServerCluster = new SlaveServerCluster(DebugCredentials.USER_NAME, DebugCredentials.PRIV_KEY, 2);
             Client client = new Client(slaveServerConfig.getMasterServerHost(), masterConfig.getPorts().getClientListenerPort())) {
            slaveServerCluster.start();
            int bits = 45;
            BigInteger p = BigInteger.probablePrime(bits, new Random());
            BigInteger q = BigInteger.probablePrime(bits, new Random());
            NodeRequest request = createFactorTest(p, q);
            Future<NodeResponse> future = client.executeTask(request);
            NodeResponse response = future.get();
            BigInteger factor = response.getDataValue("factor");
            assertTrue(factor.equals(p) || factor.equals(q));
        }
    }

    @Test
    public void testFactorOneSlaveTooBigRequest() throws IOException, InterruptedException, ExecutionException {
        MasterServerConfig masterConfig = Container.getInstance().get(MasterServerConfig.class);
        SlaveServerConfig slaveServerConfig = Container.getInstance().get(SlaveServerConfig.class);
        try (SlaveServerCluster slaveServerCluster = new SlaveServerCluster(DebugCredentials.USER_NAME, DebugCredentials.PRIV_KEY);
             Client client = new Client(slaveServerConfig.getMasterServerHost(), masterConfig.getPorts().getClientListenerPort())) {
            slaveServerCluster.start();
            int bits = 256;
            BigInteger p = BigInteger.probablePrime(bits, new Random());
            BigInteger q = BigInteger.probablePrime(bits, new Random());
            NodeRequest request = createFactorTest(p, q);
            Future<NodeResponse> future = client.executeTask(request);
            NodeResponse response = future.get();
            assertEquals(response.getStatus(), ResponseStatus.VALIDATION_ERROR);
        }
    }

    @Test
    public void testFactorTwoSlavesMassive() throws IOException, InterruptedException, ExecutionException {
        MasterServerConfig masterConfig = Container.getInstance().get(MasterServerConfig.class);
        SlaveServerConfig slaveServerConfig = Container.getInstance().get(SlaveServerConfig.class);
        try (SlaveServerCluster slaveServerCluster = new SlaveServerCluster(DebugCredentials.USER_NAME, DebugCredentials.PRIV_KEY, 2);
             Client client = new Client(slaveServerConfig.getMasterServerHost(), masterConfig.getPorts().getClientListenerPort())) {
            slaveServerCluster.start();
            int bits = 35;
            for (int i = 0; i < getTests(); i++) {
                BigInteger p = BigInteger.probablePrime(bits, new Random());
                BigInteger q = BigInteger.probablePrime(bits, new Random());
                NodeRequest request = createFactorTest(p, q);
                Future<NodeResponse> future = client.executeTask(request);
                NodeResponse response = future.get();
                BigInteger factor = response.getDataValue("factor");
                assertTrue(factor.equals(p) || factor.equals(q));
            }
        }
    }

    protected int getTests() {
        return 35;
    }

}
