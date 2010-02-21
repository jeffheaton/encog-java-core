/*
 * Encog(tm) Artificial Intelligence Framework v2.3
 * Java Version
 * http://www.heatonresearch.com/encog/
 * http://code.google.com/p/encog-java/
 * 
 * Copyright 2008-2010 by Heaton Research Inc.
 * 
 * Released under the LGPL.
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
 * 
 * Encog and Heaton Research are Trademarks of Heaton Research, Inc.
 * For information on Heaton Research trademarks, visit:
 * 
 * http://www.heatonresearch.com/copyright.html
 */

package org.encog.neural.networks.training.competitive;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.encog.math.matrices.Matrix;
import org.encog.neural.data.NeuralData;
import org.encog.neural.data.NeuralDataPair;
import org.encog.neural.data.NeuralDataSet;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.layers.Layer;
import org.encog.neural.networks.synapse.Synapse;
import org.encog.neural.networks.training.BasicTraining;
import org.encog.neural.networks.training.LearningRate;
import org.encog.neural.networks.training.competitive.neighborhood.NeighborhoodFunction;
import org.encog.util.Format;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class implements competitive training, which would be used in a
 * winner-take-all neural network, such as the self organizing map (SOM). This
 * is an unsupervised training method, no ideal data is needed on the training
 * set. If ideal data is provided, it will be ignored.
 * 
 * Training is done by looping over all of the training elements and calculating
 * a "best matching unit" (BMU). This BMU output neuron is then adjusted to
 * better "learn" this pattern. Additionally, this training may be applied to
 * othr "nearby" output neurons. The degree to which nearby neurons are update
 * is defined by the neighborhood function.
 * 
 * A neighborhood function is required to determine the degree to which
 * neighboring neurons (to the winning neuron) are updated by each training
 * iteration.
 * 
 * Because this is unsupervised training, calculating an error to measure
 * progress by is difficult. The error is defined to be the "worst", or longest,
 * Euclidean distance of any of the BMU's. This value should be minimized, as
 * learning progresses.
 * 
 * Because only the BMU neuron and its close neighbors are updated, you can end
 * up with some output neurons that learn nothing. By default these neurons are
 * not forced to win patterns that are not represented well. This spreads out
 * the workload among all output neurons. This feature is not used by default,
 * but can be enabled by setting the "forceWinner" property.
 * 
 * @author jheaton
 * 
 */
public class CompetitiveTraining extends BasicTraining implements LearningRate {

	/**
	 * The neighborhood function to use to determine to what degree a neuron
	 * should be "trained".
	 */
	private final NeighborhoodFunction neighborhood;

	/**
	 * The learning rate. To what degree should changes be applied.
	 */
	private double learningRate;

	/**
	 * The network being trained.
	 */
	private final BasicNetwork network;

	/**
	 * The input layer.
	 */
	private final Layer inputLayer;

	/**
	 * The output layer.
	 */
	private final Layer outputLayer;

	/**
	 * A collection of the synapses being modified.
	 */
	private final Collection<Synapse> synapses;

	/**
	 * How many neurons in the input layer.
	 */
	private final int inputNeuronCount;

	/**
	 * How many neurons in the output layer.
	 */
	private final int outputNeuronCount;

	/**
	 * Utility class used to determine the BMU.
	 */
	private final BestMatchingUnit bmuUtil;

	/**
	 * Holds the corrections for any matrix being trained.
	 */
	private final Map<Synapse, Matrix> correctionMatrix = 
		new HashMap<Synapse, Matrix>();

	/**
	 * True is a winner is to be forced, see class description, or forceWinners
	 * method. By default, this is true.
	 */
	private boolean forceWinner;

	/**
	 * When used with autodecay, this is the starting learning rate.
	 */
	private double startRate;
	
	/**
	 * When used with autodecay, this is the ending learning rate.
	 */
	private double endRate;
	
	/**
	 * When used with autodecay, this is the starting radius.
	 */
	private double startRadius;
	
	/**
	 * When used with autodecay, this is the ending radius.
	 */
	private double endRadius;
	
	/**
	 * This is the current autodecay learning rate.
	 */
	private double autoDecayRate;
	
