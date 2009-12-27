/*
 * Encog Artificial Intelligence Framework v2.x
 * Java Version
 * http://www.heatonresearch.com/encog/
 * http://code.google.com/p/encog-java/
 * 
 * Copyright 2008-2010, Heaton Research Inc., and individual contributors.
 * See the copyright.txt in the distribution for a full listing of 
 * individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

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
