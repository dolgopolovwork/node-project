package ru.babobka.dlp;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.ParseException;
import ru.babobka.nodeclient.Client;
import ru.babobka.nodeclient.ClientApplicationContainer;
import ru.babobka.nodeclient.console.CLI;
import ru.babobka.nodeserials.NodeRequest;
import ru.babobka.nodeserials.NodeResponse;
import ru.babobka.nodeserials.data.Data;
import ru.babobka.nodeserials.enumerations.ResponseStatus;
import ru.babobka.nodeutils.container.Container;
import ru.babobka.nodeutils.time.Timer;
import ru.babobka.nodeutils.util.TextUtil;

import java.io.IOException;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * Created by 123 on 16.12.2017.
 */
public class DlpApp extends CLI {
    private static final String TASK_NAME = "ru.babobka.dlp.task.regular.PollardDlpTask";
    private static final String HOST_OPTION = "host";
    private static final String PORT_OPTION = "port";
    private static final String X_OPT = "x";
    private static final String Y_OPT = "y";
    private static final String MOD_OPTION = "mod";

    static {
        Container.getInstance().put(new ClientApplicationContainer());
    }

    @Override
    public List<Option> createOptions() {
        Option host = createRequiredArgOption(HOST_OPTION, "Host of master node server");
        Option port = createRequiredArgOption(PORT_OPTION, "Port of master node server");
        Option x = createRequiredArgOption(X_OPT, "Generator of group");
        Option y = createRequiredArgOption(Y_OPT, "Element of group");
        Option mod = createRequiredArgOption(MOD_OPTION, "Modulus of group");
        return Arrays.asList(host, port, x, y, mod);
    }

    @Override
    public String getAppName() {
        return "dlp";
    }

    @Override
    public void extraValidation(CommandLine cmd) throws ParseException {
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
    public void run(CommandLine cmd) {
        int port = Integer.parseInt(cmd.getOptionValue(PORT_OPTION));
        BigInteger x = new BigInteger(cmd.getOptionValue(X_OPT));
        BigInteger y = new BigInteger(cmd.getOptionValue(Y_OPT));
        BigInteger mod = new BigInteger(cmd.getOptionValue(MOD_OPTION));
        try (Client client = createClient(cmd.getOptionValue(HOST_OPTION), port)) {
            Future<NodeResponse> future = client.executeTask(createDlpRequest(x, y, mod));
            Timer timer = new Timer();
            NodeResponse response = future.get();
            if (response.getStatus() == ResponseStatus.NORMAL) {
                print("the result is " + response.getData() + ". " + timer.getTimePassed() + "mls passed.");
            } else {
                printErr("can not get the result. the real data received is " + response);
            }
        } catch (IOException | InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

    private static Client createClient(String host, int port) {
        return new Client(host, port);
    }

    private static NodeRequest createDlpRequest(BigInteger x, BigInteger y, BigInteger mod) {
        Data data = new Data();
        data.put("x", x);
        data.put("y", y);
        data.put("mod", mod);
        return NodeRequest.regular(UUID.randomUUID(), TASK_NAME, data);
    }

    public static void main(String[] args) {
        new DlpApp().onStart(args);
    }

}
