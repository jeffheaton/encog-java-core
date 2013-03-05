/*
 * Encog(tm) Core v3.2 - Java Version
 * http://www.heatonresearch.com/encog/
 * http://code.google.com/p/encog-java/
 
 * Copyright 2008-2012 Heaton Research, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *   
 * For more information on Heaton Research copyrights, licenses 
 * and trademarks visit:
 * http://www.heatonresearch.com/copyright
 */
package org.encog.ml.ea.opp;

import java.util.Random;

import org.encog.ml.ea.genome.Genome;
import org.encog.ml.ea.train.EvolutionaryAlgorithm;
import org.encog.util.obj.ObjectHolder;

/**
 * A compound operator randomly chooses sub-operators to perform the actual
 * operation. Each of the sub-operators can be provided with a weighting.
 */
public class CompoundOperator implements EvolutionaryOperator {

	/**
	 * The owner of this operator.
	 */
	private EvolutionaryAlgorithm owner;

	/**
	 * The sub-operators that make up this compound operator.
	 */
	private final OperationList components = new OperationList();

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void performOperation(Random rnd, Genome[] parents, int parentIndex,
			Genome[] offspring, int offspringIndex) {
		EvolutionaryOperator opp = this.components.pick(rnd);
		opp.performOperation(rnd, parents, parentIndex, offspring,
				offspringIndex);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int offspringProduced() {
		return this.components.maxOffspring();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int parentsNeeded() {
		return this.components.maxOffspring();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void init(EvolutionaryAlgorithm theOwner) {
		this.owner = theOwner;
		for (ObjectHolder<EvolutionaryOperator> obj : components.getList()) {
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
