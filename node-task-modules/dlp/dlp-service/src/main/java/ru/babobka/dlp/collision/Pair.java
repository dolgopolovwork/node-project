package ru.babobka.dlp.collision;

/**
 * Created by 123 on 06.01.2018.
 */
public class Pair<T> {
    private final T first;

    private final T second;

    public Pair(T first, T second) {
        this.first = first;
        this.second = second;
    }

    public T getFirst() {
        return first;
    }

    public T getSecond() {
        return second;
    }

    @Override
    public String toString() {
        return "Pair{" +
                "first=" + first +
                ", second=" + second +
                '}';
    }
}
