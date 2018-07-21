package ru.babobka.dlp.model.dist;

import ru.babobka.dlp.model.regular.DlpTask;
import ru.babobka.nodeutils.math.Fp;
import ru.babobka.nodeutils.util.MathUtil;

/**
 * Created by 123 on 06.07.2018.
 */
public class DlpTaskDist extends DlpTask {
    private static final long serialVersionUID = -956035949479142235L;
    private static final int MIN_LOOPS = 10;
    private final int loops;

    public DlpTaskDist(Fp gen, Fp y, int loops) {
        super(gen, y);
        if (loops < 1) {
            throw new IllegalArgumentException("there must be at least one loop");
        }
        this.loops = loops;
    }

    public DlpTaskDist(Fp gen, Fp y) {
        super(gen, y);
        if (gen.getNumber().bitLength() > 62) {
            loops = Integer.MAX_VALUE;
        } else {
            loops = Math.max(MathUtil.sqrtBig(gen.getMod()).intValue() / 10, MIN_LOOPS);
        }
    }

    public int getLoops() {
        return loops;
    }
}
