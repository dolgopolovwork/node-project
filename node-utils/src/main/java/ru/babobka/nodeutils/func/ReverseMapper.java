package ru.babobka.nodeutils.func;

/**
 * Created by 123 on 14.07.2018.
 */
public abstract class ReverseMapper<F, T> extends Mapper<F, T> {

    public F reverseMap(T entity) {
        if (entity == null) {
            throw new IllegalArgumentException("cannot map null value");
        }
        return reverseMapImpl(entity);
    }

    protected abstract F reverseMapImpl(T entity);
}
