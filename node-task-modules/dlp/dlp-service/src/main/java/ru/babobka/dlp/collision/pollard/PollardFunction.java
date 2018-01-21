package ru.babobka.dlp.collision.pollard;

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
