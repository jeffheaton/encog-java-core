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
package org.encog.util.time;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class used to get the English names for TimeUnits.
 * 
 * @author jheaton
 * 
 */
public class EnglishTimeUnitNames implements TimeUnitNames {

	/**
	 * The logging object.
	 */
	@SuppressWarnings("unused")
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	/**
	 * Get the code for a TimeUnit.
	 * 
	 * @param unit
	 *            The time unit.
	 * @return The code for the specified time unit.
	 */
	public String code(final TimeUnit unit) {
		switch (unit) {
		case SECONDS:
			return "sec";
		case MINUTES:
			return "min";
		case HOURS:
			return "hr";
		case DAYS:
			return "d";
		case WEEKS:
			return "w";
		case FORTNIGHTS:
			return "fn";
		case MONTHS:
			return "m";
		case YEARS:
			return "y";
		case DECADES:
			return "dec";
		case SCORES:
			return "sc";
		case CENTURIES:
			return "c";
		case MILLENNIA:
			return "m";
		default:
			return "unk";
		}
	}

	/**
	 * Get the plural form for a TimeUnit.
	 * 
	 * @param unit
	 *            The time unit.
	 * @return The plural form for the specified time unit.
	 */
	public String plural(final TimeUnit unit) {
		switch (unit) {
		case SECONDS:
			return "seconds";
		case MINUTES:
			return "minutes";
		case HOURS:
			return "hours";
		case DAYS:
			return "days";
		case WEEKS:
			return "weeks";
		case FORTNIGHTS:
			return "fortnights";
		case MONTHS:
			return "months";
		case YEARS:
			return "years";
		case DECADES:
			return "decades";
		case SCORES:
			return "scores";
		case CENTURIES:
			return "centures";
		case MILLENNIA:
			return "millennia";
		default:
			return "unknowns";
		}
	}

	/**
	 * Get the singular form for a TimeUnit.
	 * 
	 * @param unit
	 *            The time unit.
	 * @return The singular form for the specified time unit.
	 */
	public String singular(final TimeUnit unit) {
		switch (unit) {
		case SECONDS:
			return "second";
		case MINUTES:
			return "minute";
		case HOURS:
			return "hour";
		case DAYS:
			return "day";
		case WEEKS:
			return "week";
		case FORTNIGHTS:
			return "fortnight";
		case MONTHS:
			return "month";
		case YEARS:
			return "year";
		case DECADES:
			return "decade";
		case SCORES:
			return "score";
		case CENTURIES:
			return "century";
		case MILLENNIA:
			return "millenium";
		default:
			return "unknown";
		}
	}

}
