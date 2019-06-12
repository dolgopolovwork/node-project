package ru.babobka.nodeclient.rpc;

import ru.babobka.nodeserials.NodeResponse;

import java.util.concurrent.CountDownLatch;

public class LockedResponse {
    private final CountDownLatch countDownLatch = new CountDownLatch(1);
    private NodeResponse response;

    public void unlock(NodeResponse response) {
        this.response = response;
        countDownLatch.countDown();
    }

    public NodeResponse getResponse() throws InterruptedException {
        countDownLatch.await();
        return response;
    }
}
