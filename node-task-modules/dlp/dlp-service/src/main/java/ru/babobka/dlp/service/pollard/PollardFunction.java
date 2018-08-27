package ru.babobka.dlp.service.pollard;

import ru.babobka.dlp.model.PollardEntity;

/**
 * Created by 123 on 11.01.2018.
 */
public abstract class PollardFunction {

    public PollardEntity mix(PollardEntity pollardEntity) {
        if (pollardEntity == null) {
            throw new IllegalArgumentException("pollardEntity is null");
        }
        return mixImpl(pollardEntity);
    }

    protected abstract PollardEntity mixImpl(PollardEntity pollardEntity);
}
