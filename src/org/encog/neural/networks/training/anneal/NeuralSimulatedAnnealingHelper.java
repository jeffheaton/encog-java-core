package org.encog.neural.networks.training.anneal;

import org.encog.solve.anneal.SimulatedAnnealing;

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
		return owner.getCalculateScore().calculateScore(this.owner.getNetwork());
	}

	/**
	 * Used to pass the getArray call on to the parent object.
	 * 
	 * @return The array returned by the owner.
	 */
	@Override
	public Double[] getArray() {
		return owner.getArray();
	}

	/**
	 * Used to pass the getArrayCopy call on to the parent object.
	 * 
	 * @return The array copy created by the owner.
	 */
	@Override
	public Double[] getArrayCopy() {
		return owner.getArrayCopy();
	}

	/**
	 * Used to pass the putArray call on to the parent object.
	 * @param array The array.
	 */
	@Override
	public void putArray(final Double[] array) {
		owner.putArray(array);
	}

	/**
	 * Call the owner's randomize method.
	 */
	@Override
	public void randomize() {
		owner.randomize();
	}

}
