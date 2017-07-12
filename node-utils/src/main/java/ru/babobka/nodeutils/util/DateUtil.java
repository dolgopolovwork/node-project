package ru.babobka.nodeutils.util;

import java.util.Calendar;

public interface DateUtil {

    static int getCurrentHour() {
        return (int) ((System.currentTimeMillis() / 1000 / 60 / 60) % 24);
    }

    static String getMonthYear() {
        Calendar calendar = Calendar.getInstance();
        return calendar.get(Calendar.YEAR) + "" + calendar.get(Calendar.MONTH);
    }

}
