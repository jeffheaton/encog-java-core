package org.encog.ml.bayesian.naive;

import org.encog.ml.bayesian.BayesianEvent;

public class BoolProbHolder {
	
	private final BayesianEvent event;
	private final double probabilityClass;
	private final double probabilityNotClass;

	public BoolProbHolder(BayesianEvent event, double probabilityClass,
			double probabilityNotClass) {
		super();
		this.event = event;
		this.probabilityClass = probabilityClass;
		this.probabilityNotClass = probabilityNotClass;
	}

	public BayesianEvent getEvent() {
		return event;
	}

	public double getProbabilityClass() {
		return probabilityClass;
	}

	public double getProbabilityNotClass() {
		return probabilityNotClass;
	}

}
