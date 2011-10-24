package org.encog.ml.bayesian.query;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.encog.ml.bayesian.BayesianEvent;
import org.encog.ml.bayesian.BayesianNetwork;
import org.encog.ml.bayesian.EventType;

public class SamplingQuery {
	
	public static final int DEFAULT_SAMPLE_SIZE = 100000;
	
	private final BayesianNetwork network;
	private final Map<BayesianEvent,EventType> types = new HashMap<BayesianEvent,EventType>();
	private int sampleSize = DEFAULT_SAMPLE_SIZE;
	private final List<BayesianEvent> evidenceEvents = new ArrayList<BayesianEvent>();
	private final List<BayesianEvent> outcomeEvents = new ArrayList<BayesianEvent>();
		
	public SamplingQuery(BayesianNetwork theNetwork) {
		this.network = theNetwork;
		for(BayesianEvent event: theNetwork.getEvents().values()) {
			types.put(event, EventType.Hidden);
		}
	}
	
	public void defineEventType(BayesianEvent event, EventType et) {
		types.put(event, et);		
	}
	
	public EventType getEventType(BayesianEvent event) {
		return types.get(event);
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

	public void execute() {
		locateEventTypes();
		
		for(int i=0;i<this.sampleSize;i++) {
			//for()
		}
		
	}
	
}
