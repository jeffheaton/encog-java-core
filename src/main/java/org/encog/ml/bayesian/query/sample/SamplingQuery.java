package org.encog.ml.bayesian.query.sample;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.encog.ml.bayesian.BayesianError;
import org.encog.ml.bayesian.BayesianEvent;
import org.encog.ml.bayesian.BayesianNetwork;
import org.encog.ml.bayesian.EventType;
import org.encog.ml.bayesian.query.BasicQuery;
import org.encog.util.Format;

public class SamplingQuery extends BasicQuery {
	
	public static final int DEFAULT_SAMPLE_SIZE = 100000;
	
	private int sampleSize = DEFAULT_SAMPLE_SIZE;
	private int usableSamples;
	private int goodSamples;
	private int totalSamples;
		
	public SamplingQuery(BayesianNetwork theNetwork) {
		super(theNetwork);
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
	
	
	private void randomizeEvents(EventState eventState) {
		// first, has this event already been randomized
		if (!eventState.isCalculated()) {
			// next, see if we can randomize the event passed
			double[] args = obtainArgs(eventState.getEvent());
			if (args != null) {
				eventState.randomize(args);
			}
		}

		// randomize children
		for (BayesianEvent childEvent : eventState.getEvent().getChildren()) {
			randomizeEvents(getEventState(childEvent));
		}
	}
		
	private int countUnCalculated() {
		int result = 0;
		for(EventState state: getEvents().values()) {
			if( !state.isCalculated())
				result++;
		}
		return result;
	}

	public void execute() {
		locateEventTypes();
		this.usableSamples = 0;
		this.goodSamples = 0;
		this.totalSamples = 0;
		
		for(int i=0;i<this.sampleSize;i++) {
			reset();
			
			int lastUncalculated = Integer.MAX_VALUE;
			int uncalculated;
			do {
				for(EventState state: getEvents().values()) {
					randomizeEvents(state);
				}
				uncalculated = countUnCalculated();
				if( uncalculated==lastUncalculated) 
				{
					throw new BayesianError("Unable to calculate all nodes in the graph.");
				}
				lastUncalculated = uncalculated;
			} while( uncalculated>0 );
			
			//System.out.println("Sample:\n" + this.dumpCurrentState());
			this.totalSamples++;
			if( isNeededEvidence() ) {
				this.usableSamples++;
				if( satisfiesDesiredOutcome() ) {
					this.goodSamples++;
				}
			}
		}		
	}

	
	public double getProbability() {
		return (double)this.goodSamples/(double)this.usableSamples;
	}
	
	public String dumpCurrentState() {
		StringBuilder result = new StringBuilder();
		for(EventState state : getEvents().values()) {
			result.append(state.toString());
			result.append("\n");
		}
		return result.toString();
	}
	
	public String toString() {
		StringBuilder result = new StringBuilder();
		result.append("[SamplingQuery: ");
		result.append(getProblem());
		result.append("=");
		result.append(Format.formatPercent(getProbability()));
		result.append(" ;good/usable=");
		result.append(Format.formatInteger(this.goodSamples));
		result.append("/");
		result.append(Format.formatInteger(this.usableSamples));
		result.append(";totalSamples=");
		result.append(Format.formatInteger(this.totalSamples));
		return result.toString();
	}

	
}
