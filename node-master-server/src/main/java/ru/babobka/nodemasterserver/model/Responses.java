package ru.babobka.nodemasterserver.model;

import lombok.NonNull;
import ru.babobka.nodemasterserver.listener.OnRaceStyleTaskIsReady;
import ru.babobka.nodemasterserver.listener.OnResponseListener;
import ru.babobka.nodemasterserver.listener.OnTaskIsReady;
import ru.babobka.nodeserials.NodeResponse;
import ru.babobka.nodeserials.enumerations.ResponseStatus;
import ru.babobka.nodetask.model.SubTask;
import ru.babobka.nodeutils.container.Container;
import ru.babobka.nodeutils.logger.NodeLogger;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Created by dolgopolov.a on 03.08.15.
 */
public class Responses {

    private static final int HOUR_MILLIS = 1000 * 60 * 60;
    private final NodeLogger nodeLogger = Container.getInstance().get(NodeLogger.class);
    private final OnResponseListener taskIsReadyListener = Container.getInstance().get(OnTaskIsReady.class);
    private final OnResponseListener raceStyleTaskIsReadyListener = Container.getInstance().get(OnRaceStyleTaskIsReady.class);
    private final int maxSize;
    private final List<NodeResponse> responsesList = new LinkedList<>();
    private final CountDownLatch countDownLatch;
    private final SubTask task;

    public Responses(int maxSize, @NonNull SubTask task) {
        if (maxSize < 1) {
            throw new IllegalArgumentException("maxSize must be at least 1");
        }
        this.maxSize = maxSize;
        this.countDownLatch = new CountDownLatch(maxSize);
        this.task = task;
    }

    synchronized boolean isComplete() {
        return responsesList.size() >= maxSize;
    }

    public synchronized boolean add(NodeResponse response) {
        if (isComplete() || alreadyHasResponse(response)) {
            return false;
        }
        responsesList.add(response);
        countDownLatch.countDown();
        if (isComplete()) {
            taskIsReadyListener.onResponse(response);
        } else if (task.isRaceStyle()
                && task.getDataValidators().isValidResponse(response)) {
            raceStyleTaskIsReadyListener.onResponse(response);
        }
        return true;

    }

    synchronized boolean alreadyHasResponse(NodeResponse response) {
        for (NodeResponse addedResponse : responsesList) {
            if (addedResponse.getId().equals(response.getId())) {
                return true;
            }
        }
        return false;
    }

    synchronized boolean fill(NodeResponse response) {
        if (isComplete()) {
            return false;
        }
        int elementsToAdd = maxSize - responsesList.size();
        for (int i = 0; i < elementsToAdd; i++) {
            responsesList.add(response);
            countDownLatch.countDown();
        }
        taskIsReadyListener.onResponse(response);
        return true;
    }

    synchronized void setStatus(ResponseStatus status) {
        for (NodeResponse response : responsesList) {
            response.setStatus(status);
        }
    }


    public List<NodeResponse> getResponseList() throws TimeoutException {
        return getResponseList(HOUR_MILLIS);
    }

    public synchronized boolean isStopped() {
        if (responsesList.isEmpty()) {
            return false;
        }
        for (NodeResponse response : responsesList) {
            if (response.getStatus() != ResponseStatus.STOPPED) {
                return false;
            }
        }
        return true;
    }

    public List<NodeResponse> getResponseList(long waitMillis) throws TimeoutException {
        if (waitMillis < 1) {
            throw new IllegalArgumentException("waitMillis must be at least 1");
        }
        List<NodeResponse> resultingResponses = new ArrayList<>();
        boolean completed = false;
        try {
            completed = countDownLatch.await(waitMillis, TimeUnit.MILLISECONDS);
            resultingResponses.addAll(this.responsesList);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            nodeLogger.error(e);
        }
        if (!completed && !Thread.currentThread().isInterrupted()) {
            throw new TimeoutException();
        }
        return resultingResponses;
    }


    @Override
    public String toString() {
        return responsesList.toString();
    }

}