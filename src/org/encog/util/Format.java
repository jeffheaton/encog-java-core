package org.encog.util;

import java.text.NumberFormat;

public class Format {

	public static final int SECONDS_INA_MINUTE = 60;
	public static final int SECONDS_INA_HOUR = SECONDS_INA_MINUTE * 60;
	public static final int SECONDS_INA_DAY = SECONDS_INA_HOUR * 24;
	
	
	public static String formatInteger(int i)
	{
		NumberFormat f = NumberFormat.getIntegerInstance();
		return f.format(i);
	}
	
	public static String formatPercentWhole(double e) {
		NumberFormat f = NumberFormat.getPercentInstance();
		return f.format(e);
	}

	public static String formatPercent(double e) {
		NumberFormat f = NumberFormat.getPercentInstance();
		f.setMinimumFractionDigits(6);
		return f.format(e);
	}
	
	public static String formatTimeSpan(int seconds)
	{
		int secondsCount = seconds;
		int days = seconds / Format.SECONDS_INA_DAY;
		secondsCount-=days * Format.SECONDS_INA_DAY;
		int hours = secondsCount / Format.SECONDS_INA_HOUR;
		secondsCount-=hours * Format.SECONDS_INA_HOUR;
		int minutes = secondsCount / Format.SECONDS_INA_MINUTE;
		secondsCount-= minutes * Format.SECONDS_INA_MINUTE;
		
		NumberFormat f = NumberFormat.getIntegerInstance();
		f.setMinimumIntegerDigits(2);
		f.setMaximumIntegerDigits(2);
		StringBuilder result = new StringBuilder();
		
		if( days>0 )
		{
			result.append(days);
			if( days>1)
				result.append(" days ");
			else
				result.append(" day ");
		}
		
		result.append(f.format(hours));
		result.append(':');
		result.append(f.format(minutes));
		result.append(':');
		result.append(f.format(secondsCount));
		
		return result.toString();
	}

	public static Object formatDouble(double d, int i) {
		NumberFormat f = NumberFormat.getNumberInstance();
		f.setMinimumFractionDigits(i);
		return f.format(d);	
	}
}
