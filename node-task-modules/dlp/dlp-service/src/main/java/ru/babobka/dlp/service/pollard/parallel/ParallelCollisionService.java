package ru.babobka.dlp.service.pollard.parallel;

import ru.babobka.dlp.model.Pair;
import ru.babobka.dlp.model.PollardEntity;
import ru.babobka.dlp.service.pollard.ClassicPollardFunction;
import ru.babobka.nodeutils.container.Container;
import ru.babobka.nodeutils.math.Fp;
import ru.babobka.nodeutils.util.ArrayUtil;

import java.util.Map;

/**
 * Created by 123 on 16.01.2018.
 */
public abstract class ParallelCollisionService {
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
        long maxAttempts = Integer.MAX_VALUE;
        if (gen.getMod().bitLength() < 32) {
            maxAttempts = gen.getMod().intValue();
        }
        ClassicPollardFunction pollardFunction = new ClassicPollardFunction(gen.getMod());
        PollardEntity result = pollardFunction.mix(PollardEntity.initRandom(a, gen));
        for (long attempt = 0; attempt < maxAttempts && !isDone(); attempt++) {
            result = pollardFunction.mix(result);
            if (distinguishable.isDistinguishable(result.getX())) {
                return result;
            }
        }
        return null;
    }

    public abstract boolean isDone();

    public abstract void done();
}