	/**
	 * This is the current autodecay radius.
	 */
	private double autoDecayRadius;

	/**
	 * The logging object.
	 */
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	/**
	 * The current radius.
	 */
	private double radius;

	/**
	 * Create an instance of competitive training.
	 * 
	 * @param network
	 *            The network to train.
	 * @param learningRate
	 *            The learning rate, how much to apply per iteration.
	 * @param training
	 *            The training set (unsupervised).
	 * @param neighborhood
	 *            The neighborhood function to use.
	 */
	public CompetitiveTraining(final BasicNetwork network,
			final double learningRate, final NeuralDataSet training,
			final NeighborhoodFunction neighborhood) {
		this.neighborhood = neighborhood;
		setTraining(training);
		this.learningRate = learningRate;
		this.network = network;
		this.inputLayer = network.getLayer(BasicNetwork.TAG_INPUT);
		this.outputLayer = network.getLayer(BasicNetwork.TAG_OUTPUT);
		this.synapses = network.getStructure().getPreviousSynapses(
				this.outputLayer);
		this.inputNeuronCount = this.inputLayer.getNeuronCount();
		this.outputNeuronCount = this.outputLayer.getNeuronCount();
		this.forceWinner = false;
		setError(0);

		// setup the correction matrix
		for (final Synapse synapse : this.synapses) {
			final Matrix matrix = new Matrix(synapse.getMatrix().getRows(),
					synapse.getMatrix().getCols());
			this.correctionMatrix.put(synapse, matrix);
		}

		// create the BMU class
		this.bmuUtil = new BestMatchingUnit(this);
	}

	/**
	 * Loop over the synapses to be trained and apply any corrections that were
	 * determined by this training iteration.
	 */
	private void applyCorrection() {
		for (final Entry<Synapse, Matrix> entry : this.correctionMatrix
				.entrySet()) {
			entry.getKey().getMatrix().set(entry.getValue());
		}
	}

	/**
	 * Should be called each iteration if autodecay is desired.
	 */
	public void autoDecay() {
		if (this.radius > this.endRadius) {
			this.radius += this.autoDecayRadius;
		}

		if (this.learningRate > this.endRate) {
			this.learningRate += this.autoDecayRate;
		}
		getNeighborhood().setRadius(this.radius);
	}

	/**
	 * Copy the specified input pattern to the weight matrix. This causes an
	 * output neuron to learn this pattern "exactly". This is useful when a
	 * winner is to be forced.
	 * 
	 * @param synapse
	 *            The synapse that is the target of the copy.
	 * @param outputNeuron
	 *            The output neuron to set.
	 * @param input
	 *            The input pattern to copy.
	 */
	private void copyInputPattern(final Synapse synapse,
			final int outputNeuron, final NeuralData input) {
		for (int inputNeuron = 0; inputNeuron < this.inputNeuronCount; 
			inputNeuron++) {
			synapse.getMatrix().set(inputNeuron, outputNeuron,
					input.getData(inputNeuron));
		}
	}

	/**
	 * Called to decay the learning rate and radius by the specified amount.  
	 * @param d The percent to decay by.
	 */
	public void decay(final double d) {
		this.radius *= (1.0 - d);
		this.learningRate *= (1.0 - d);
	}

	/**
	 * Decay the learning rate and radius by the specified amount.
	 * @param decayRate The percent to decay the learning rate by.
	 * @param decayRadius The percent to decay the radius by.
	 */
	public void decay(final double decayRate, final double decayRadius) {
		this.radius *= (1.0 - decayRadius);
		this.learningRate *= (1.0 - decayRate);
		getNeighborhood().setRadius(this.radius);
	}

	/**
	 * Determine the weight adjustment for a single neuron during a training
	 * iteration.
	 * 
	 * @param weight
	 *            The starting weight.
	 * @param input
	 *            The input to this neuron.
	 * @param currentNeuron
	 *            The neuron who's weight is being updated.
	 * @param bmu
	 *            The neuron that "won", the best matching unit.
	 * @return The new weight value.
	 */
	private double determineNewWeight(final double weight, final double input,
			final int currentNeuron, final int bmu) {

		final double newWeight = weight
				+ (this.neighborhood.function(currentNeuron, bmu)
						* this.learningRate * (input - weight));
		return newWeight;
	}

