package ru.babobka.dlp.poison;

import ru.babobka.dlp.model.dist.PollardDistResult;
import ru.babobka.dlp.model.regular.PollardEntity;
import ru.babobka.nodeutils.math.Fp;

import java.math.BigInteger;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by 123 on 17.07.2018.
 */
public class PollardResultPoison extends PollardDistResult {
    private static final Map<Fp, PollardEntity> EMPTY_MAP = Collections.unmodifiableMap(new HashMap<>());
    private static final long serialVersionUID = -7430116730000446316L;

    private PollardResultPoison() {
        super(EMPTY_MAP, BigInteger.ONE);
    }

    private static class Holder {
        private static final PollardResultPoison instance = new PollardResultPoison();
    }

    public static PollardResultPoison getInstance() {
        return Holder.instance;
    }
}
