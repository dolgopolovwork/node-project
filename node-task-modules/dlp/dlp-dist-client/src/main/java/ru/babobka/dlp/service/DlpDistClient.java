package ru.babobka.dlp.service;

import ru.babobka.dlp.ServerConfig;
import ru.babobka.dlp.model.dist.DlpTaskDist;
import ru.babobka.dlp.model.dist.PollardDistResult;
import ru.babobka.dlp.poison.PollardResultPoison;
import ru.babobka.dlp.service.collision.CollisionService;
import ru.babobka.dlp.service.dist.DlpDistService;
import ru.babobka.dlp.thread.DlpClientThread;
import ru.babobka.nodeutils.func.done.DoneFunc;
import ru.babobka.nodeutils.func.done.FlaggedDoneFunc;

import java.math.BigInteger;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Created by 123 on 15.07.2018.
 */
public class DlpDistClient extends Thread {

    private final DlpTaskDist dlpTaskDist;
    private final Thread dlpClientThread;
    private final BlockingQueue<PollardDistResult> results = new LinkedBlockingQueue<>();
    private final CountDownLatch resultLatch = new CountDownLatch(1);
    private final AtomicReference<BigInteger> result = new AtomicReference<>();

    public DlpDistClient(ServerConfig serverConfig,
                         DlpTaskDist dlpTaskDist) {
        if (serverConfig == null) {
            throw new IllegalArgumentException("serverConfig was not set");
        } else if (dlpTaskDist == null) {
            throw new IllegalArgumentException("dlpTaskDist is null");
        }
        this.dlpTaskDist = dlpTaskDist;
        DoneFunc doneFunc = new FlaggedDoneFunc();
        this.dlpClientThread = new DlpClientThread(
                serverConfig,
                doneFunc,
                dlpTaskDist,
                results);
    }

    @Override
    public void run() {
        dlpClientThread.start();
        BigInteger exp = CollisionService.dlp(dlpTaskDist, new DlpDistService() {
            @Override
            protected PollardDistResult dlpImpl(DlpTaskDist task) {
                try {
                    PollardDistResult result = results.take();
                    if (result instanceof PollardResultPoison) {
                        return null;
                    }
                    return result;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return null;
            }
        });
        result.set(exp);
        resultLatch.countDown();
    }

    public BigInteger getResult() throws InterruptedException {
        resultLatch.await();
        return result.get();
    }

    @Override
    public void interrupt() {
        resultLatch.countDown();
        dlpClientThread.interrupt();
        try {
            results.put(PollardResultPoison.getInstance());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        super.interrupt();
    }
}
