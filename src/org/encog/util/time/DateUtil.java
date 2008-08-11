package org.encog.util.time;

import java.util.Calendar;
import java.util.Date;

public class DateUtil {
	/**
	 * January is 1.
	 * @param month
	 * @param day
	 * @param year
	 * @return
	 */
	public static Date createDate(int month,int day,int year)
	{
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.MONTH, month-1);
		cal.set(Calendar.DAY_OF_MONTH, day);
		cal.set(Calendar.YEAR, year);
		return cal.getTime();
	}
}
