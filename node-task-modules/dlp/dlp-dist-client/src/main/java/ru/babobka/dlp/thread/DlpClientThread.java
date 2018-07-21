package ru.babobka.dlp.thread;

import org.apache.http.client.fluent.Request;
import ru.babobka.dlp.ServerConfig;
import ru.babobka.dlp.listener.DlpDistResponseListener;
import ru.babobka.dlp.mapper.NodeRequestsListMapper;
import ru.babobka.dlp.mapper.PollardDistResultMapper;
import ru.babobka.dlp.model.dist.DlpTaskDist;
import ru.babobka.dlp.model.dist.PollardDistResult;
import ru.babobka.dlp.poison.PollardResultPoison;
import ru.babobka.dlp.task.dist.PollardDistDlpTask;
import ru.babobka.nodeclient.Client;
import ru.babobka.nodeserials.NodeRequest;
import ru.babobka.nodeserials.NodeResponse;
import ru.babobka.nodeutils.container.Container;
import ru.babobka.nodeutils.func.done.DoneFunc;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * Created by 123 on 15.07.2018.
 */
public class DlpClientThread extends Thread {
    private static final String TASK_NAME = PollardDistDlpTask.class.getCanonicalName();
    private final PollardDistResultMapper pollardDistResultMapper = Container.getInstance().get(PollardDistResultMapper.class);
    private final NodeRequestsListMapper nodeRequestsListMapper = Container.getInstance().get(NodeRequestsListMapper.class);
    private final DlpTaskDist dlpTaskDist;
    private final ServerConfig serverConfig;
    private final DoneFunc doneFunc;
    private final BlockingQueue<PollardDistResult> results;
    private final Client client;
    private volatile Future<List<NodeResponse>> currentFuture;

    public DlpClientThread(ServerConfig serverConfig,
                           DoneFunc doneFunc,
                           DlpTaskDist dlpTaskDist,
                           BlockingQueue<PollardDistResult> results) {
        this.serverConfig = serverConfig;
        this.doneFunc = doneFunc;
        this.results = results;
        this.dlpTaskDist = dlpTaskDist;
        client = new Client(serverConfig.getHost(), serverConfig.getClientPort());
    }

    @Override
    public void run() {
        try {
            while (!isEnough()) {
                List<NodeRequest> requests = nodeRequestsListMapper.map(dlpTaskDist, getNodes(serverConfig));
                currentFuture = client.executeTask(requests, createDlpResultListener(doneFunc, results));
                if (!isEnough()) {
                    currentFuture.get();
                }
            }
        } catch (IOException | InterruptedException | ExecutionException e) {
            if (!isEnough()) {
                e.printStackTrace();
            }
            stopResultQueue();
        } finally {
            client.close();
        }
    }


    private boolean isEnough() {
        return Thread.currentThread().isInterrupted() || doneFunc.isDone();
    }

    private int getNodes(ServerConfig serverConfig) throws IOException {
        String content = Request.Get("http://" + serverConfig.getHost() + ":" + serverConfig.getWebPort()
                + "/serverInfo?infoType=clusterSize&takName=" + TASK_NAME)
                .execute().returnContent().toString();
        return Integer.parseInt(content);
    }

    private DlpDistResponseListener createDlpResultListener(DoneFunc doneFunc,
                                                            BlockingQueue<PollardDistResult> results) {
        return new DlpDistResponseListener(results, doneFunc, pollardDistResultMapper);
    }

    @Override
    public void interrupt() {
        doneFunc.setDone();
        client.close();
        if (currentFuture != null) {
            currentFuture.cancel(true);
        }
        stopResultQueue();
        super.interrupt();
    }

    private void stopResultQueue() {
        try {
            results.put(PollardResultPoison.getInstance());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
