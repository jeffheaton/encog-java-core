package org.encog.ml.bayesian.table;

import org.encog.util.EngineArray;

public class TableLine {
	
	private final double probability;
	private final double result;
	private final double[] arguments;

	public TableLine(double prob, double result, double[] args) {
		this.probability = prob;
		this.result = result;
		this.arguments = EngineArray.arrayCopy(args);
	}

	/**
	 * @return the probability
	 */
	public double getProbability() {
		return probability;
	}

	/**
	 * @return the arguments
	 */
	public double[] getArguments() {
		return arguments;
	}
	
	

}
