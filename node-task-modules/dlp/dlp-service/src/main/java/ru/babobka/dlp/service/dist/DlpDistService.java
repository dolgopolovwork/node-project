package ru.babobka.dlp.service.dist;

import ru.babobka.dlp.model.dist.DlpTaskDist;
import ru.babobka.dlp.model.dist.PollardDistResult;

import java.math.BigInteger;

/**
 * Created by 123 on 09.07.2018.
 */
public abstract class DlpDistService {

    public PollardDistResult dlp(DlpTaskDist task) {
        if (task == null) {
            throw new IllegalArgumentException("task is null");
        } else if (task.getY().isMultNeutral()) {
            return PollardDistResult.result(BigInteger.ZERO);
        } else if (task.getY().equals(task.getGen())) {
            return PollardDistResult.result(BigInteger.ONE);
        }
        return dlpImpl(task);
    }

    protected abstract PollardDistResult dlpImpl(DlpTaskDist task);
}
