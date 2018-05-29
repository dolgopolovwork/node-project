package ru.babobka.nodeclient;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import ru.babobka.nodeserials.NodeRequest;
import ru.babobka.nodeserials.NodeResponse;
import ru.babobka.nodeserials.data.Data;
import ru.babobka.nodeserials.enumerations.ResponseStatus;
import ru.babobka.nodeutils.container.Container;
import ru.babobka.nodeutils.time.Timer;
import ru.babobka.nodeutils.util.TextUtil;

import java.io.IOException;
import java.math.BigInteger;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * Created by 123 on 16.12.2017.
 */
public class MainApplication extends CLI {
    private static final String TASK_NAME = "ru.babobka.factor.task.EllipticCurveFactorTask";
    private static final String HOST_OPTION = "host";
    private static final String HOST_OPT = "h";
    private static final String PORT_OPTION = "port";
    private static final String PORT_OPT = "p";
    private static final String NUMBER_OPTION = "number";
    private static final String NUMBER_OPT = "n";

    static {
        Container.getInstance().put(new ClientApplicationContainer());
    }

    @Override
    protected Options createOptions() {
        Options options = new Options();
        Option host = Option.builder(HOST_OPT).longOpt(HOST_OPTION).hasArg().
                desc("Host of master node server").required().build();
        Option port = Option.builder(PORT_OPT).longOpt(PORT_OPTION).hasArg().
                desc("Port of master node server").required().build();
        Option number = Option.builder(NUMBER_OPT).longOpt(NUMBER_OPTION).hasArg().
                desc("Number to factor").required().build();
        options.addOption(host).addOption(port).addOption(number);
        return options;
    }

    @Override
    protected String getAppName() {
        return "factor";
    }

    @Override
    protected void extraValidation(CommandLine cmd) throws ParseException {
        String cmdPort = cmd.getOptionValue(PORT_OPTION);
        int port = TextUtil.tryParseInt(cmdPort, -1);
        if (!TextUtil.isValidPort(port)) {
            throw new ParseException("invalid port " + cmdPort);
        }
        String cmdNumber = cmd.getOptionValue(NUMBER_OPTION);
        if (!TextUtil.isNumber(cmdNumber)) {
            throw new ParseException("invalid number " + cmdNumber);
        }
    }

    @Override
    protected void run(CommandLine cmd) throws Exception {
        int port = Integer.parseInt(cmd.getOptionValue(PORT_OPTION));
        BigInteger number = new BigInteger(cmd.getOptionValue(NUMBER_OPTION));
        try (Client client = createClient(cmd.getOptionValue(HOST_OPTION), port)) {
            Future<NodeResponse> future = client.executeTask(createFactorRequest(number));
            Timer timer = new Timer();
            NodeResponse response = future.get();
            if (response.getStatus() == ResponseStatus.NORMAL) {
                print("the result is " + response.getData() + ". " + timer.getTimePassed() + "mls passed.");
            } else {
                printErr("cannot get the result. the real data received is " + response);
            }
        } catch (IOException | InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

    private static Client createClient(String host, int port) {
        return new Client(host, port);
    }

    private static NodeRequest createFactorRequest(BigInteger number) {
        Data data = new Data();
        data.put("number", number);
        return NodeRequest.regular(UUID.randomUUID(), TASK_NAME, data);
    }

    public static void main(String[] args) {
        new MainApplication().onMain(args);
    }

}