package org.encog.ml.bayesian.naive;

import java.util.HashMap;
import java.util.Map;

import org.encog.ml.bayesian.BayesianEvent;
import org.encog.ml.bayesian.BayesianNetwork;
import org.encog.ml.bayesian.EventType;
import org.encog.ml.bayesian.query.BayesianQuery;

public class NaiveBayesian extends BayesianNetwork {

	private final BayesianEvent posterior;
	private final Map<BayesianEvent,BoolProbHolder> initMap = new HashMap<BayesianEvent,BoolProbHolder>();
	
	public NaiveBayesian(BayesianEvent thePosterior) {
		this.posterior = thePosterior;
		createEvent(thePosterior);
	}
	
	public NaiveBayesian(String thePosterior) {
		this( new BayesianEvent(thePosterior));
	}
	
	public void addFeature( BayesianEvent event) {
		createDependancy(this.posterior, event);
	}
	
	public BayesianEvent addFeature(String label, double pClass, double pNot) {
		BayesianEvent event = createEvent(label);
		addFeature(event);
		
		this.initMap.put(event, new BoolProbHolder(event,pClass,pNot));

		return event;
	}
	
	public BayesianEvent getPosterior() {
		return posterior;
	}

	public double computeNaiveProbability() {
		BayesianQuery query = getQuery();
						
		for( BayesianEvent event: this.getEvents()) {
			if( event!=this.posterior ) {
				query.defineEventType(event, EventType.Evidence);
			} else {
				query.defineEventType(event, EventType.Outcome);
			}
		}
		
		query.execute();

		return query.getProbability();
	}

	@Override
	public void finalizeStructure() {
		super.finalizeStructure();
		
		for(BayesianEvent event: this.initMap.keySet()) {
			BoolProbHolder holder = this.initMap.get(event);
			event.getTable().addLine(holder.getProbabilityClass(), true, true); // class
			event.getTable().addLine(holder.getProbabilityNotClass(), true, false); // not class	
		}
	}
}
