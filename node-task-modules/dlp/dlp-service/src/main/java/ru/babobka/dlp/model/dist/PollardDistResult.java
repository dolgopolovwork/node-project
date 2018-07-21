package ru.babobka.dlp.model.dist;

import ru.babobka.dlp.model.regular.PollardEntity;
import ru.babobka.nodeutils.math.Fp;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by 123 on 06.07.2018.
 */
public class PollardDistResult implements Serializable {

    private static final long serialVersionUID = 6575554385055577846L;
    //findbugs gets very mad about Map not being serializable
    private final HashMap<Fp, PollardEntity> collisions = new HashMap<>();
    private final BigInteger exp;

    protected PollardDistResult(Map<Fp, PollardEntity> collisions, BigInteger exp) {
        if (collisions != null) {
            this.collisions.putAll(collisions);
        }
        this.exp = exp;
    }

    public static PollardDistResult empty() {
        return new PollardDistResult(null, null);
    }

    public static PollardDistResult result(BigInteger exp) {
        if (exp == null) {
            throw new IllegalArgumentException("exp is null");
        }
        return new PollardDistResult(null, exp);
    }

    public static PollardDistResult collisions(Map<Fp, PollardEntity> collisions) {
        if (collisions == null) {
            throw new IllegalArgumentException("collisions is null");
        }
        return new PollardDistResult(collisions, null);
    }

    public Map<Fp, PollardEntity> getCollisions() {
        return collisions;
    }

    public BigInteger getExp() {
        return exp;
    }

    public boolean hasResult() {
        return exp != null;
    }
}
