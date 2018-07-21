package ru.babobka.dlp.service.pollard.parallel.regular;

import ru.babobka.dlp.service.pollard.parallel.Distinguishable;
import ru.babobka.nodeutils.func.Pair;
import ru.babobka.dlp.model.regular.PollardEntity;
import ru.babobka.dlp.service.pollard.ClassicPollardFunction;
import ru.babobka.nodeutils.container.Container;
import ru.babobka.nodeutils.math.Fp;

import java.util.Map;

/**
 * Created by 123 on 16.01.2018.
 */
public abstract class ParallelCollisionService {
    private final Distinguishable distinguishable = Container.getInstance().get(Distinguishable.class);
    private final Map<Fp, PollardEntity> collisions;

    public ParallelCollisionService(Map<Fp, PollardEntity> collisions) {
        if (collisions == null) {
            throw new IllegalArgumentException("collisions is null");
        }
        this.collisions = collisions;
    }

    public Pair<PollardEntity> getCollision(Fp gen, Fp a, int loops) {
        int i = 0;
        while (!isDone() && i++ < loops) {
            PollardEntity collision = produceCollision(gen, a);
            if (collision == null) {
                continue;
            }
            PollardEntity probableCollision = collisions.get(collision.getCollision());
            if (collision.isCollision(probableCollision)) {
                done();
                return new Pair<>(collision, probableCollision);
            } else {
                collisions.put(collision.getCollision(), collision);
            }
        }
        return null;
    }

    private PollardEntity produceCollision(Fp gen, Fp a) {
        long maxAttempts = Integer.MAX_VALUE;
        if (gen.getMod().bitLength() < 32) {
            maxAttempts = gen.getMod().intValue();
        }
        ClassicPollardFunction pollardFunction = new ClassicPollardFunction(gen.getMod());
        PollardEntity result = pollardFunction.mix(PollardEntity.initRandom(a, gen));
        for (long attempt = 0; attempt < maxAttempts && !isDone(); attempt++) {
            result = pollardFunction.mix(result);
            if (distinguishable.isDistinguishable(result.getCollision())) {
                return result;
            }
        }
        return null;
    }

    public abstract boolean isDone();

    public abstract void done();
}
