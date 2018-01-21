package ru.babobka.dlp.dummy;

import ru.babobka.dlp.DlpService;
import ru.babobka.dlp.DlpTask;
import ru.babobka.nodeutils.math.Fp;

import java.math.BigInteger;

/**
 * Created by 123 on 08.01.2018.
 */
public class DummyDlpService implements DlpService {
    @Override
    public BigInteger dlp(DlpTask task) {
        if (task.getY().isMultNeutral()) {
            return BigInteger.ZERO;
        }
        long mod = task.getGen().getMod().longValue();
        for (long exp = 1; exp < mod; exp++) {
            Fp guess = task.getGen().pow(exp);
            if (guess.equals(task.getY())) {
                return BigInteger.valueOf(exp);
            }
        }
        return null;
    }
}
