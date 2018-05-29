package ru.babobka.nodeserials.data;

import java.util.Iterator;
import java.util.function.Consumer;

/**
 * Created by 123 on 28.05.2018.
 */
public class FinalIterator<T> implements Iterator<T> {
    private final Iterator<T> iterator;

    public FinalIterator(Iterator<T> iterator) {
        if (iterator == null) {
            throw new NullPointerException("iterator is null");
        }
        this.iterator = iterator;
    }

    @Override
    public boolean hasNext() {
        return iterator.hasNext();
    }

    @Override
    public T next() {
        return iterator.next();
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException("cannot remove from final iterator");
    }

    @Override
    public void forEachRemaining(Consumer<? super T> action) {
        iterator.forEachRemaining(action);
    }
}
