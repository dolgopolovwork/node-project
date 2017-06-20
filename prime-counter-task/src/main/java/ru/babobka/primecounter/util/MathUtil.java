package ru.babobka.primecounter.util;

import ru.babobka.primecounter.model.Range;

/**
 * Created by dolgopolov.a on 06.07.15.
 */
public interface MathUtil {

    static Range[] getRangeArray(long begin, long end, int parts) {
        Range[] ranges = new Range[parts];
        long portion = (end - begin) / parts;
        Range tempRange;
        for (int i = 0; i < parts; i++) {
            tempRange = new Range();
            tempRange.setBegin(begin);
            begin += portion;
            if (i == parts - 1) {
                tempRange.setEnd(end);
            } else {
                tempRange.setEnd(begin);
                begin++;
            }
            ranges[i] = tempRange;
        }

        return ranges;
    }


}
