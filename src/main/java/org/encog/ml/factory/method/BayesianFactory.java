package org.encog.ml.factory.method;

import org.encog.ml.MLMethod;
import org.encog.ml.bayesian.BayesianNetwork;

public class BayesianFactory {
	
	
	public final MLMethod create(final String architecture, final int input,
			final int output) {
		MLMethod method = new BayesianNetwork();
		return method;
	}
}
