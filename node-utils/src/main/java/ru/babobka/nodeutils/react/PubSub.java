package ru.babobka.nodeutils.react;

import lombok.NonNull;
import org.apache.log4j.Logger;
import ru.babobka.nodeutils.func.Subscriber;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.function.Function;

/**
 * Created by 123 on 26.03.2019.
 */
public class PubSub<O> implements Observer<O> {

    private final Function<O, Boolean> DUMMY_FILTER = (item) -> true;
    private static final Logger logger = Logger.getLogger(PubSub.class);
    private final List<FilterableSubscriber<O>> mainThreadSubscribers = new CopyOnWriteArrayList<>();
    private final List<CustomThreadFilterableSubscriber<O>> customThreadSubscribers = new CopyOnWriteArrayList<>();
    private volatile boolean done;

    @Override
    public synchronized void publish(@NonNull O item) {
        if (!done) {
            callMainThreadSubscribers(item);
            callCustomThreadSubscribers(item);
        }
    }

    private void callMainThreadSubscribers(@NonNull O item) {
        mainThreadSubscribers.forEach(filterableSubscriber -> {
            try {
                if (!done && filterableSubscriber.filter.apply(item)) {
                    filterableSubscriber.subscriber.subscribe(item);
                }
            } catch (RuntimeException e) {
                onError(e);
            }
        });
    }

    private void callCustomThreadSubscribers(@NonNull O item) {
        customThreadSubscribers.forEach(filterableSubscriber -> {
            try {
                if (!done && filterableSubscriber.filter.apply(item)) {
                    filterableSubscriber.executorService.submit(() -> {
                        filterableSubscriber.subscriber.subscribe(item);
                    });
                }
            } catch (RuntimeException e) {
                onError(e);
            }

        });
    }

    protected void onError(@NonNull RuntimeException e) {
        //Log errors by default
        logger.error("observer error", e);
    }

    @Override
    public synchronized void subscribe(@NonNull Function<O, Boolean> filter, @NonNull Subscriber<O> subscriber) {
        if (done) {
            return;
        }
        mainThreadSubscribers.add(new FilterableSubscriber<>(subscriber, filter));
    }

    @Override
    public synchronized void subscribe(@NonNull Subscriber<O> subscriber) {
        subscribe(DUMMY_FILTER, subscriber);
    }

    @Override
    public synchronized void subscribe(@NonNull Function<O, Boolean> filter,
                                       @NonNull Subscriber<O> subscriber,
                                       @NonNull ExecutorService executorService) {
        if (done) {
            return;
        }
        customThreadSubscribers.add(new CustomThreadFilterableSubscriber<>(subscriber, filter, executorService));
    }

    @Override
    public synchronized void subscribe(@NonNull Subscriber<O> subscriber,
                                       @NonNull ExecutorService executorService) {
        subscribe(DUMMY_FILTER, subscriber, executorService);
    }

    @Override
    public synchronized void close() {
        if (done) {
            return;
        }
        done = true;
        customThreadSubscribers.forEach(customThreadSubscriber -> {
            customThreadSubscriber.executorService.shutdownNow();
        });
        customThreadSubscribers.clear();
        mainThreadSubscribers.clear();
    }

    private class FilterableSubscriber<O> {
        final Subscriber<O> subscriber;
        final Function<O, Boolean> filter;

        FilterableSubscriber(Subscriber<O> subscriber, Function<O, Boolean> filter) {
            this.subscriber = subscriber;
            this.filter = filter;
        }
    }

    private class CustomThreadFilterableSubscriber<O> extends FilterableSubscriber<O> {

        protected final ExecutorService executorService;

        CustomThreadFilterableSubscriber(Subscriber<O> subscriber, Function<O, Boolean> filter, ExecutorService executorService) {
            super(subscriber, filter);
            this.executorService = executorService;
        }
    }
}
