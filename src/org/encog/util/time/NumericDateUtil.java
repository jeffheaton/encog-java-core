package org.encog.util.time;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class NumericDateUtil {
    public static final int YEAR_OFFSET = 10000;
    public static final int MONTH_OFFSET = 100;

    public static final int HOUR_OFFSET = 10000;
    public static final int MINUTE_OFFSET = 100;

    public static long date2Long(Date time)
    {
    	GregorianCalendar gc = new GregorianCalendar();
    	gc.setTime(time);
    	int month = gc.get(Calendar.MONTH);
    	int day = gc.get(Calendar.DAY_OF_MONTH);
    	int year = gc.get(Calendar.YEAR);
        return (long)(day + (month * MONTH_OFFSET) + (year * YEAR_OFFSET));
    }

    public static Date long2Date(long l)
    {
        long rest = (long)l;
        int year = (int)(rest / YEAR_OFFSET);
        rest-=year*YEAR_OFFSET;
        int month = (int)(rest / MONTH_OFFSET);
        rest -= month * MONTH_OFFSET;
        int day = (int)rest;
        GregorianCalendar gc = new GregorianCalendar(year, month, day);
        return gc.getTime();
    }

    public static Date stripTime(Date time)
    {
    	GregorianCalendar gc = new GregorianCalendar();
    	gc.setTime(time);
    	int month = gc.get(Calendar.MONTH);
    	int day = gc.get(Calendar.DAY_OF_MONTH);
    	int year = gc.get(Calendar.YEAR);
    	GregorianCalendar gc2 = new GregorianCalendar(year,month,day);
    	return gc2.getTime();        
    }

    public static boolean haveSameDate(Date d1, Date d2)
    {
    	GregorianCalendar gc1 = new GregorianCalendar();    	
    	gc1.setTime(d1);
    	
    	GregorianCalendar gc2 = new GregorianCalendar();
    	gc2.setTime(d2);
    	
        return ((gc1.get(Calendar.DAY_OF_MONTH) == gc2.get(Calendar.DAY_OF_MONTH)) 
        		&& (gc1.get(Calendar.DAY_OF_MONTH) == gc2.get(Calendar.DAY_OF_MONTH)) 
        		&& (gc1.get(Calendar.YEAR) == gc2.get(Calendar.YEAR)));
    }

    public static Date int2Time(Date date, int i)
    {
        int rest = i;
        int hour = (int)(rest / HOUR_OFFSET);
        rest -= (hour * HOUR_OFFSET);
        int minute = (int)(rest / MONTH_OFFSET);
        rest -= (minute * MINUTE_OFFSET);
        int second = (int)rest;       
        
    	GregorianCalendar gc = new GregorianCalendar();
    	gc.setTime(date);
    	int month = gc.get(Calendar.MONTH);
    	int day = gc.get(Calendar.DAY_OF_MONTH);
    	int year = gc.get(Calendar.YEAR);
        
        return new GregorianCalendar(year, month, day, hour, minute, second).getTime();
    }

    public static int time2Int(Date time)
    {
    	GregorianCalendar gc = new GregorianCalendar();
    	gc.setTime(time);
    	int hour = gc.get(Calendar.HOUR);
    	int minute = gc.get(Calendar.MINUTE);
    	int second = gc.get(Calendar.SECOND);
        return (int)(second + (minute * MINUTE_OFFSET) + (hour * HOUR_OFFSET));
    }

    public static int getYear(long date)
    {
        return (int) (date / YEAR_OFFSET);
    }

    public static int getMonth(long l)
    {
        long rest = (long)l;
        int year = (int)(rest / YEAR_OFFSET);
        rest -= year * YEAR_OFFSET;
        return (int)(rest / MONTH_OFFSET);
    }

    public static int getMinutePeriod(int time, int period)
    {
        int rest = time;
        int hour = (int)(rest / HOUR_OFFSET);
        rest -= (hour * HOUR_OFFSET);
        int minute = (int)(rest / MONTH_OFFSET);

        int minutes = minute + (hour * 60);
        return minutes / period;

    }

    public static long combine(long date, int time)
    {
        return (date * 1000000) + time;
    }

    public static int GetDayOfWeek(long p)
    {
        Date t = long2Date(p);
        GregorianCalendar gc = new GregorianCalendar();
        gc.setTime(t);
        
        switch (gc.get(Calendar.DAY_OF_WEEK))
        {
            case Calendar.SUNDAY:
                return 0;
            case Calendar.MONDAY:
                return 1;
            case Calendar.TUESDAY:
                return 2;
            case Calendar.WEDNESDAY:
                return 3;
            case Calendar.THURSDAY:
                return 4;
            case Calendar.FRIDAY:
                return 5;
            case Calendar.SATURDAY:
                return 6;
            default:
                // no way this should happen!
                return -1;
        }
    }

}
