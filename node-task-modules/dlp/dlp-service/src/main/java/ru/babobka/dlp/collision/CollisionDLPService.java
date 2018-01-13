package ru.babobka.dlp.collision;

import ru.babobka.dlp.DlpService;
import ru.babobka.dlp.DlpTask;
import ru.babobka.nodeutils.math.Fp;

import java.math.BigInteger;

/**
 * Created by 123 on 06.01.2018.
 */
public class CollisionDLPService implements DlpService {

    @Override
    public BigInteger dlp(DlpTask task) {
        for (int attempt = 0; attempt < 5; attempt++) {
            BigInteger dlp = dlpComputation(task.getGen(), task.getY());
            if (dlp != null) {
                return dlp;
            }
        }
        return null;
    }

    BigInteger dlpComputation(Fp gen, Fp y) {
        Pair<Long> collision = new CollisionStorage(gen, y).produceCollision();
        if (collision == null) {
            return null;
        }
        BigInteger exp = BigInteger.valueOf(collision.getFirst() - collision.getSecond());
        return exp.mod(gen.getMod().subtract(BigInteger.ONE));
    }

}
