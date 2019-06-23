package ru.babobka.nodeclient.rpc;

import ru.babobka.nodeserials.NodeResponse;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class LockedResponse {
    private final CountDownLatch countDownLatch = new CountDownLatch(1);
    private NodeResponse response;

    public void unlock(NodeResponse response) {
        this.response = response;
        countDownLatch.countDown();
    }

    public NodeResponse getResponse() throws InterruptedException, TimeoutException {
        return getResponse(Long.MAX_VALUE);
    }

    public NodeResponse getResponse(long timeoutMillis) throws InterruptedException, TimeoutException {
        if (timeoutMillis < 0) {
            throw new IllegalArgumentException("Cannot wait negative range of time");
        }
        if (!countDownLatch.await(timeoutMillis, TimeUnit.MILLISECONDS)) {
            throw new TimeoutException("Cannot get response");
        }
        return response;
    }
}
