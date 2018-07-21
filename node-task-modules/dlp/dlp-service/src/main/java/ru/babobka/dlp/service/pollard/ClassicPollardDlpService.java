package ru.babobka.dlp.service.pollard;

import ru.babobka.dlp.model.regular.DlpTask;
import ru.babobka.nodeutils.func.Pair;
import ru.babobka.dlp.model.regular.PollardEntity;
import ru.babobka.dlp.service.regular.DlpService;
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
