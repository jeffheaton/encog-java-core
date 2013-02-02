package org.encog.neural.neat.training.opp;

import org.encog.ml.ea.opp.EvolutionaryOperator;
import org.encog.ml.ea.train.EvolutionaryAlgorithm;
import org.encog.neural.neat.training.NEATTraining;

public abstract class NEATMutation implements EvolutionaryOperator {
	
	private NEATTraining owner;

	/**
	 * @return the owner
	 */
	public NEATTraining getOwner() {
		return owner;
	}
	
	@Override
	public int offspringProduced() {
		return 1;
	}

	@Override
	public int parentsNeeded() {
		// TODO Auto-generated method stub
		return 1;
	}
	
	@Override
	public void init(EvolutionaryAlgorithm theOwner) {
		this.owner = (NEATTraining) theOwner;	
	}
	
	
}
