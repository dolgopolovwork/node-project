package ru.babobka.dlp;

import ru.babobka.dlp.model.DlpTask;

import java.math.BigInteger;

/**
 * Created by 123 on 06.01.2018.
 */
public abstract class DlpService {

    public BigInteger dlp(DlpTask task) {
        if (task == null) {
            throw new IllegalArgumentException("task is null");
        } else if (task.getY().isMultNeutral()) {
            return BigInteger.ZERO;
        } else if (task.getY().equals(task.getGen())) {
            return BigInteger.ONE;
        }
        return dlpImpl(task);
    }

    protected abstract BigInteger dlpImpl(DlpTask task);
}
