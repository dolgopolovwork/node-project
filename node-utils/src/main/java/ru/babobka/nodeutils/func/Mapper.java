package ru.babobka.nodeutils.func;

/**
 * Created by 123 on 09.08.2017.
 */
public abstract class Mapper<F, T> {
    public T map(F entity) {
        if (entity == null) {
            throw new IllegalArgumentException("Can not map null value");
        }
        return mapImpl(entity);
    }

    protected abstract T mapImpl(F entity);

}
