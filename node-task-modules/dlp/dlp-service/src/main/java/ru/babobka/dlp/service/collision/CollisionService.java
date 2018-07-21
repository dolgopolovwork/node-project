package ru.babobka.dlp.service.collision;

import ru.babobka.dlp.model.dist.DlpTaskDist;
import ru.babobka.dlp.model.dist.PollardDistResult;
import ru.babobka.dlp.model.regular.DlpTask;
import ru.babobka.dlp.model.regular.PollardEntity;
import ru.babobka.dlp.service.dist.DlpDistService;
import ru.babobka.dlp.service.pollard.PollardEqualitySolver;
import ru.babobka.nodeutils.func.Pair;
import ru.babobka.nodeutils.math.Fp;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by 123 on 09.07.2018.
 */
public class CollisionService {

    public static BigInteger dlp(DlpTaskDist dlpTask, DlpDistService dlpService) {
        Map<Fp, PollardEntity> collisions = new HashMap<>();
        while (true) {
            PollardDistResult result = dlpService.dlp(dlpTask);
            if (result == null) {
                return null;
            }
            if (result.hasResult()) {
                return result.getExp();
            }
            Map<Fp, Pair<PollardEntity>> commonElements = getCommonCollisions(collisions, result.getCollisions());
            if (commonElements.isEmpty()) {
                collisions.putAll(result.getCollisions());
                continue;
            }
            BigInteger exp = getExpFromCollisions(commonElements, dlpTask);
            if (exp == null) {
                collisions.putAll(result.getCollisions());
                continue;
            }
            return exp;
        }
    }

    private static BigInteger getExpFromCollisions(Map<Fp, Pair<PollardEntity>> collisions, DlpTask dlpTask) {
        for (Pair<PollardEntity> pair : collisions.values()) {
            if (pair.getFirst().isCollision(pair.getSecond())) {
                return PollardEqualitySolver.solve(dlpTask, pair);
            }
        }
        return null;
    }

    private static Map<Fp, Pair<PollardEntity>> getCommonCollisions(Map<Fp, PollardEntity> collisions, Map<Fp, PollardEntity> newCollisions) {
        Map<Fp, Pair<PollardEntity>> commonCollisions = new HashMap<>();
        if (collisions.isEmpty() || newCollisions.isEmpty()) {
            return commonCollisions;
        }
        for (Map.Entry<Fp, PollardEntity> entry : newCollisions.entrySet()) {
            PollardEntity pollardEntity = collisions.get(entry.getKey());
            if (pollardEntity != null) {
                commonCollisions.put(entry.getKey(), new Pair<>(entry.getValue(), pollardEntity));
            }
        }
        return commonCollisions;
    }
}