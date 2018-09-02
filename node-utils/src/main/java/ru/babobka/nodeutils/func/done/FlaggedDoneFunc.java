package ru.babobka.nodeutils.func.done;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by 123 on 18.07.2018.
 */
public class FlaggedDoneFunc implements DoneFunc {
    private final AtomicBoolean atomicBoolean = new AtomicBoolean();

    @Override
    public boolean isDone() {
        return atomicBoolean.get();
    }

    @Override
    public void setDone() {
        atomicBoolean.set(true);
    }
}
