/*
 * Encog Artificial Intelligence Framework v2.x
 * Java Version
 * http://www.heatonresearch.com/encog/
 * http://code.google.com/p/encog-java/
 * 
 * Copyright 2008-2010, Heaton Research Inc., and individual contributors.
 * See the copyright.txt in the distribution for a full listing of 
 * individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.encog.neural.networks.training.anneal;

import org.encog.solve.anneal.SimulatedAnnealing;
import org.encog.util.EncogArray;

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
				this.owner.getNetwork());
	}

	/**
	 * Used to pass the getArray call on to the parent object.
	 * 
	 * @return The array returned by the owner.
	 */
	@Override
	public Double[] getArray() {
		return EncogArray.doubleToObject(owner.getArray());
	}

	/**
	 * Used to pass the getArrayCopy call on to the parent object.
	 * 
	 * @return The array copy created by the owner.
	 */
	@Override
	public Double[] getArrayCopy() {
		return EncogArray.doubleToObject(owner.getArrayCopy());
	}

	/**
	 * Used to pass the putArray call on to the parent object.
	 * @param array The array.
	 */
	@Override
	public void putArray(final Double[] array) {
		owner.putArray(EncogArray.objectToDouble(array));
	}

	/**
	 * Call the owner's randomize method.
	 */
	@Override
	public void randomize() {
		owner.randomize();
	}

}