	/**
	 * Force any neurons that did not win to off-load patterns from overworked
	 * neurons.
	 * 
	 * @param won
	 *            An array that specifies how many times each output neuron has
	 *            "won".
	 * @param leastRepresented
	 *            The training pattern that is the least represented by this
	 *            neural network.
	 * @param synapse
	 *            The synapse to modify.
	 * @return True if a winner was forced.
	 */
	private boolean forceWinners(final Synapse synapse, final int[] won,
			final NeuralData leastRepresented) {

		double maxActivation = Double.MIN_VALUE;
		int maxActivationNeuron = -1;

		final NeuralData output = this.network.compute(leastRepresented);

		// Loop over all of the output neurons. Consider any neurons that were
		// not the BMU (winner) for any pattern. Track which of these
		// non-winning neurons had the highest activation.
		for (int outputNeuron = 0; outputNeuron < won.length; outputNeuron++) {
			// Only consider neurons that did not "win".
			if (won[outputNeuron] == 0) {
				if ((maxActivationNeuron == -1)
						|| (output.getData(outputNeuron) > maxActivation)) {
					maxActivation = output.getData(outputNeuron);
					maxActivationNeuron = outputNeuron;
				}
			}
		}

		// If a neurons was found that did not activate for any patterns, then
		// force it to "win" the least represented pattern.
		if (maxActivationNeuron != -1) {
			copyInputPattern(synapse, maxActivationNeuron, leastRepresented);
			return true;
		} else {
			return false;
		}
	}

	/**
	 * @return The input neuron count.
	 */
	public int getInputNeuronCount() {
		return this.inputNeuronCount;
	}

	/**
	 * @return The learning rate. This was set when the object was created.
	 */
	public double getLearningRate() {
		return this.learningRate;
	}

	/**
	 * @return The network neighborhood function.
	 */
	public NeighborhoodFunction getNeighborhood() {
		return this.neighborhood;
	}

	/**
	 * @return The network being trained.
	 */
	public BasicNetwork getNetwork() {
		return this.network;
	}

	/**
	 * @return The output neuron count.
	 */
	public int getOutputNeuronCount() {
		return this.outputNeuronCount;
	}

	/**
	 * @return Is a winner to be forced of neurons that do not learn. See class
	 *         description for more info.
	 */
	public boolean isForceWinner() {
		return this.forceWinner;
	}

	/**
	 * Perform one training iteration.
	 */
	public void iteration() {

		if (this.logger.isInfoEnabled()) {
			this.logger.info("Performing Competitive Training iteration.");
		}

		preIteration();

		// Reset the BMU and begin this iteration.
		this.bmuUtil.reset();
		final int[] won = new int[this.outputNeuronCount];
		double leastRepresentedActivation = Double.MAX_VALUE;
		NeuralData leastRepresented = null;

		// The synapses are processed parallel to each other.
		for (final Synapse synapse : this.synapses) {

			// Reset the correction matrix for this synapse and iteration.
			final Matrix correction = this.correctionMatrix.get(synapse);
			correction.clear();

			// Determine the BMU for each training element.
			for (final NeuralDataPair pair : getTraining()) {
				final NeuralData input = pair.getInput();

				final int bmu = this.bmuUtil.calculateBMU(synapse, input);

				// If we are to force a winner each time, then track how many
				// times each output neuron becomes the BMU (winner).
				if (this.forceWinner) {
					won[bmu]++;

					// Get the "output" from the network for this pattern. This
					// gets the activation level of the BMU.
					final NeuralData output = this.network.compute(pair
							.getInput());

					// Track which training entry produces the least BMU. This
					// pattern is the least represented by the network.
					if (output.getData(bmu) < leastRepresentedActivation) {
						leastRepresentedActivation = output.getData(bmu);
						leastRepresented = pair.getInput();
					}
				}

				train(bmu, synapse, input);

			}

			if (this.forceWinner) {
				// force any non-winning neurons to share the burden somewhat\
				if (!forceWinners(synapse, won, leastRepresented)) {
					applyCorrection();
				}
			} else {
				applyCorrection();
			}
		}

		// update the error
		setError(this.bmuUtil.getWorstDistance());

		postIteration();
	}

