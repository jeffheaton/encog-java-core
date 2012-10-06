/*
 * Encog(tm) Core v3.1 - Java Version
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
package org.encog.neural.networks.training.anneal;

import org.encog.ml.MLRegression;
import org.encog.ml.anneal.SimulatedAnnealing;
import org.encog.util.EngineArray;

/**
 * Simple class used by the neural simulated annealing. This class is a subclass
 * of the basic SimulatedAnnealing class. The It is used by the actual
 * NeuralSimulatedAnnealing class, which subclasses BasicTraining. This class is
 * mostly necessary due to the fact that NeuralSimulatedAnnealing can't subclass
 * BOTH SimulatedAnnealing and Train, because multiple inheritance is not
 * supported.
 * 
 * @author jheaton
 * 
 */
public class NeuralSimulatedAnnealingHelper extends SimulatedAnnealing<Double> {

	/**
	 * The class that this class should report to.
	 */
	private NeuralSimulatedAnnealing owner;

	/**
	 * Constructs this object.
	 * 
	 * @param owner
	 *            The owner of this class, that recieves all messages.
	 */
	public NeuralSimulatedAnnealingHelper(
			final NeuralSimulatedAnnealing owner) {
		this.owner = owner;
		this.setShouldMinimize(this.owner.getCalculateScore().shouldMinimize());
	}

	/**
	 * Used to pass the determineError call on to the parent object.
	 * 
	 * @return The error returned by the owner.
	 */
	@Override
	public double calculateScore() {
		return owner.getCalculateScore().calculateScore(
				(MLRegression)this.owner.getMethod());
	}

	/**
	 * Used to pass the getArray call on to the parent object.
	 * 
	 * @return The array returned by the owner.
	 */
	@Override
	public Double[] getArray() {
		return EngineArray.doubleToObject(owner.getArray());
	}

	/**
	 * Used to pass the getArrayCopy call on to the parent object.
	 * 
	 * @return The array copy created by the owner.
	 */
	@Override
	public Double[] getArrayCopy() {
		return EngineArray.doubleToObject(owner.getArrayCopy());
	}

	/**
	 * Used to pass the putArray call on to the parent object.
	 * @param array The array.
	 */
	@Override
	public void putArray(final Double[] array) {
		owner.putArray(EngineArray.objectToDouble(array));
	}

	/**
	 * Call the owner's randomize method.
	 */
	@Override
	public void randomize() {
		owner.randomize();
	}

}
