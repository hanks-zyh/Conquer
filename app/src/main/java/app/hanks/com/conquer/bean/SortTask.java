/*
 * Created by Hanks
 * Copyright (c) 2015 Nashangban. All rights reserved
 *
 */
package app.hanks.com.conquer.bean;

import java.util.Calendar;

/**
 * 为Task排序
 * Created by Hanks on 2015/6/10.
 */
public class SortTask implements java.util.Comparator<Task> {

    @Override
    public int compare(Task lhs, Task rhs) {
        if (lhs.getRepeat() == 1) {
            long time = lhs.getTime();

            Calendar today = Calendar.getInstance();
            today.setTimeInMillis(System.currentTimeMillis());

            Calendar c = Calendar.getInstance();
            c.setTimeInMillis(time);
            c.set(Calendar.YEAR, today.get(Calendar.YEAR));
            c.set(Calendar.MONTH, today.get(Calendar.MONTH));
            c.set(Calendar.DAY_OF_MONTH, today.get(Calendar.DAY_OF_MONTH));

            lhs.setTime(c.getTimeInMillis());
        }
        if (rhs.getRepeat() == 1) {
            long time = rhs.getTime();

            Calendar today = Calendar.getInstance();
            today.setTimeInMillis(System.currentTimeMillis());

            Calendar c = Calendar.getInstance();
            c.setTimeInMillis(time);
            c.set(Calendar.YEAR, today.get(Calendar.YEAR));
            c.set(Calendar.MONTH, today.get(Calendar.MONTH));
            c.set(Calendar.DAY_OF_MONTH, today.get(Calendar.DAY_OF_MONTH));
            rhs.setTime(c.getTimeInMillis());
        }
        return (int) (lhs.getTime() - rhs.getTime());
    }
}
