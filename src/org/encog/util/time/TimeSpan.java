package org.encog.util.time;

import java.util.Calendar;
import java.util.Date;

public class TimeSpan {
	
	private Date from;
	private Date to;
	
	public TimeSpan(Date from,Date to)
	{
		this.from = from;
		this.to = to;
	}

	/**
	 * @return the from
	 */
	public Date getFrom() {
		return from;
	}

	/**
	 * @return the to
	 */
	public Date getTo() {
		return to;
	}
	
	
	public long getSpan(TimeUnit unit)
	{
		switch(unit)
		{
			case SECONDS:return getSpanSeconds();
			case MINUTES:return getSpanMinutes();
			case HOURS:return getSpanHours();
			case DAYS:return getSpanDays();
			case WEEKS:return getSpanWeeks();
			case FORTNIGHTS:return getSpanFortnights();
			case MONTHS:return getSpanMonths();
			case YEARS:return getSpanYears();
			case SCORES:return getSpanScores();
			case CENTURIES:return getSpanCenturies();
			case MILLENNIA:return getSpanMillennia();
			default: return 0;
		}
		
	}
	
	private long getSpanSeconds()
	{
		Calendar fromCalendar = Calendar.getInstance();
		Calendar toCalendar = Calendar.getInstance();
		fromCalendar.setTime(this.from);
		toCalendar.setTime(this.to);
		return (toCalendar.getTimeInMillis()-fromCalendar.getTimeInMillis())/1000;
		
	}
	
	private long getSpanMinutes()
	{
		return getSpanSeconds()/60;
	}
	
	private long getSpanHours()
	{
		return getSpanMinutes()/60;
	}
	
	private long getSpanDays()
	{
		return getSpanHours()/24;
	}
	
	private long getSpanWeeks()
	{
		return getSpanDays()/7;
	}
	
	private long getSpanFortnights()
	{
		return getSpanWeeks()/2;
	}
	
	private long getSpanMonths()
	{
		Calendar startCal = Calendar.getInstance();
		startCal.setTime( this.from );
		 
		Calendar endCal = Calendar.getInstance();
		endCal.setTime( this.to );
		 
		return endCal.get( Calendar.MONTH ) - startCal.get( Calendar.MONTH ) + ( endCal.get( Calendar.YEAR ) - startCal.get( Calendar.YEAR ) ) * 12;
	}
	
	private long getSpanYears()
	{
		return getSpanMonths()/12;
	}
	
	private long getSpanScores()
	{
		return getSpanYears()/20;
	}
	
	private long getSpanCenturies()
	{
		return getSpanYears()/100;
	}
	
	private long getSpanMillennia()
	{
		return getSpanYears()/1000;
	}
}
