package ru.babobka.dlp.service.pollard;

import ru.babobka.dlp.service.DlpService;
import ru.babobka.dlp.model.DlpTask;
import ru.babobka.dlp.model.Pair;
import ru.babobka.dlp.model.PollardEntity;
import ru.babobka.nodeutils.container.Container;

import java.math.BigInteger;

/**
 * Created by 123 on 12.01.2018.
 */
public class ClassicPollardDlpService extends DlpService {
    private final PollardCollisionService collisionService = Container.getInstance().get(PollardCollisionService.class);

    @Override
    protected BigInteger dlpImpl(DlpTask task) {
        Pair<PollardEntity> pollardCollision = collisionService.getCollision(task.getGen(), task.getY());
        return PollardEqualitySolver.solve(task, pollardCollision);
    }
}
