package org.encog.ml.bayesian.query;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.encog.ml.bayesian.BayesianError;
import org.encog.ml.bayesian.BayesianEvent;
import org.encog.ml.bayesian.BayesianNetwork;
import org.encog.ml.bayesian.EventType;
import org.encog.ml.bayesian.query.sample.EventState;

public class BasicQuery {
	
	private final BayesianNetwork network;
	private final Map<BayesianEvent,EventState> events = new HashMap<BayesianEvent,EventState>();	
	private final List<BayesianEvent> evidenceEvents = new ArrayList<BayesianEvent>();
	private final List<BayesianEvent> outcomeEvents = new ArrayList<BayesianEvent>();
	
	public BasicQuery(BayesianNetwork theNetwork) {
		this.network = theNetwork;
		for(BayesianEvent event: theNetwork.getEvents().values()) {
			events.put(event, new EventState(event));
		}
	}
	
	
	
	/**
	 * @return the network
	 */
	public BayesianNetwork getNetwork() {
		return network;
	}



	/**
	 * @return the events
	 */
	public Map<BayesianEvent, EventState> getEvents() {
		return events;
	}



	/**
	 * @return the evidenceEvents
	 */
	public List<BayesianEvent> getEvidenceEvents() {
		return evidenceEvents;
	}



	/**
	 * @return the outcomeEvents
	 */
	public List<BayesianEvent> getOutcomeEvents() {
		return outcomeEvents;
	}



	public void locateEventTypes() {
		this.evidenceEvents.clear();
		this.outcomeEvents.clear();
		
		for(BayesianEvent event: this.network.getEvents().values()) {
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
	
	public void reset() {
		for(EventState s : this.events.values()) {
			s.setCalculated(false);			
		}
	}

	
	public void defineEventType(BayesianEvent event, EventType et) {		
		getEventState(event).setEventType(et);		
	}
	
	public EventState getEventState(BayesianEvent event) {
		return this.events.get(event);
	}
	
	public EventType getEventType(BayesianEvent event) {
		return getEventState(event).getEventType();
	}
	
	protected boolean isNeededEvidence() {
		for(BayesianEvent evidenceEvent: this.evidenceEvents) {
			EventState state = getEventState(evidenceEvent);
			if(!state.isSatisfied() )  {
				return false;
			}
		}
		return true;
	}
	
	protected boolean satisfiesDesiredOutcome() {
		for(BayesianEvent outcomeEvent: this.outcomeEvents) {
			EventState state = getEventState(outcomeEvent);
			if(!state.isSatisfied() )  {
				return false;
			}
		}
		return true;
	}

	public void setEventValue(BayesianEvent event, boolean b) {
		setEventValue(event,b?1:0);		
	}
	
	public void setEventValue(BayesianEvent event, double d) {
		if( getEventType(event)==EventType.Hidden) {
			throw new BayesianError("You may only set the value of an evidence or outcome event.");
		}
		
		getEventState(event).setCompareValue(d);
	}

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
