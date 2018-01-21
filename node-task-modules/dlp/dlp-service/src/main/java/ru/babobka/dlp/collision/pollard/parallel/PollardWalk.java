package ru.babobka.dlp.collision.pollard.parallel;

import ru.babobka.dlp.DlpTask;
import ru.babobka.dlp.collision.Pair;
import ru.babobka.dlp.collision.pollard.PollardEntity;
import ru.babobka.nodeutils.util.ArrayUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * Created by 123 on 20.01.2018.
 */
public class PollardWalk implements Callable<Pair<PollardEntity>> {

    private final ParallelCollisionService collisionService;

    private final DlpTask dlpTask;

    public static List<PollardWalk> createCalls(ParallelCollisionService collisionService, DlpTask dlpTask, int calls) {
        if (calls < 1) {
            throw new IllegalArgumentException("there must be at least one call");
        }
        List<PollardWalk> walks = new ArrayList<>(calls);
        for (int i = 0; i < calls; i++) {
            walks.add(new PollardWalk(collisionService, dlpTask));
        }
        return walks;
    }

    public PollardWalk(ParallelCollisionService collisionService, DlpTask dlpTask) {
        ArrayUtil.validateNonNull(dlpTask, collisionService);
        this.collisionService = collisionService;
        this.dlpTask = dlpTask;
    }

    @Override
    public Pair<PollardEntity> call() {
        return collisionService.getCollision(dlpTask.getGen(), dlpTask.getY());
    }
}