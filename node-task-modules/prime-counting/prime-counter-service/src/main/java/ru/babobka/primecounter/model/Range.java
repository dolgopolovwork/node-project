package ru.babobka.primecounter.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by dolgopolov.a on 07.07.15.
 */
public class Range implements Serializable {
    private static final long serialVersionUID = 3165688581178207146L;
    private long begin;
    private long end;

    private Range() {
    }

    public Range(long begin, long end) {
        if (begin < 0) {
            throw new IllegalArgumentException("begin cannot be negative");
        } else if (begin > end) {
            throw new IllegalArgumentException("begin is bigger than end");
        }
        this.begin = begin;
        this.end = end;
    }

    public static List<Range> getRanges(long begin, long end, int parts) {
        if (begin < 0) {
            throw new IllegalArgumentException("begin cannot be negative");
        } else if (begin > end) {
            throw new IllegalArgumentException("begin is bigger than end");
        } else if (parts <= 0) {
            throw new IllegalArgumentException("the must be at least one part of range");
        } else if (Math.abs(end - begin) < parts) {
            throw new IllegalArgumentException("cannot divide range [" + begin + ":" + end + "] into " + parts + " part(s)");
        }
        List<Range> ranges = new ArrayList<>(parts);
        long portion = (end - begin) / parts;
        for (int i = 0; i < parts; i++) {
            Range range = new Range();
            range.setBegin(begin);
            begin += portion;
            if (i == parts - 1) {
                range.setEnd(end);
            } else {
                range.setEnd(begin);
                begin++;
            }
            ranges.add(range);
        }

        return ranges;
    }

    public long getBegin() {
        return begin;
    }

    private void setBegin(long begin) {
        this.begin = begin;
    }

    public long getEnd() {
        return end;
    }

    private void setEnd(long end) {
        this.end = end;
    }

    @Override
    public String toString() {
        return "Range{" + "begin=" + begin + ", end=" + end + '}';
    }
}
