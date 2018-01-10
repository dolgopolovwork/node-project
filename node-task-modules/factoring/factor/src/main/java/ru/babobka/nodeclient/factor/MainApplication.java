package ru.babobka.nodeclient.factor;

import ru.babobka.nodeclient.Client;
import ru.babobka.nodeclient.ClientApplicationContainer;
import ru.babobka.nodeserials.NodeRequest;
import ru.babobka.nodeserials.NodeResponse;
import ru.babobka.nodeserials.enumerations.ResponseStatus;
import ru.babobka.nodeutils.container.Container;
import ru.babobka.nodeutils.time.Timer;

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
public class MainApplication {
    private static final String TASK_NAME = "ru.babobka.factor.task.EllipticCurveFactorTask";

    static {
        Container.getInstance().put(new ClientApplicationContainer());
    }

    public static void main(String[] args) {
        FactorData factorData;
        try {
            factorData = new FactorData(args);
        } catch (IllegalArgumentException e) {
            System.err.print(e.getMessage());
            return;
        }
        try (Client client = createClient(factorData.getHost(), factorData.getPort())) {
            Future<NodeResponse> future = client.executeTask(createFactorRequest(factorData.getNumber()));
            Timer timer = new Timer();
            NodeResponse response = future.get();
            if (response.getStatus() == ResponseStatus.NORMAL) {
                System.out.println("The result is " + response.getData() + ". " + timer.getTimePassed() + "mls passed.");
            } else {
                System.err.println("Can not get the result. The real data received is " + response);
            }
        } catch (IOException | InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

    private static Client createClient(String host, int port) {
        return new Client(host, port);
    }

    private static NodeRequest createFactorRequest(BigInteger number) {
        Map<String, Serializable> data = new HashMap<>();
        data.put("number", number);
        return NodeRequest.regular(UUID.randomUUID(), TASK_NAME, data);
    }
}
