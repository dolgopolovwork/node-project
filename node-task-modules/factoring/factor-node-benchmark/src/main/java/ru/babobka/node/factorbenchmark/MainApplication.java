package ru.babobka.node.factorbenchmark;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import ru.babobka.dlp.CLI;
import ru.babobka.dlp.Client;
import ru.babobka.dlp.ClientApplicationContainer;
import ru.babobka.nodeserials.NodeRequest;
import ru.babobka.nodeserials.NodeResponse;
import ru.babobka.nodeserials.enumerations.ResponseStatus;
import ru.babobka.nodeutils.container.Container;
import ru.babobka.nodeutils.time.Timer;
import ru.babobka.nodeutils.util.TextUtil;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.Future;

public class MainApplication extends CLI {

    private static final String HOST_OPTION = "host";
    private static final String HOST_OPT = "h";
    private static final String PORT_OPTION = "port";
    private static final String PORT_OPT = "p";
    private static final String TESTS_OPTION = "tests";
    private static final String TESTS_OPT = "t";
    private static final String NUMBER_BIT_LENGTH_OPTION = "bitLength";
    private static final String NUMBER_BIT_LENGTH_OPT = "b";
    private static final int MIN_BIT_LENGTH = 16;

    private static final String TASK_NAME = "ru.babobka.factor.task.EllipticCurveFactorTask";

    static {
        Container.getInstance().put(new ClientApplicationContainer());
    }

    private static BigInteger createBigInteger(int bits) {
        int factorLength = bits / 2;
        BigInteger p = BigInteger.probablePrime(factorLength, new Random());
        BigInteger q = BigInteger.probablePrime(factorLength, new Random());
        return p.multiply(q);
    }

    private static Client createClient(String host, int port) {
        return new Client(host, port);
    }

    private static NodeRequest createFactorRequest(BigInteger number) {
        Map<String, Serializable> data = new HashMap<>();
        data.put("number", number);
        return NodeRequest.regular(UUID.randomUUID(), TASK_NAME, data);
    }

    @Override
    protected Options createCmdOptions() {
        Options options = new Options();
        Option host = Option.builder(HOST_OPT).longOpt(HOST_OPTION).hasArg().
                desc("Host of master node server").required().build();
        Option port = Option.builder(PORT_OPT).longOpt(PORT_OPTION).hasArg().
                desc("Port of master node server").required().build();
        Option tests = Option.builder(TESTS_OPT).longOpt(TESTS_OPTION).hasArg().
                desc("Tests to run").required().build();
        Option numberBitLength = Option.builder(NUMBER_BIT_LENGTH_OPT).longOpt(NUMBER_BIT_LENGTH_OPTION).hasArg().
                desc("Bit length of numbers to factor. Must be at least " + MIN_BIT_LENGTH).required().build();
        options.addOption(host).addOption(port).addOption(tests).addOption(numberBitLength);
        return options;
    }

    @Override
    protected void extraValidation(CommandLine cmd) throws ParseException {
        String cmdPort = cmd.getOptionValue(PORT_OPTION);
        int port = TextUtil.tryParseInt(cmdPort, -1);
        if (!TextUtil.isValidPort(port)) {
            throw new ParseException("invalid port " + cmdPort);
        }
        String cmdTests = cmd.getOptionValue(TESTS_OPTION);
        if (TextUtil.tryParseInt(cmdTests, 0) < 1) {
            throw new ParseException("invalid tests number " + cmdTests);
        }
        String cmdNumberBitLength = cmd.getOptionValue(NUMBER_BIT_LENGTH_OPTION);
        if (TextUtil.tryParseInt(cmdNumberBitLength, 0) < MIN_BIT_LENGTH) {
            throw new ParseException("invalid bit length " + cmdNumberBitLength);
        }
    }

    @Override
    protected void run(CommandLine cmd) {
        int numberBitLength = Integer.parseInt(cmd.getOptionValue(NUMBER_BIT_LENGTH_OPTION));
        BigInteger number = createBigInteger(numberBitLength);
        Timer timer = new Timer();
        int port = Integer.parseInt(cmd.getOptionValue(PORT_OPTION));
        int tests = Integer.parseInt(cmd.getOptionValue(TESTS_OPTION));
        try (Client client = createClient(cmd.getOptionValue(HOST_OPTION), port)) {
            for (int test = 0; test < tests; test++) {
                Future<NodeResponse> future = client.executeTask(createFactorRequest(number));
                NodeResponse response = future.get();
                if (response.getStatus() != ResponseStatus.NORMAL) {
                    printErr("Can not get the result. Response is " + response);
                    return;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
        print(numberBitLength + " bit number takes " +
                (timer.getTimePassed() / (double) tests) + "mls");
    }

    @Override
    protected String getAppName() {
        return "factor-benchmark";
    }

    public static void main(String[] args) {
        new MainApplication().onMain(args);
    }
}
