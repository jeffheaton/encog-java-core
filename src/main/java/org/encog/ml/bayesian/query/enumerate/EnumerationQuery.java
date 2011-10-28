package org.encog.ml.bayesian.query.enumerate;

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

public class EnumerationQuery extends BasicQuery {

	private List<EventState> enumerationEvents = new ArrayList<EventState>();
	private double probability;

	public EnumerationQuery(BayesianNetwork theNetwork) {
		super(theNetwork);		
	}

	public void resetEnumeration(boolean includeEvidence, boolean includeOutcome) {		
		this.enumerationEvents.clear();

		for (EventState state : this.getEvents().values()) {
			if (state.getEventType() == EventType.Hidden) {
				this.enumerationEvents.add(state);
				state.setValue(0);
			} else if( includeEvidence && state.getEventType()==EventType.Evidence) {
				this.enumerationEvents.add(state);
				state.setValue(0);
			} else if( includeOutcome && state.getEventType()==EventType.Outcome) {
				this.enumerationEvents.add(state);
				state.setValue(0);
			} else {
				state.setValue(state.getCompareValue());
			}
		}
	}

	public boolean forward() {
		int currentIndex = 0;
		boolean done = false;
		boolean eof = false;

		while (!done) {
			
			EventState state = this.enumerationEvents.get(currentIndex);
			int v = (int) state.getValue();
			v++;
			if (v >= state.getEvent().getChoices().length) {
				state.setValue(0);
			} else {
				state.setValue(v);
				done = true;
				break;
			}
			
			currentIndex++;
			
			if( currentIndex>=this.enumerationEvents.size() ) {
				done = true;
				eof = true;
			}
		}

		return !eof;
	}
	
	private double[] obtainArgs(BayesianEvent event) {
		double[] result = new double[event.getParents().size()];
		
		int index = 0;
		for(BayesianEvent parentEvent: event.getParents()) {
			EventState state = this.getEventState(parentEvent);
			result[index++] = state.getValue();
			
		}
		return result;
	}
	
	private double calculateProbability(EventState state) {

		double[] args = obtainArgs(state.getEvent());
		
		for( TableLine line : state.getEvent().getTable().getLines()) {
			if( line.compareArgs(args)) {
				if( Math.abs(line.getResult()-state.getValue())<Encog.DEFAULT_DOUBLE_EQUAL ) {
					return line.getProbability();
				}
			}
		}
		
		throw new BayesianError("Could not determine the probability for " + state.toString());
	}
	
	private double performEnumeration() {
		double result = 0;
		
		do {
			boolean first = true;
			double prob = 0;
			for(EventState state: this.getEvents().values()) {				
				if( first ) {
					prob = calculateProbability(state);
					first = false;
				} else {
					prob *= calculateProbability(state);					
				}
			}
			result+=prob;
		} while(forward());
		return result;
	}
	
	

	public void execute() {
		locateEventTypes();
		resetEnumeration(false,false);
		double numerator = performEnumeration();
		resetEnumeration(false,true);
		double denominator = performEnumeration();
		this.probability = numerator/denominator;
	}
		
	/**
	 * @return the probability
	 */
	public double getProbability() {
		return probability;
	}

	public String toString() {
		StringBuilder result = new StringBuilder();
		result.append("[SamplingQuery: ");
		result.append(getProblem());
		result.append("=");
		result.append(Format.formatPercent(getProbability()));
		result.append("]");
		return result.toString();
	}


}
