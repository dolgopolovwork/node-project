package ru.babobka.dlp.collision.pollard;

import ru.babobka.dlp.collision.Pair;
import ru.babobka.nodeutils.math.Fp;

/**
 * Created by 123 on 11.01.2018.
 */
public class PollardCollisionService {

    public Pair<PollardEntity> getCollision(Fp gen, Fp a) {
        ClassicPollardFunction pollardFunction = new ClassicPollardFunction(gen.getMod());
        PollardEntity startPoint = PollardEntity.initRandom(a, gen);
        PollardEntity singleResult = pollardFunction.mix(startPoint);
        PollardEntity doubleResult = pollardFunction.doubleMix(startPoint);
        while (!isCollision(singleResult, doubleResult)) {
            singleResult = pollardFunction.mix(singleResult);
            doubleResult = pollardFunction.doubleMix(doubleResult);
            if (singleResult.equals(doubleResult)) {
                startPoint = PollardEntity.initRandom(a, gen);
                singleResult = pollardFunction.mix(startPoint);
                doubleResult = pollardFunction.doubleMix(startPoint);
            }
        }
        return new Pair<>(singleResult, doubleResult);
    }

    private boolean isCollision(PollardEntity singleResult, PollardEntity doubleResult) {
        return singleResult.getX().equals(doubleResult.getX()) && !singleResult.equals(doubleResult);
    }
}