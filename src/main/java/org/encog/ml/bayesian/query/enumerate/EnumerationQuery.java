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
package org.encog.ml.bayesian.query.enumerate;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.encog.Encog;
import org.encog.ml.bayesian.BayesianError;
import org.encog.ml.bayesian.BayesianEvent;
import org.encog.ml.bayesian.BayesianNetwork;
import org.encog.ml.bayesian.EventType;
import org.encog.ml.bayesian.query.BasicQuery;
import org.encog.ml.bayesian.query.sample.EventState;
import org.encog.ml.bayesian.table.TableLine;
import org.encog.util.Format;

/**
 * An enumeration query allows probabilistic queries on a Bayesian network.
 * Enumeration works by calculating every combination of hidden nodes and using
 * total probability. This results in an accurate deterministic probability.
 * However, enumeration can be slow for large Bayesian networks. For a quick
 * estimate of probability the sampling query can be used.
 * 
 */
public class EnumerationQuery extends BasicQuery implements Serializable {

	/**
	 * The events that we will enumerate over.
	 */
	private List<EventState> enumerationEvents = new ArrayList<EventState>();

	/**
	 * The calculated probability.
	 */
	private double probability;

	/**
	 * Construct the enumeration query.
	 * 
	 * @param theNetwork
	 *            The Bayesian network to query.
	 */
	public EnumerationQuery(BayesianNetwork theNetwork) {
		super(theNetwork);
	}
	
	/**
	 * Default constructor.
	 */
	public EnumerationQuery() {
		
	}

	/**
	 * Reset the enumeration events. Always reset the hidden events. Optionally
	 * reset the evidence and outcome.
	 * 
	 * @param includeEvidence
	 *            True if the evidence is to be reset.
	 * @param includeOutcome
	 *            True if the outcome is to be reset.
	 */
	public void resetEnumeration(boolean includeEvidence, boolean includeOutcome) {
		this.enumerationEvents.clear();

		for (EventState state : this.getEvents().values()) {
			if (state.getEventType() == EventType.Hidden) {
				this.enumerationEvents.add(state);
				state.setValue(0);
			} else if (includeEvidence
					&& state.getEventType() == EventType.Evidence) {
				this.enumerationEvents.add(state);
				state.setValue(0);
			} else if (includeOutcome
					&& state.getEventType() == EventType.Outcome) {
				this.enumerationEvents.add(state);
				state.setValue(0);
			} else {
				state.setValue(state.getCompareValue());
			}
		}
	}

	/**
	 * Roll the enumeration events forward by one.
	 * 
	 * @return False if there are no more values to roll into, which means we're
	 *         done.
	 */
	public boolean forward() {
		int currentIndex = 0;
		boolean done = false;
		boolean eof = false;
		
		if( this.enumerationEvents.size() == 0 ) {
			done = true;
			eof = true;
		}

		while (!done) {

			EventState state = this.enumerationEvents.get(currentIndex);
			int v = (int) state.getValue();
			v++;
			if (v >= state.getEvent().getChoices().size()) {
				state.setValue(0);
			} else {
				state.setValue(v);
				done = true;
				break;
			}

			currentIndex++;

			if (currentIndex >= this.enumerationEvents.size()) {
				done = true;
				eof = true;
			}
		}

		return !eof;
	}

	/**
	 * Obtain the arguments for an event.
	 * @param event The event.
	 * @return The arguments.
	 */
	private int[] obtainArgs(BayesianEvent event) {
		int[] result = new int[event.getParents().size()];

		int index = 0;
		for (BayesianEvent parentEvent : event.getParents()) {
			EventState state = this.getEventState(parentEvent);
			result[index++] = state.getValue();

		}
		return result;
	}

	/**
	 * Calculate the probability for a state.
	 * @param state The state to calculate.
	 * @return The probability.
	 */
	private double calculateProbability(EventState state) {

		int[] args = obtainArgs(state.getEvent());

		for (TableLine line : state.getEvent().getTable().getLines()) {
			if (line.compareArgs(args)) {
				if (Math.abs(line.getResult() - state.getValue()) < Encog.DEFAULT_DOUBLE_EQUAL) {
					return line.getProbability();
				}
			}
		}

		throw new BayesianError("Could not determine the probability for "
				+ state.toString());
	}

	/**
	 * Perform a single enumeration.
	 * @return The result.
	 */
	private double performEnumeration() {
		double result = 0;

		do {
			boolean first = true;
			double prob = 0;
			for (EventState state : this.getEvents().values()) {
				if (first) {
					prob = calculateProbability(state);
					first = false;
				} else {
					prob *= calculateProbability(state);
				}
			}
			result += prob;
		} while (forward());
		return result;
	}

	/**
	 * {@inheritDoc}
	 */
	public void execute() {
		locateEventTypes();
		resetEnumeration(false, false);
		double numerator = performEnumeration();
		resetEnumeration(false, true);
		double denominator = performEnumeration();
		this.probability = numerator / denominator;
	}

	/**
	 * {@inheritDoc}
	 */
	public double getProbability() {
		return probability;
	}

	/**
	 * {@inheritDoc}
	 */
	public String toString() {
		StringBuilder result = new StringBuilder();
		result.append("[SamplingQuery: ");
		result.append(getProblem());
		result.append("=");
		result.append(Format.formatPercent(getProbability()));
		result.append("]");
		return result.toString();
	}
	
	/**
	 * Roll the enumeration events forward by one.
	 * 
	 * @return False if there are no more values to roll into, which means we're
	 *         done.
	 */
	public static boolean roll(List<BayesianEvent> enumerationEvents, int[] args) {
		int currentIndex = 0;
		boolean done = false;
		boolean eof = false;
		
		if( enumerationEvents.size() == 0 ) {
			done = true;
			eof = true;
		}

		while (!done) {
			BayesianEvent event = enumerationEvents.get(currentIndex);
			int v = (int) args[currentIndex];
			v++;
			if (v >= event.getChoices().size()) {
				args[currentIndex] = 0;
			} else {
				args[currentIndex] = v;
				done = true;
				break;
			}

			currentIndex++;

			if (currentIndex >= args.length) {
				done = true;
				eof = true;
			}
		}

		return !eof;
	}
	
	/**
	 * @return A clone of this object.
	 */
	public EnumerationQuery clone() {
		return new EnumerationQuery(this.getNetwork());
	}

}
