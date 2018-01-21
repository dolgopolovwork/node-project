package ru.babobka.dlp.collision.pollard;

import ru.babobka.dlp.DlpTask;
import ru.babobka.dlp.collision.Pair;
import ru.babobka.nodeutils.math.Fp;
import ru.babobka.nodeutils.util.MathUtil;

import java.math.BigInteger;

/**
 * Created by 123 on 20.01.2018.
 */
public class PollardEqualitySolver {

    public static BigInteger solve(DlpTask dlpTask, Pair<PollardEntity> pollardCollision) {
        if (pollardCollision == null) {
            throw new IllegalArgumentException("collision was not set");
        } else if (dlpTask == null) {
            throw new IllegalArgumentException("dlp task was not set");
        }
        Fp firstGenExp = pollardCollision.getFirst().getGenExp();
        Fp firstValueExp = pollardCollision.getFirst().getValExp();
        Fp secondGenExp = pollardCollision.getSecond().getGenExp();
        Fp secondValueExp = pollardCollision.getSecond().getValExp();
        Fp u = firstGenExp.subtract(secondGenExp);
        Fp v = secondValueExp.subtract(firstValueExp);
        Fp d = new Fp(v.getNumber().gcd(v.getMod()), v.getMod());
        if (d.getNumber().equals(BigInteger.ONE)) {
            return u.divide(v).getNumber();
        } else if (d.getNumber().equals(BigInteger.ZERO)) {
            System.out.println("Aw shit");
        }
        Fp w = new Fp(v.getMod().divide(d.getNumber()), v.getMod());
        Fp s = new Fp(MathUtil.eea(v.getNumber(), v.getMod()).getX(), v.getMod());
        Fp x = new Fp(s.getNumber().multiply(u.getNumber()).divide(d.getNumber()), u.getMod());
        while (!isAnswer(dlpTask.getGen(), dlpTask.getY(), x)) {
            x = x.add(w);
        }
        return x.getNumber();
    }

    private static boolean isAnswer(Fp gen, Fp y, Fp x) {
        return gen.pow(x.getNumber()).equals(y);
    }
}

