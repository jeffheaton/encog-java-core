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
package org.encog.ml.bayesian.query.sample;

import java.io.Serializable;

import org.encog.Encog;
import org.encog.ml.bayesian.BayesianError;
import org.encog.ml.bayesian.BayesianEvent;
import org.encog.ml.bayesian.EventType;
import org.encog.util.Format;

/**
 * Holds the state of an event during a query. This allows the event to actually
 * hold a value, as well as an anticipated value (compareValue).
 * 
 */
public class EventState implements Serializable {

	/**
	 * Has this event been calculated yet?
	 */
	private boolean calculated;
	
	/**
	 * The current value of this event.
	 */
	private int value;
	
	/**
	 * The event that this state is connected to.
	 */
	private final BayesianEvent event;
	
	/**
	 * The type of event that this is for the query.
	 */
	private EventType eventType;
	
	/**
	 * The value that we are comparing to, for probability.
	 */
	private int compareValue;

	/**
	 * Construct an event state for the specified event.
	 * @param theEvent The event to create a state for.
	 */
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
	 * @param calculated
	 *            the calculated to set
	 */
	public void setCalculated(boolean calculated) {
		this.calculated = calculated;
	}

	/**
	 * @return the value
	 */
	public int getValue() {
		return value;
	}

	/**
	 * @param value
	 *            the value to set
	 */
	public void setValue(int value) {
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
	 * @param eventType
	 *            the eventType to set
	 */
	public void setEventType(EventType eventType) {
		this.eventType = eventType;
	}

	public void randomize(int... args) {
		setValue(event.getTable().generateRandom(args));
	}

	/**
	 * @return the compareValue
	 */
	public int getCompareValue() {
		return compareValue;
	}

	/**
	 * @param compareValue
	 *            the compareValue to set
	 */
	public void setCompareValue(int compareValue) {
		this.compareValue = compareValue;
	}

	public boolean isSatisfied() {
		if (eventType == EventType.Hidden) {
			throw new BayesianError(
					"Satisfy can't be called on a hidden event.");
		}
		return Math.abs(this.compareValue - this.value) < Encog.DEFAULT_DOUBLE_EQUAL;
	}

	/**
	 * {@inheritDoc}
	 */
	public String toString() {
		StringBuilder result = new StringBuilder();
		result.append("[EventState:event=");
		result.append(this.event.toString());
		result.append(",type=");
		result.append(this.eventType.toString());
		result.append(",value=");
		result.append(Format.formatDouble(this.value, 2));
		result.append(",compare=");
		result.append(Format.formatDouble(this.compareValue, 2));
		result.append(",calc=");
		result.append(this.calculated ? "y" : "n");
		result.append("]");
		return result.toString();
	}

	/**
	 * Convert a state to a simple string. (probability expression)
	 * @param state The state.
	 * @return A probability expression as a string.
	 */
	public static String toSimpleString(EventState state) {
		return BayesianEvent.formatEventName(state.getEvent(), state.getCompareValue());
	}
}
