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
package org.encog.ml.bayesian.query;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.encog.ml.bayesian.BayesianError;
import org.encog.ml.bayesian.BayesianEvent;
import org.encog.ml.bayesian.BayesianNetwork;
import org.encog.ml.bayesian.EventType;
import org.encog.ml.bayesian.query.sample.EventState;

/**
 * Provides basic functionality for a Bayesian query. This class is abstract,
 * and is not used directly. Rather, other queries make use of it.
 * 
 */
public abstract class BasicQuery implements BayesianQuery, Serializable {
	
	/**
	 * The network to be queried.
	 */
	private final BayesianNetwork network;
	
	/**
	 * A mapping of the events to event states.
	 */
	private final Map<BayesianEvent,EventState> events = new HashMap<BayesianEvent,EventState>();
	
	/**
	 * The evidence events.
	 */
	private final List<BayesianEvent> evidenceEvents = new ArrayList<BayesianEvent>();
	
	/**
	 * Default constructor.
	 */
	public BasicQuery() {
		this.network = null;
	}
	
	
	/**
	 * The outcome events.
	 */
	private final List<BayesianEvent> outcomeEvents = new ArrayList<BayesianEvent>();
	
	public BasicQuery(BayesianNetwork theNetwork) {
		this.network = theNetwork;
		finalizeStructure();
	}
	
	public void finalizeStructure() {
		this.events.clear();
		for(BayesianEvent event: this.network.getEvents()) {
			events.put(event, new EventState(event));
		}
	}
	
	
	
	/**
	 * {@inheritDoc}
	 */
	public BayesianNetwork getNetwork() {
		return network;
	}



	/**
	 * {@inheritDoc}
	 */
	public Map<BayesianEvent, EventState> getEvents() {
		return events;
	}



	/**
	 * {@inheritDoc}
	 */
	public List<BayesianEvent> getEvidenceEvents() {
		return evidenceEvents;
	}



	/**
	 * {@inheritDoc}
	 */
	public List<BayesianEvent> getOutcomeEvents() {
		return outcomeEvents;
	}



	/**
	 * Called to locate the evidence and outcome events.
	 */
	public void locateEventTypes() {
		this.evidenceEvents.clear();
		this.outcomeEvents.clear();
		
		for(BayesianEvent event: this.network.getEvents()) {
			switch(getEventType(event)) {
				case Evidence:
					this.evidenceEvents.add(event);
					break;
				case Outcome:
					this.outcomeEvents.add(event);
					break;
			}
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	public void reset() {
		for(EventState s : this.events.values()) {
			s.setCalculated(false);			
		}
	}

	
	/**
	 * {@inheritDoc}
	 */
	public void defineEventType(BayesianEvent event, EventType et) {		
		getEventState(event).setEventType(et);		
	}
	
	/**
	 * {@inheritDoc}
	 */
	public EventState getEventState(BayesianEvent event) {
		return this.events.get(event);
	}
	
	/**
	 * {@inheritDoc}
	 */
	public EventType getEventType(BayesianEvent event) {
		return getEventState(event).getEventType();
	}
	
	/**
	 * @return Determines if the evidence events have values that satisfy the
	 *         needed case. This is used for sampling.
	 */
	protected boolean isNeededEvidence() {
		for(BayesianEvent evidenceEvent: this.evidenceEvents) {
			EventState state = getEventState(evidenceEvent);
			if(!state.isSatisfied() )  {
				return false;
			}
		}
		return true;
	}
	
	/**
	 * @return True, if the current state satisifies the desired outcome.
	 */
	protected boolean satisfiesDesiredOutcome() {
		for(BayesianEvent outcomeEvent: this.outcomeEvents) {
			EventState state = getEventState(outcomeEvent);
			if(!state.isSatisfied() )  {
				return false;
			}
		}
		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	public void setEventValue(BayesianEvent event, boolean b) {
		setEventValue(event,b?0:1);		
	}
	
	/**
	 * {@inheritDoc}
	 */
	public void setEventValue(BayesianEvent event, int d) {
		if( getEventType(event)==EventType.Hidden) {
			throw new BayesianError("You may only set the value of an evidence or outcome event.");
		}
		
		getEventState(event).setCompareValue(d);
		getEventState(event).setValue(d);
	}

	/**
	 * {@inheritDoc}
	 */
	public String getProblem() {
		
		if( this.outcomeEvents.size()==0 )
			return "";
		
		StringBuilder result = new StringBuilder();
		result.append("P(");
		boolean first = true;
		for(BayesianEvent event: this.outcomeEvents) {
			if( !first ) {
				result.append(",");
			}
			first = false;
			result.append(EventState.toSimpleString(getEventState(event)));
		}
		result.append("|");
		
		first = true;
		for(BayesianEvent event: this.evidenceEvents) {
			if( !first ) {
				result.append(",");
			}
			first = false;
			EventState state = getEventState(event);
			if( state==null )
				break;
			result.append(EventState.toSimpleString(state));
		}
		result.append(")");
		
		return result.toString();
	}

	public BayesianQuery clone() {
		return null;
	}

}
