package ru.babobka.dlp.service.pollard.parallel.dist;

import ru.babobka.dlp.model.dist.DlpTaskDist;
import ru.babobka.dlp.service.pollard.parallel.regular.ParallelCollisionService;
import ru.babobka.nodeutils.func.Pair;
import ru.babobka.dlp.model.regular.PollardEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * Created by 123 on 06.07.2018.
 */
public class PollardDistWalk implements Callable<Pair<PollardEntity>> {

    private final ParallelCollisionService collisionService;
    private final DlpTaskDist dlpTask;

    static List<PollardDistWalk> createCalls(ParallelCollisionService collisionService, DlpTaskDist dlpTask, int calls) {
        if (calls < 1) {
            throw new IllegalArgumentException("there must be at least one call");
        }
        List<PollardDistWalk> walks = new ArrayList<>(calls);
        for (int i = 0; i < calls; i++) {
            walks.add(new PollardDistWalk(collisionService, dlpTask));
        }
        return walks;
    }

    public PollardDistWalk(ParallelCollisionService collisionService, DlpTaskDist dlpTask) {
        if (collisionService == null) {
            throw new IllegalArgumentException("collisionService is null");
        } else if (dlpTask == null) {
            throw new IllegalArgumentException("dlpTask is null");
        }
        this.collisionService = collisionService;
        this.dlpTask = dlpTask;
    }

    @Override
    public Pair<PollardEntity> call() {
        return collisionService.getCollision(dlpTask.getGen(), dlpTask.getY(), dlpTask.getLoops());
    }
}
