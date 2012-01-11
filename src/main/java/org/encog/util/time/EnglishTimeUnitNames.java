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

/**
 * Class used to get the English names for TimeUnits.
 * 
 * @author jheaton
 * 
 */
public class EnglishTimeUnitNames implements TimeUnitNames {


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
