package org.encog.ml.bayesian.query;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.encog.ml.bayesian.BayesianError;
import org.encog.ml.bayesian.BayesianEvent;
import org.encog.ml.bayesian.BayesianNetwork;
import org.encog.ml.bayesian.EventType;

public class SamplingQuery {
	
	public static final int DEFAULT_SAMPLE_SIZE = 100000;
	
	private final BayesianNetwork network;
	private final Map<BayesianEvent,EventState> events = new HashMap<BayesianEvent,EventState>();
	private int sampleSize = DEFAULT_SAMPLE_SIZE;
	private final List<BayesianEvent> evidenceEvents = new ArrayList<BayesianEvent>();
	private final List<BayesianEvent> outcomeEvents = new ArrayList<BayesianEvent>();
	private int usableSamples;
	private int goodSamples;
		
	public SamplingQuery(BayesianNetwork theNetwork) {
		this.network = theNetwork;
		for(BayesianEvent event: theNetwork.getEvents().values()) {
			events.put(event, new EventState(event));
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
	
	

	/**
	 * @return the sampleSize
	 */
	public int getSampleSize() {
		return sampleSize;
	}

	/**
	 * @param sampleSize the sampleSize to set
	 */
	public void setSampleSize(int sampleSize) {
		this.sampleSize = sampleSize;
	}
	
	private void locateEventTypes() {
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
	
	private void randomizeEvidence() {
		for(BayesianEvent event: this.evidenceEvents) {
			EventState state = getEventState(event);
			state.randomize();
		}
	}
	
	private double[] obtainArgs(BayesianEvent event) {
		double[] result = new double[event.getParents().size()];
		
		int index = 0;
		for(BayesianEvent parentEvent: event.getParents()) {
			EventState state = this.getEventState(parentEvent);
			if( !state.isCalculated())
				return null;
			result[index++] = state.getValue();
			
		}
		return result;
	}
	
	
	private void randomizeEvents(BayesianEvent event) {
		// first, has this event already been randomized
		EventState eventState = getEventState(event);
		if (!eventState.isCalculated()) {
			// next, see if we can randomize the event passed
			double[] args = obtainArgs(event);
			if (args != null) {
				EventState state = getEventState(event);
				state.randomize(args);
			}
		}

		// randomize children
		for (BayesianEvent childEvent : event.getChildren()) {
			randomizeEvents(childEvent);
		}
	}
	
	private boolean isNeededEvidence() {
		for(BayesianEvent evidenceEvent: this.evidenceEvents) {
			EventState state = getEventState(evidenceEvent);
			if(!state.isSatisfied() )  {
				return false;
			}
		}
		return true;
	}
	
	private boolean satisfiesDesiredOutcome() {
		for(BayesianEvent outcomeEvent: this.outcomeEvents) {
			EventState state = getEventState(outcomeEvent);
			if(!state.isSatisfied() )  {
				return false;
			}
		}
		return true;
	}

	public void execute() {
		locateEventTypes();
		this.usableSamples = 0;
		this.goodSamples = 0;
		
		for(int i=0;i<this.sampleSize;i++) {
			reset();
			randomizeEvidence();
			
			for(BayesianEvent evidenceEvent: this.evidenceEvents) {
				randomizeEvents(evidenceEvent);
			}
			
			if( isNeededEvidence() ) {
				this.usableSamples++;
				if( satisfiesDesiredOutcome() ) {
					this.goodSamples++;
				}
			}
		}		
	}

	public void setEventValue(BayesianEvent event, boolean b) {
		setEventValue(event,0.0);		
	}
	
	public void setEventValue(BayesianEvent event, double d) {
		if( getEventType(event)==EventType.Hidden) {
			throw new BayesianError("You may only set the value of an evidence or result event.");
		}
		
		getEventState(event).setCompareValue(d);
	}
	
	public double getProbability() {
		return (double)this.goodSamples/(double)this.usableSamples;
	}
	
}
