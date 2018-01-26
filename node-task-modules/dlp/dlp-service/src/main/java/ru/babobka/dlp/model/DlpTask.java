package ru.babobka.dlp.model;

import ru.babobka.nodeutils.math.Fp;

import java.io.Serializable;

/**
 * Created by 123 on 08.01.2018.
 */
public class DlpTask implements Serializable {
    private static final long serialVersionUID = -838274703492878533L;
    private final Fp gen;
    private final Fp y;

    public DlpTask(Fp gen, Fp y) {
        if (gen == null || y == null) {
            throw new IllegalArgumentException("Both generator and y must be non null");
        } else if (!gen.getMod().equals(y.getMod())) {
            throw new IllegalArgumentException("Generator and y are from different groups");
        } else if (y.equals(Fp.addNeutral(y.getMod()))) {
            throw new IllegalArgumentException("Can not get dlp for zero");
        }
        this.gen = gen;
        this.y = y;
    }

    public Fp getGen() {
        return gen;
    }

    public Fp getY() {
        return y;
    }

    @Override
    public String toString() {
        return "DlpTask{" +
                "gen=" + gen +
                ", y=" + y +
                '}';
    }
}
