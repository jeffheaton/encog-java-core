package org.encog.util.time;

public class EnglishTimeUnitNames implements TimeUnitNames {

	public String code(TimeUnit unit) {
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

	public String plural(TimeUnit unit) {
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

	public String singular(TimeUnit unit) {
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
