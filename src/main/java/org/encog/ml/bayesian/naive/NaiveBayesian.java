package org.encog.ml.bayesian.naive;

import java.util.HashMap;
import java.util.Map;

import org.encog.ml.bayesian.BayesianError;
import org.encog.ml.bayesian.BayesianEvent;
import org.encog.ml.bayesian.BayesianNetwork;
import org.encog.ml.bayesian.EventType;
import org.encog.ml.bayesian.query.BayesianQuery;

public class NaiveBayesian extends BayesianNetwork {

	private final BayesianEvent posterior;
	private final Map<BayesianEvent,BoolProbHolder> initMap = new HashMap<BayesianEvent,BoolProbHolder>();
	private final double[] posteriorProbability;
	
	public NaiveBayesian(BayesianEvent thePosterior, double[] thePosteriorProbability) {
		if( thePosteriorProbability.length!=thePosterior.getChoices().length) {
			throw new BayesianError("The posterior choice count (" + thePosterior.getChoices().length
					+") must match the posterior probability array size(" 
					+ thePosteriorProbability.length + ")");
		}
		this.posterior = thePosterior;
		this.posteriorProbability = thePosteriorProbability;
		createEvent(thePosterior);
	}
	
	public NaiveBayesian(String thePosterior, double probClass) {
		this( new BayesianEvent(thePosterior), new double[] { probClass, 1.0-probClass } );
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
		
		// handle the posterior probability		
		for(int i=0;i<this.posteriorProbability.length;i++) {
			this.posterior.getTable().addLine(this.posteriorProbability[i], i, new int[0]);
		}
		
		// handle the observed events
		for(BayesianEvent event: this.initMap.keySet()) {
			BoolProbHolder holder = this.initMap.get(event);
			event.getTable().addLine(holder.getProbabilityClass(), true, true); // class
			event.getTable().addLine(holder.getProbabilityNotClass(), true, false); // not class	
		}
	
		// clear up any memory used by this, since it is used no longer.
		this.initMap.clear();
	}
}
