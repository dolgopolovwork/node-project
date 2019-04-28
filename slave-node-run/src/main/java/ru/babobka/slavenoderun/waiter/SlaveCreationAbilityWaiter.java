package ru.babobka.slavenoderun.waiter;

import ru.babobka.nodeutils.waiter.Waiter;

import java.util.concurrent.atomic.AtomicBoolean;

public class SlaveCreationAbilityWaiter implements Waiter {
    private final AtomicBoolean able = new AtomicBoolean();

    @Override
    public void waitUntilAble() {
        // Busy waiting
        while (!able.get()) {
            waitSecond();
        }
    }

    @Override
    public void able() {
        able.set(true);
    }

    @Override
    public void disable() {
        able.set(false);
    }

    private void waitSecond() {
        try {
            Thread.sleep(1_000);
        } catch (InterruptedException ignored) {

        }
    }
}
