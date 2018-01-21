package ru.babobka.dlp.collision.pollard;

import ru.babobka.dlp.DlpService;
import ru.babobka.dlp.DlpTask;
import ru.babobka.dlp.collision.Pair;
import ru.babobka.nodeutils.container.Container;

import java.math.BigInteger;

/**
 * Created by 123 on 12.01.2018.
 */
public class ClassicPollardDLPService implements DlpService {
    private final PollardCollisionService collisionService = Container.getInstance().get(PollardCollisionService.class);

    @Override
    public BigInteger dlp(DlpTask task) {
        if (task.getY().isMultNeutral()) {
            return BigInteger.ZERO;
        }
        Pair<PollardEntity> pollardCollision = collisionService.getCollision(task.getGen(), task.getY());
        return PollardEqualitySolver.solve(task, pollardCollision);
    }

}
