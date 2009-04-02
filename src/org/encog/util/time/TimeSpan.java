/*
 * Encog Artificial Intelligence Framework v2.x
 * Java Version
 * http://www.heatonresearch.com/encog/
 * http://code.google.com/p/encog-java/
 * 
 * Copyright 2008-2009, Heaton Research Inc., and individual contributors.
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
	 * The logging object.
	 */
	@SuppressWarnings("unused")
	final private Logger logger = LoggerFactory.getLogger(this.getClass());
	
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
		return getSpanYears() / 100;
	}

	/**
	 * The time span in centuries.
	 * 
	 * @return The time span.
	 */
	private long getSpanDays() {
		return getSpanHours() / 24;
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
		return getSpanMinutes() / 60;
	}

	/**
	 * The time span in centuries.
	 * 
	 * @return The time span.
	 */

	private long getSpanMillennia() {
		return getSpanYears() / 1000;
	}

	/**
	 * The time span in centuries.
	 * 
	 * @return The time span.
	 */

	private long getSpanMinutes() {
		return getSpanSeconds() / 60;
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
				* 12;
	}

	/**
	 * The time span in centuries.
	 * 
	 * @return The time span.
	 */
	private long getSpanScores() {
		return getSpanYears() / 20;
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
		return (toCalendar.getTimeInMillis() 
				- fromCalendar.getTimeInMillis()) / 1000;

	}

	/**
	 * The time span in centuries.
	 * 
	 * @return The time span.
	 */
	private long getSpanWeeks() {
		return getSpanDays() / 7;
	}

	/**
	 * The time span in centuries.
	 * 
	 * @return The time span.
	 */
	private long getSpanYears() {
		return getSpanMonths() / 12;
	}

	/**
	 * @return the to date/time.
	 */
	public Date getTo() {
		return this.to;
	}
}
