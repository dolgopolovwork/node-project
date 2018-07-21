package ru.babobka.dlp.service.pollard.parallel.dist;

import ru.babobka.dlp.model.dist.DlpTaskDist;
import ru.babobka.dlp.model.dist.PollardDistResult;
import ru.babobka.dlp.model.regular.PollardEntity;
import ru.babobka.dlp.service.pollard.PollardEqualitySolver;
import ru.babobka.dlp.service.pollard.parallel.regular.ParallelCollisionService;
import ru.babobka.nodeutils.func.Pair;
import ru.babobka.nodeutils.math.Fp;
import ru.babobka.nodeutils.thread.ThreadPoolService;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by 123 on 06.07.2018.
 */
public class ParallelDistributedPollardDlpService extends ThreadPoolService<DlpTaskDist, PollardDistResult> {

    private final AtomicBoolean done = new AtomicBoolean();

    public ParallelDistributedPollardDlpService(int cores) {
        super(cores);
    }

    @Override
    public void stopImpl() {
        done.set(true);
    }

    @Override
    public PollardDistResult getStoppedResponse() {
        return PollardDistResult.empty();
    }

    @Override
    public PollardDistResult executeImpl(DlpTaskDist task) {
        if (task.getY().isMultNeutral()) {
            return PollardDistResult.result(BigInteger.ZERO);
        } else if (task.getY().equals(task.getGen())) {
            return PollardDistResult.result(BigInteger.ONE);
        }
        done.set(false);
        Map<Fp, PollardEntity> collisions = new HashMap<>();
        ParallelCollisionService collisionService = createCollisionService(collisions);
        List<PollardDistWalk> calls = PollardDistWalk.createCalls(collisionService, task, getCores());
        List<Future<Pair<PollardEntity>>> futures = submit(calls);
        Pair<PollardEntity> pollardCollision = null;
        for (Future<Pair<PollardEntity>> future : futures) {
            try {
                Pair<PollardEntity> futureCollision = future.get();
                if (futureCollision == null) {
                    continue;
                }
                pollardCollision = futureCollision;
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException expected) {
                //it's absolutely ok
            }
        }
        if (pollardCollision == null) {
            return PollardDistResult.collisions(collisions);
        }
        return PollardDistResult.result(PollardEqualitySolver.solve(task, pollardCollision));
    }

    private ParallelCollisionService createCollisionService(Map<Fp, PollardEntity> collisions) {
        if (collisions == null) {
            throw new IllegalArgumentException("no storage for collisions was set");
        }
        return new ParallelCollisionService(collisions) {
            @Override
            public boolean isDone() {
                return done.get();
            }

            @Override
            public void done() {
                stopImpl();
            }
        };
    }

    protected void setDone(boolean done) {
        this.done.set(done);
    }

}