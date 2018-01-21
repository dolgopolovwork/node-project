package ru.babobka.dlp.collision.pollard.parallel;

import ru.babobka.dlp.collision.Pair;
import ru.babobka.dlp.collision.pollard.ClassicPollardFunction;
import ru.babobka.dlp.collision.pollard.PollardEntity;
import ru.babobka.nodeutils.container.Container;
import ru.babobka.nodeutils.math.Fp;
import ru.babobka.nodeutils.util.ArrayUtil;

import java.util.Map;

/**
 * Created by 123 on 16.01.2018.
 */
public abstract class ParallelCollisionService {
    private static final int MAX_ATTEMPTS = 1_000_000;
    private final Distinguishable distinguishable = Container.getInstance().get(Distinguishable.class);
    private final Map<Fp, PollardEntity> collisions;

    public ParallelCollisionService(Map<Fp, PollardEntity> collisions) {
        ArrayUtil.validateNonNull(collisions);
        this.collisions = collisions;
    }

    public Pair<PollardEntity> getCollision(Fp gen, Fp a) {
        while (!isDone()) {
            PollardEntity collision = produceCollision(gen, a);
            if (collision == null) {
                continue;
            }
            PollardEntity probableCollision = collisions.get(collision.getX());
            if (collision.isCollision(probableCollision)) {
                done();
                return new Pair<>(collision, probableCollision);
            } else {
                collisions.put(collision.getX(), collision);
            }
        }
        return null;
    }

    private PollardEntity produceCollision(Fp gen, Fp a) {
        int maxAttempts = MAX_ATTEMPTS;
        if (a.getMod().bitLength() < 32) {
            maxAttempts = Math.abs(a.getMod().intValue());
        }
        ClassicPollardFunction pollardFunction = new ClassicPollardFunction(gen.getMod());
        PollardEntity singleResult = pollardFunction.mix(PollardEntity.initRandom(a, gen));
        for (int attempt = 0; attempt < maxAttempts && !isDone(); attempt++) {
            singleResult = pollardFunction.mix(singleResult);
            if (distinguishable.isDistinguishable(singleResult.getX())) {
                return singleResult;
            }
        }
        return null;
    }

    public abstract boolean isDone();

    public abstract void done();
}
