package org.encog.ml.bayesian.naive;

import java.util.ArrayList;
import java.util.List;

import org.encog.ml.bayesian.BayesianError;
import org.encog.ml.bayesian.BayesianEvent;

public class NaiveBayes {
	
	private BayesianEvent posterior;
	private List<BayesianEvent> evidence = new ArrayList<BayesianEvent>();
	
	public NaiveBayes() {
		
	}
	
	public NaiveBayes(String posteriorLabel) {
		this.posterior = new BayesianEvent(posteriorLabel);		
	}
	
	public void addEvidence(BayesianEvent evidenceEvent) {
		if( this.posterior==null ) {
			throw new BayesianError("Can't add evidence, posterior has not yet been defined.");
		}
		this.evidence.add(evidenceEvent);
	}
	
	public void addEvidence(String label) {
		addEvidence(new BayesianEvent(label));
	}

	public BayesianEvent getPosterior() {
		return posterior;
	}

	public void setPosterior(BayesianEvent posterior) {
		this.posterior = posterior;
	}

	public List<BayesianEvent> getEvidence() {
		return evidence;
	}
	
	
	
	
}
