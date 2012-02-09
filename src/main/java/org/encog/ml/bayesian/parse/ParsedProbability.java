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
package org.encog.ml.bayesian.parse;

import java.util.ArrayList;
import java.util.List;

import org.encog.ml.bayesian.BayesianError;
import org.encog.ml.bayesian.BayesianEvent;
import org.encog.ml.bayesian.BayesianNetwork;

/**
 * A probability that has been parsed. 
 */
public class ParsedProbability {
	/**
	 * The base events.
	 */
	private final List<ParsedEvent> baseEvents = new ArrayList<ParsedEvent>();
	
	/**
	 * The given events.
	 */
	private final List<ParsedEvent> givenEvents = new ArrayList<ParsedEvent>();
	
	public void addGivenEvent(ParsedEvent event) {
		this.givenEvents.add(event);
	}
	
	
	/**
	 * Add a base event.
	 * @param event The base event to add.
	 */
	public void addBaseEvent(ParsedEvent event) {
		this.baseEvents.add(event);
	}
	
	/**
	 * Get the arguments to this event.
	 * @param network The network.
	 * @return The arguments.
	 */
	public int[] getArgs(BayesianNetwork network) {
		int[] result = new int[givenEvents.size()];
				
		for(int i=0;i<givenEvents.size();i++) {
			ParsedEvent givenEvent = this.givenEvents.get(i);
			BayesianEvent actualEvent = network.getEvent(givenEvent.getLabel());
			result[i] = givenEvent.resolveValue(actualEvent);
		}
		
		return result;
	}
	
	/**
	 * @return The child events.
	 */
	public ParsedEvent getChildEvent() {
		if( this.baseEvents.size()>1 ) {
			throw new BayesianError("Only one base event may be used to define a probability, i.e. P(a), not P(a,b).");
		}
		
		if( this.baseEvents.size()==0) {
			throw new BayesianError("At least one event must be provided, i.e. P() or P(|a,b,c) is not allowed.");
		}
		
		return this.baseEvents.get(0);
	}

	/**
	 * Define the truth table.
	 * @param network The bayesian network.
	 * @param result The resulting probability.
	 */
	public void defineTruthTable(BayesianNetwork network, double result) {
		
		ParsedEvent childParsed = getChildEvent();
		BayesianEvent childEvent = network.requireEvent(childParsed.getLabel());
		
		// define truth table line
		int[] args = getArgs(network);
		childEvent.getTable().addLine(result, childParsed.resolveValue(childEvent), args);
		
	}

	/**
	 * @return The base events.
	 */
	public List<ParsedEvent> getBaseEvents() {
		return baseEvents;
	}

	/**
	 * @return The given events.
	 */
	public List<ParsedEvent> getGivenEvents() {
		return givenEvents;
	}

	/**
	 * Define the relationships.
	 * @param network The network.
	 */
	public void defineRelationships(BayesianNetwork network) {
		// define event relations, if they are not there already
		ParsedEvent childParsed = getChildEvent();
		BayesianEvent childEvent = network.requireEvent(childParsed.getLabel());
		for( ParsedEvent event : this.givenEvents) {
			BayesianEvent parentEvent = network.requireEvent(event.getLabel());
			network.createDependency(parentEvent, childEvent);
		}
		
	}

	/**
	 * {@inheritDoc}
	 */
	public String toString() {
		StringBuilder result = new StringBuilder();
		result.append("[ParsedProbability:baseEvents=");
		result.append(this.baseEvents.toString());
		result.append(",givenEvents=");
		result.append(this.givenEvents.toString());
		result.append("]");
		return result.toString();
	}

	
}
