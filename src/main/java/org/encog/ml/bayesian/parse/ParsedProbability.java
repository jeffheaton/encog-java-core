package org.encog.ml.bayesian.parse;

import java.util.ArrayList;
import java.util.List;

import org.encog.ml.bayesian.BayesianError;
import org.encog.ml.bayesian.BayesianEvent;
import org.encog.ml.bayesian.BayesianNetwork;

public class ParsedProbability {
	private final List<ParsedEvent> baseEvents = new ArrayList<ParsedEvent>();
	private final List<ParsedEvent> givenEvents = new ArrayList<ParsedEvent>();
	
	public void addGivenEvent(ParsedEvent event) {
		this.givenEvents.add(event);
	}
	
	public void addBaseEvent(ParsedEvent event) {
		this.baseEvents.add(event);
	}
	
	public double[] getArgs(BayesianEvent actualEvent) {
		double[] result = new double[givenEvents.size()];
				
		for(int i=0;i<givenEvents.size();i++) {
			ParsedEvent givenEvent = this.givenEvents.get(i);
			result[i] = givenEvent.resolveValue(actualEvent);
		}
		
		return result;
	}
	
	public ParsedEvent getChildEvent() {
		if( this.baseEvents.size()>1 ) {
			throw new BayesianError("Only one base event may be used to define a probability, i.e. P(a), not P(a,b).");
		}
		
		if( this.baseEvents.size()==0) {
			throw new BayesianError("At least one event must be provided, i.e. P() or P(|a,b,c) is not allowed.");
		}
		
		return this.baseEvents.get(0);
	}

	public void defineTruthTable(BayesianNetwork network, double result) {
		
		ParsedEvent childParsed = getChildEvent();
		BayesianEvent childEvent = network.requireEvent(childParsed.getLabel());
		
		// define truth table line
		double[] args = getArgs(childEvent);
		childEvent.getTable().addLine(result, childParsed.resolveValue(childEvent), args);
		
	}

	public List<ParsedEvent> getBaseEvents() {
		return baseEvents;
	}

	public List<ParsedEvent> getGivenEvents() {
		return givenEvents;
	}

	public void defineRelationships(BayesianNetwork network) {
		// define event relations, if they are not there already
		ParsedEvent childParsed = getChildEvent();
		BayesianEvent childEvent = network.requireEvent(childParsed.getLabel());
		for( ParsedEvent event : this.givenEvents) {
			BayesianEvent parentEvent = network.requireEvent(event.getLabel());
			network.createDependancy(parentEvent, childEvent);
		}
		
	}
	
	
}