	/**
	 * Setup autodecay.  This will decrease the radius and learning rate from
	 * the start values to the end values.
	 * @param plannedIterations The number of iterations that are planned.
	 * This allows the decay rate to be determined.
	 * @param startRate The starting learning rate.
	 * @param endRate The ending learning rate.
	 * @param startRadius The starting radius.
	 * @param endRadius The ending radius.
	 */
	public void setAutoDecay(final int plannedIterations,
			final double startRate, final double endRate,
			final double startRadius, final double endRadius) {
		this.startRate = startRate;
		this.endRate = endRate;
		this.startRadius = startRadius;
		this.endRadius = endRadius;
		this.autoDecayRadius = (endRadius - startRadius) / plannedIterations;
		this.autoDecayRate = (endRate - startRate) / plannedIterations;
		setParams(this.startRate, this.startRadius);
	}

	/**
	 * Determine if a winner is to be forced. See class description for more
	 * info.
	 * 
	 * @param forceWinner
	 *            True if a winner is to be forced.
	 */
	public void setForceWinner(final boolean forceWinner) {
		this.forceWinner = forceWinner;
	}

	/**
	 * Set the learning rate. This is the rate at which the weights are changed.
	 * 
	 * @param rate
	 *            The learning rate.
	 */
	public void setLearningRate(final double rate) {
		this.learningRate = rate;
	}

	/**
	 * Set the learning rate and radius.
	 * @param rate The new learning rate.
	 * @param radius The new radius.
	 */
	public void setParams(final double rate, final double radius) {
		this.radius = radius;
		this.learningRate = rate;
		getNeighborhood().setRadius(radius);
	}

	/**
	 * @return This object as a string.
	 */
	@Override
	public String toString() {
		final StringBuilder result = new StringBuilder();
		result.append("Rate=");
		result.append(Format.formatPercent(this.learningRate));
		result.append(", Radius=");
		result.append(Format.formatDouble(this.radius, 2));
		return result.toString();
	}

	/**
	 * Train for the specified synapse and BMU.
	 * 
	 * @param bmu
	 *            The best matching unit for this input.
	 * @param synapse
	 *            The synapse to train.
	 * @param input
	 *            The input to train for.
	 */
	private void train(final int bmu, final Synapse synapse,
			final NeuralData input) {
		// adjust the weight for the BMU and its neighborhood
		for (int outputNeuron = 0; outputNeuron < this.outputNeuronCount; 
			outputNeuron++) {
			trainPattern(synapse, input, outputNeuron, bmu);
		}
	}

	/**
	 * Train the specified pattern.  Find a winning neuron and adjust all
	 * neurons according to the neighborhood function.
	 * @param pattern The pattern to train.
	 */
	public void trainPattern(final NeuralData pattern) {
		for (final Synapse synapse : this.synapses) {
			final NeuralData input = pattern;
			final int bmu = this.bmuUtil.calculateBMU(synapse, input);
			train(bmu, synapse, input);
		}
		applyCorrection();

	}

	/**
	 * Train for the specified pattern.
	 * 
	 * @param synapse
	 *            The synapse to train.
	 * @param input
	 *            The input pattern to train for.
	 * @param current
	 *            The current output neuron being trained.
	 * @param bmu
	 *            The best matching unit, or winning output neuron.
	 */
	private void trainPattern(final Synapse synapse, final NeuralData input,
			final int current, final int bmu) {

		final Matrix correction = this.correctionMatrix.get(synapse);

		for (int inputNeuron = 0; inputNeuron < this.inputNeuronCount; 
			inputNeuron++) {

			final double currentWeight = synapse.getMatrix().get(inputNeuron,
					current);
			final double inputValue = input.getData(inputNeuron);

			final double newWeight = determineNewWeight(currentWeight,
					inputValue, current, bmu);

			correction.set(inputNeuron, current, newWeight);
		}
	}

}
