package ru.babobka.nodeutils.func;

/**
 * Created by 123 on 01.09.2017.
 */
public abstract class Applyer<O> {

    public void apply(O object) {
        if (object == null) {
            throw new IllegalArgumentException("cannot apply to null object");
        }
        applyImpl(object);
    }

    protected abstract void applyImpl(O object);
}
