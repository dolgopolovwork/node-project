package ru.babobka.primecounter.model;

/**
 * Created by dolgopolov.a on 07.07.15.
 */
public class Range {

    private long begin;

    private long end;

    public long getBegin() {
        return begin;
    }

    public void setBegin(long begin) {
        this.begin = begin;
    }

    public long getEnd() {
        return end;
    }

    public void setEnd(long end) {
        this.end = end;
    }

    @Override
    public String toString() {
        return "Range{" +
                "begin=" + begin +
                ", end=" + end +
                '}';
    }
}
