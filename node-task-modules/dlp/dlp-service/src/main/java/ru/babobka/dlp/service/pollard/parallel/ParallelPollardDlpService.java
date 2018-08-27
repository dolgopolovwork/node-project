package ru.babobka.dlp.service.pollard.parallel;

import ru.babobka.dlp.model.DlpTask;
import ru.babobka.dlp.model.Pair;
import ru.babobka.dlp.model.PollardEntity;
import ru.babobka.dlp.service.pollard.PollardEqualitySolver;
import ru.babobka.nodeutils.math.Fp;
import ru.babobka.nodeutils.thread.ThreadPoolService;

import java.math.BigInteger;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by 123 on 16.01.2018.
 */
public class ParallelPollardDlpService extends ThreadPoolService<DlpTask, BigInteger> {

    private final AtomicBoolean done = new AtomicBoolean();

    public ParallelPollardDlpService(int cores) {
        super(cores);
    }

    @Override
    protected void stopImpl() {
        done.set(true);
    }

    @Override
    protected BigInteger getStoppedResponse() {
        return BigInteger.ONE;
    }

    @Override
    protected BigInteger executeImpl(DlpTask task) {
        if (task.getY().isMultNeutral()) {
            return BigInteger.ZERO;
        } else if (task.getY().equals(task.getGen())) {
            return BigInteger.ONE;
        }
        done.set(false);
        ParallelCollisionService collisionService = createCollisionService(new ConcurrentHashMap<>());
        List<PollardWalk> calls = PollardWalk.createCalls(collisionService, task, getCores());
        List<Future<Pair<PollardEntity>>> futures = submit(calls);
        Pair<PollardEntity> pollardCollision = null;
        for (Future<Pair<PollardEntity>> future : futures) {
            try {
                Pair<PollardEntity> futureCollision = future.get();
                if (futureCollision == null) {
                    continue;
                }
                pollardCollision = futureCollision;
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }
        if (pollardCollision == null) {
            return null;
        }
        return PollardEqualitySolver.solve(task, pollardCollision);
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
