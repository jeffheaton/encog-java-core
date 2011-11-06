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
		setEventValue(event,b?1:0);		
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
			result.append(EventState.toSimpleString(getEventState(event)));
		}
		result.append(")");
		
		return result.toString();
	}


}
