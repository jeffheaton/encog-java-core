/*
 * Encog(tm) Artificial Intelligence Framework v2.3
 * Java Version
 * http://www.heatonresearch.com/encog/
 * http://code.google.com/p/encog-java/
 * 
 * Copyright 2008-2010 by Heaton Research Inc.
 * 
 * Released under the LGPL.
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
 * 
 * Encog and Heaton Research are Trademarks of Heaton Research, Inc.
 * For information on Heaton Research trademarks, visit:
 * 
 * http://www.heatonresearch.com/copyright.html
 */

package org.encog.util.time;

import java.util.Calendar;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A timespan between two Dates.
 * 
 * @author jheaton
 * 
 */
public class TimeSpan {

	/**
	 * Years in a century.
	 */
	public static final int YEARS_CENTURY = 100;

	/**
	 * Hours in a day.
	 */
	public static final int HOURS_DAY = 24;

	/**
	 * Minutes in an hour.
	 */
	public static final int MINUTES_HOUR = 60;

	/**
	 * Seconds in a minute.
	 */
	public static final int SECONDS_MINUTE = 60;

	/**
	 * Months in a year.
	 */
	public static final int MONTHS_YEAR = 12;

	/**
	 * Days in a week.
	 */
	public static final int DAYS_WEEK = 7;

	/**
	 * Years in a mil.
	 */
	public static final int YEARS_MIL = 1000;

	/**
	 * Years in a score.
	 */
	public static final int YEARS_SCORE = 20;

	/**
	 * The logging object.
	 */
	@SuppressWarnings("unused")
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	/**
	 * The from date.
	 */
	private final Date from;

	/**
	 * The to date.
	 */
	private final Date to;

	/**
	 * Construct a time span.
	 * 
	 * @param from
	 *            The from date/time.
	 * @param to
	 *            The two date/time.
	 */
	public TimeSpan(final Date from, final Date to) {
		this.from = from;
		this.to = to;
	}

	/**
	 * @return the from
	 */
	public Date getFrom() {
		return this.from;
	}

	/**
	 * Get the time span specified by the unit.
	 * 
	 * @param unit
	 *            The unit desired.
	 * @return The timespan in the specified unit.
	 */
	public long getSpan(final TimeUnit unit) {
		switch (unit) {
		case SECONDS:
			return getSpanSeconds();
		case MINUTES:
			return getSpanMinutes();
		case HOURS:
			return getSpanHours();
		case DAYS:
			return getSpanDays();
		case WEEKS:
			return getSpanWeeks();
		case FORTNIGHTS:
			return getSpanFortnights();
		case MONTHS:
			return getSpanMonths();
		case YEARS:
			return getSpanYears();
		case SCORES:
			return getSpanScores();
		case CENTURIES:
			return getSpanCenturies();
		case MILLENNIA:
			return getSpanMillennia();
		default:
			return 0;
		}

	}

	/**
	 * The time span in centuries.
	 * 
	 * @return The time span.
	 */
	private long getSpanCenturies() {
		return getSpanYears() / TimeSpan.YEARS_CENTURY;
	}

	/**
	 * The time span in centuries.
	 * 
	 * @return The time span.
	 */
	private long getSpanDays() {
		return getSpanHours() / TimeSpan.HOURS_DAY;
	}

	/**
	 * The time span in centuries.
	 * 
	 * @return The time span.
	 */

	private long getSpanFortnights() {
		return getSpanWeeks() / 2;
	}

	/**
	 * The time span in centuries.
	 * 
	 * @return The time span.
	 */
	private long getSpanHours() {
		return getSpanMinutes() / TimeSpan.MINUTES_HOUR;
	}

	/**
	 * The time span in centuries.
	 * 
	 * @return The time span.
	 */

	private long getSpanMillennia() {
		return getSpanYears() / TimeSpan.YEARS_MIL;
	}

	/**
	 * The time span in centuries.
	 * 
	 * @return The time span.
	 */

	private long getSpanMinutes() {
		return getSpanSeconds() / TimeSpan.SECONDS_MINUTE;
	}

	/**
	 * The time span in centuries.
	 * 
	 * @return The time span.
	 */

	private long getSpanMonths() {
		final Calendar startCal = Calendar.getInstance();
		startCal.setTime(this.from);

		final Calendar endCal = Calendar.getInstance();
		endCal.setTime(this.to);

		return endCal.get(Calendar.MONTH) - startCal.get(Calendar.MONTH)
				+ (endCal.get(Calendar.YEAR) - startCal.get(Calendar.YEAR))
				* TimeSpan.MONTHS_YEAR;
	}

	/**
	 * The time span in centuries.
	 * 
	 * @return The time span.
	 */
	private long getSpanScores() {
		return getSpanYears() / TimeSpan.YEARS_SCORE;
	}

	/**
	 * The time span in centuries.
	 * 
	 * @return The time span.
	 */
	private long getSpanSeconds() {
		final Calendar fromCalendar = Calendar.getInstance();
		final Calendar toCalendar = Calendar.getInstance();
		fromCalendar.setTime(this.from);
		toCalendar.setTime(this.to);
		return (toCalendar.getTimeInMillis() - fromCalendar.getTimeInMillis())
				/ TimeSpan.YEARS_MIL;

	}

	/**
	 * The time span in centuries.
	 * 
	 * @return The time span.
	 */
	private long getSpanWeeks() {
		return getSpanDays() / TimeSpan.DAYS_WEEK;
	}

	/**
	 * The time span in centuries.
	 * 
	 * @return The time span.
	 */
	private long getSpanYears() {
		return getSpanMonths() / TimeSpan.MONTHS_YEAR;
	}

	/**
	 * @return the to date/time.
	 */
	public Date getTo() {
		return this.to;
	}
}
