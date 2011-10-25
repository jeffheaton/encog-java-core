package org.encog.ml.bayesian.query;

import org.encog.Encog;
import org.encog.ml.bayesian.BayesianError;
import org.encog.ml.bayesian.BayesianEvent;
import org.encog.ml.bayesian.EventType;

public class EventState {
	
	private boolean calculated;
	private double value;
	private final BayesianEvent event;
	private EventType eventType;
	private double compareValue;
	
	public EventState(BayesianEvent theEvent) {
		this.event = theEvent;
		this.eventType = EventType.Hidden;
		calculated = false;
	}

	/**
	 * @return the calculated
	 */
	public boolean isCalculated() {
		return calculated;
	}

	/**
	 * @param calculated the calculated to set
	 */
	public void setCalculated(boolean calculated) {
		this.calculated = calculated;
	}

	/**
	 * @return the value
	 */
	public double getValue() {
		return value;
	}

	/**
	 * @param value the value to set
	 */
	public void setValue(double value) {
		this.calculated = true;
		this.value = value;
	}

	/**
	 * @return the event
	 */
	public BayesianEvent getEvent() {
		return event;
	}

	/**
	 * @return the eventType
	 */
	public EventType getEventType() {
		return eventType;
	}

	/**
	 * @param eventType the eventType to set
	 */
	public void setEventType(EventType eventType) {
		this.eventType = eventType;
	}

	public void randomize(double ... args) {
		setValue( event.getTable().generateRandom(args) );		
	}

	/**
	 * @return the compareValue
	 */
	public double getCompareValue() {
		return compareValue;
	}

	/**
	 * @param compareValue the compareValue to set
	 */
	public void setCompareValue(double compareValue) {
		this.compareValue = compareValue;
	}

	public boolean isSatisfied() {
		if( eventType==EventType.Hidden) {
			throw new BayesianError("Satisfy can't be called on a hidden event.");
		}
		return Math.abs(this.compareValue-this.value)<Encog.DEFAULT_DOUBLE_EQUAL;
	}
	
	
	
}
