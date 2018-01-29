package ru.babobka.dlp;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import ru.babobka.nodeserials.NodeRequest;
import ru.babobka.nodeserials.NodeResponse;
import ru.babobka.nodeserials.enumerations.ResponseStatus;
import ru.babobka.nodeutils.container.Container;
import ru.babobka.nodeutils.time.Timer;
import ru.babobka.nodeutils.util.TextUtil;

import java.io.IOException;
import java.io.Serializable;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * Created by 123 on 16.12.2017.
 */
public class MainApplication extends CLI {
    private static final String TASK_NAME = "ru.babobka.dlp.task.PollardDlpTask";
    private static final String HOST_OPTION = "host";
    private static final String HOST_OPT = "h";
    private static final String PORT_OPTION = "port";
    private static final String PORT_OPT = "p";
    private static final String X_OPT = "x";
    private static final String Y_OPT = "y";
    private static final String MOD_OPTION = "mod";
    private static final String MOD_OPT = "m";

    static {
        Container.getInstance().put(new ClientApplicationContainer());
    }

    @Override
    protected Options createCmdOptions() {
        Options options = new Options();
        Option host = Option.builder(HOST_OPT).longOpt(HOST_OPTION).hasArg().
                desc("Host of master node server").required().build();
        Option port = Option.builder(PORT_OPT).longOpt(PORT_OPTION).hasArg().
                desc("Port of master node server").required().build();
        Option x = Option.builder(X_OPT).hasArg().
                desc("Generator of group").required().build();
        Option y = Option.builder(Y_OPT).hasArg().
                desc("Element of group").required().build();
        Option mod = Option.builder(MOD_OPT).longOpt(MOD_OPTION).hasArg().
                desc("Modulus of group").required().build();
        options.addOption(host).addOption(port).addOption(x).addOption(y).addOption(mod);
        return options;
    }

    @Override
    protected String getAppName() {
        return "dlp";
    }

    @Override
    protected void extraValidation(CommandLine cmd) throws ParseException {
        String cmdPort = cmd.getOptionValue(PORT_OPTION);
        int port = TextUtil.tryParseInt(cmdPort, -1);
        if (!TextUtil.isValidPort(port)) {
            throw new ParseException("invalid port " + cmdPort);
        }
        String cmdX = cmd.getOptionValue(X_OPT);
        if (!TextUtil.isNumber(cmdX)) {
            throw new ParseException("invalid x " + cmdX);
        }
        String cmdY = cmd.getOptionValue(Y_OPT);
        if (!TextUtil.isNumber(cmdY)) {
            throw new ParseException("invalid y " + cmdX);
        }
        String mod = cmd.getOptionValue(MOD_OPTION);
        if (!TextUtil.isNumber(cmdX)) {
            throw new ParseException("invalid mod " + mod);
        }
    }

    @Override
    protected void run(CommandLine cmd) throws Exception {
        int port = Integer.parseInt(cmd.getOptionValue(PORT_OPTION));
        BigInteger x = new BigInteger(cmd.getOptionValue(X_OPT));
        BigInteger y = new BigInteger(cmd.getOptionValue(Y_OPT));
        BigInteger mod = new BigInteger(cmd.getOptionValue(MOD_OPTION));
        try (Client client = createClient(cmd.getOptionValue(HOST_OPTION), port)) {
            Future<NodeResponse> future = client.executeTask(createDlpRequest(x, y, mod));
            Timer timer = new Timer();
            NodeResponse response = future.get();
            if (response.getStatus() == ResponseStatus.NORMAL) {
                print("The result is " + response.getData() + ". " + timer.getTimePassed() + "mls passed.");
            } else {
                printErr("Can not get the result. The real data received is " + response);
            }
        } catch (IOException | InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

    private static Client createClient(String host, int port) {
        return new Client(host, port);
    }

    private static NodeRequest createDlpRequest(BigInteger x, BigInteger y, BigInteger mod) {
        Map<String, Serializable> data = new HashMap<>();
        data.put("x", x);
        data.put("y", y);
        data.put("mod", mod);
        return NodeRequest.regular(UUID.randomUUID(), TASK_NAME, data);
    }

    public static void main(String[] args) {
        new MainApplication().onMain(args);
    }

}