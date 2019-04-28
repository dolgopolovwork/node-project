package ru.babobka.nodeutils.react;

import ru.babobka.nodeutils.func.Subscriber;

import java.io.Closeable;
import java.util.concurrent.ExecutorService;
import java.util.function.Function;

/**
 * Created by 123 on 26.03.2019.
 */
public interface Observer<O>  extends Closeable {

    void publish(O item);

    void subscribe(Function<O, Boolean> filter, Subscriber<O> subscriber);

    void subscribe(Subscriber<O> subscriber);

    void subscribe(Function<O, Boolean> filter, Subscriber<O> subscriber, ExecutorService executorService);

    void subscribe(Subscriber<O> subscriber, ExecutorService executorService);
}
