package ru.babobka.nodetester.network;

import ru.babobka.nodeutils.network.NodeConnectionImpl;

import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by 123 on 06.04.2018.
 */
public class LaggyNodeConnection extends NodeConnectionImpl {
    private final ThreadLocalRandom random = ThreadLocalRandom.current();
    private final long maxSleepMillis;

    public LaggyNodeConnection(Socket socket, long maxSleepMillis) {
        super(socket);
        if (maxSleepMillis < 0) {
            throw new IllegalArgumentException("maxSleepMillis must be positive");
        }
        this.maxSleepMillis = maxSleepMillis;
    }

    public LaggyNodeConnection(Socket socket) {
        this(socket, 500);
    }


    @Override
    public <T> T receive() throws IOException {
        randomLag();
        return super.receive();
    }

    @Override
    public void send(Object object) throws IOException {
        randomLag();
        super.send(object);
    }

    private void randomLag() {
        try {
            Thread.sleep(random.nextLong(maxSleepMillis));
        } catch (InterruptedException expected) {
            //that's ok
        }
    }
}
