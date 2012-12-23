package org.encog.ml.genetic.evolutionary;

import org.encog.ml.genetic.evolutionary.EvolutionaryOperator;

public class OperationHolder {
	private final EvolutionaryOperator opp;
	private final double probability;
	
	public OperationHolder(EvolutionaryOperator opp, double probability) {
		super();
		this.opp = opp;
		this.probability = probability;
	}

	/**
	 * @return the opp
	 */
	public EvolutionaryOperator getOpp() {
		return opp;
	}

	/**
	 * @return the probability
	 */
	public double getProbability() {
		return probability;
	}
	
	
	
}
