package ru.babobka.dlp.service.pollard;

import lombok.NonNull;
import ru.babobka.dlp.model.PollardEntity;

/**
 * Created by 123 on 11.01.2018.
 */
public abstract class PollardFunction {

    public PollardEntity mix(@NonNull PollardEntity pollardEntity) {
        return mixImpl(pollardEntity);
    }

    protected abstract PollardEntity mixImpl(PollardEntity pollardEntity);
}
