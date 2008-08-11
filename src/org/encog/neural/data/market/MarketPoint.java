package org.encog.neural.data.market;

import java.util.Date;

import org.encog.neural.data.temporal.TemporalPoint;

public class MarketPoint extends TemporalPoint {

	private Date when;

	
	public MarketPoint(Date when, int size) {
		super(size);
		this.when = when;
	}


	/**
	 * @return the when
	 */
	public Date getWhen() {
		return when;
	}
	
	
	
}
