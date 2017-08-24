package ru.babobka.primecounter.model;

import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Created by 123 on 21.10.2017.
 */
public class RangeTest {

    @Test(expected = IllegalArgumentException.class)
    public void testGetRangeArrayBadRange() {
        Range.getRanges(10, 9, 1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetRangeArrayBadParts() {
        Range.getRanges(0, 10, -1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetRangeArrayZeroParts() {
        Range.getRanges(0, 10, 0);
    }

    @Test
    public void testGetRangeArrayParts() {
        int end = 10;
        int begin = 0;
        for (int parts = 1; parts <= end; parts++) {
            List<Range> ranges = Range.getRanges(begin, end, 5);
            assertEquals(ranges.size(), 5);
        }
    }

    @Test
    public void testGetRangeArrayOnePart() {
        int parts = 1;
        int begin = 0;
        int end = 10;
        List<Range> ranges = Range.getRanges(begin, end, parts);
        assertEquals(ranges.size(), parts);
        assertEquals(ranges.get(0).getBegin(), begin);
        assertEquals(ranges.get(0).getEnd(), end);
    }

    @Test
    public void testGetRangeArrayBeginEnd() {
        int begin = 0;
        int end = 10;
        for (int parts = 1; parts < end; parts++) {
            List<Range> ranges = Range.getRanges(begin, end, parts);
            assertEquals(ranges.size(), parts);
            assertEquals(ranges.get(0).getBegin(), begin);
            assertEquals(ranges.get(ranges.size() - 1).getEnd(), end);
        }
    }

    @Test
    public void testGetRangeTwoParts() {
        int begin = 0;
        int end = 10;
        int parts = 2;
        List<Range> ranges = Range.getRanges(begin, end, parts);
        assertEquals(ranges.size(), parts);
        assertEquals(ranges.get(0).getBegin(), begin);
        assertEquals(ranges.get(0).getEnd(), 5);
        assertEquals(ranges.get(1).getBegin(), 6);
        assertEquals(ranges.get(1).getEnd(), end);
    }

    @Test
    public void testGetRangeThreeParts() {
        int begin = 0;
        int end = 10;
        int parts = 3;
        List<Range> ranges = Range.getRanges(begin, end, parts);
        assertEquals(ranges.size(), parts);
        assertEquals(ranges.get(0).getBegin(), begin);
        assertEquals(ranges.get(0).getEnd(), 3);
        assertEquals(ranges.get(1).getBegin(), 4);
        assertEquals(ranges.get(1).getEnd(), 7);
        assertEquals(ranges.get(2).getBegin(), 8);
        assertEquals(ranges.get(2).getEnd(), end);
    }

    @Test
    public void testGetRangeOneElementLeft() {
        int begin = 0;
        int end = 2;
        int parts = 2;
        List<Range> ranges = Range.getRanges(begin, end, parts);
        assertEquals(ranges.get(0).getBegin(), begin);
        assertEquals(ranges.get(0).getEnd(), 1);
        assertEquals(ranges.get(1).getBegin(), 2);
        assertEquals(ranges.get(1).getEnd(), 2);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetRangeTooManyParts() {
        int begin = 0;
        int end = 2;
        int parts = 3;
        Range.getRanges(begin, end, parts);
    }
}
