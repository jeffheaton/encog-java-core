package org.encog.ml.ea.opp;

import java.util.Random;

import org.encog.ml.ea.genome.Genome;
import org.encog.ml.ea.train.EvolutionaryAlgorithm;
import org.encog.util.obj.ObjectHolder;

public class CompoundOperator implements EvolutionaryOperator {

	private EvolutionaryAlgorithm owner;
	private final OperationList components = new OperationList();
	
	@Override
	public void performOperation(Random rnd, Genome[] parents, int parentIndex,
			Genome[] offspring, int offspringIndex) {
		EvolutionaryOperator opp = this.components.pick(rnd);
		opp.performOperation(rnd, parents, parentIndex, offspring, offspringIndex);
	}

	@Override
	public int offspringProduced() {
		return this.components.maxOffspring();
	}

	@Override
	public int parentsNeeded() {
		return this.components.maxOffspring();
	}

	@Override
	public void init(EvolutionaryAlgorithm theOwner) {
		this.owner = theOwner;
		for(ObjectHolder<EvolutionaryOperator> obj : components.getList()) {
			obj.getObj().init(theOwner);
		}
	}

	/**
	 * @return the owner
	 */
	public EvolutionaryAlgorithm getOwner() {
		return owner;
	}

	/**
	 * @return the components
	 */
	public OperationList getComponents() {
		return components;
	}
	
	
	
	

}
