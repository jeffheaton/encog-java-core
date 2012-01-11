/*
 * Encog(tm) Core v3.1 - Java Version
 * http://www.heatonresearch.com/encog/
 * http://code.google.com/p/encog-java/
 
 * Copyright 2008-2012 Heaton Research, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *   
 * For more information on Heaton Research copyrights, licenses 
 * and trademarks visit:
 * http://www.heatonresearch.com/copyright
 */
package org.encog.util.time;

import java.util.Calendar;
import java.util.Date;

/**
 * Simple date utility class.
 * 
 * @author jheaton
 * 
 */
public final class DateUtil {

	/**
	 * Create a Date object with the specified date.
	 * 
	 * @param month
	 *            The month, starting with 1 for Jan.
	 * @param day
	 *            The day of the month.
	 * @param year
	 *            The year.
	 * @return The newly created Date.
	 */
	public static Date createDate(final int month, 
			final int day, final int year) {
		final Calendar cal = Calendar.getInstance();
		cal.set(Calendar.MONTH, month - 1);
		cal.set(Calendar.DAY_OF_MONTH, day);
		cal.set(Calendar.YEAR, year);
		cal.set(Calendar.HOUR, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		cal.set(Calendar.AM_PM, Calendar.AM);
		return cal.getTime();
	}

	/**
	 * Private constructor.
	 */
	private DateUtil() {

	}
}
