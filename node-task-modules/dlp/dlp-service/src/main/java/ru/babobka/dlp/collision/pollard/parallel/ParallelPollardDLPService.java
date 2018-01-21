package ru.babobka.dlp.collision.pollard.parallel;

import ru.babobka.dlp.DlpService;
import ru.babobka.dlp.DlpTask;
import ru.babobka.dlp.collision.Pair;
import ru.babobka.dlp.collision.pollard.PollardEntity;
import ru.babobka.dlp.collision.pollard.PollardEqualitySolver;
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
public class ParallelPollardDLPService extends ThreadPoolService<DlpTask, BigInteger> implements DlpService {

    private final AtomicBoolean done = new AtomicBoolean();

    public ParallelPollardDLPService(int cores) {
        super(cores);
    }

    //For testing purposes only. Don't forget to call stop().
    @Deprecated
    @Override
    public BigInteger dlp(DlpTask dlpTask) {
        return executeImpl(dlpTask);
    }

    @Override
    protected void stopImpl() {
        done.set(true);
    }

    @Override
    protected BigInteger executeImpl(DlpTask dlpTask) {
        if (dlpTask.getY().isMultNeutral()) {
            return BigInteger.ZERO;
        }
        ParallelCollisionService collisionService = createCollisionService(new ConcurrentHashMap<>());
        List<PollardWalk> calls = PollardWalk.createCalls(collisionService, dlpTask, getCores());
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
        return PollardEqualitySolver.solve(dlpTask, pollardCollision);
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

    //For testing purposes only
    @Deprecated
    public void resetDone() {
        done.set(false);
    }

}
