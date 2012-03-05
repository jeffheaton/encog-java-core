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
package org.encog.neural.som.training.basic;

import org.encog.mathutil.matrices.Matrix;
import org.encog.mathutil.matrices.MatrixMath;
import org.encog.ml.MLMethod;
import org.encog.ml.TrainingImplementationType;
import org.encog.ml.data.MLData;
import org.encog.ml.data.MLDataPair;
import org.encog.ml.data.MLDataSet;
import org.encog.ml.data.basic.BasicMLData;
import org.encog.ml.train.BasicTraining;
import org.encog.neural.NeuralNetworkError;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.training.LearningRate;
import org.encog.neural.networks.training.propagation.TrainingContinuation;
import org.encog.neural.som.SOM;
import org.encog.neural.som.training.basic.neighborhood.NeighborhoodFunction;
import org.encog.util.Format;
import org.encog.util.logging.EncogLogging;

/**
 * This class implements competitive training, which would be used in a
 * winner-take-all neural network, such as the self organizing map (SOM). This
 * is an unsupervised training method, no ideal data is needed on the training
 * set. If ideal data is provided, it will be ignored.
 * 
 * Training is done by looping over all of the training elements and calculating
 * a "best matching unit" (BMU). This BMU output neuron is then adjusted to
 * better "learn" this pattern. Additionally, this training may be applied to
 * other "nearby" output neurons. The degree to which nearby neurons are update
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
public class BasicTrainSOM extends BasicTraining implements LearningRate {

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
	private final SOM network;

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
	private final Matrix correctionMatrix;

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
	public BasicTrainSOM(final SOM network, final double learningRate,
			final MLDataSet training, final NeighborhoodFunction neighborhood) {
		super(TrainingImplementationType.Iterative);
		this.neighborhood = neighborhood;
		setTraining(training);
		this.learningRate = learningRate;
		this.network = network;
		this.inputNeuronCount = network.getInputCount();
		this.outputNeuronCount = network.getOutputCount();
		this.forceWinner = false;
		setError(0);

		// setup the correction matrix
		this.correctionMatrix = new Matrix(this.outputNeuronCount,this.inputNeuronCount);

		// create the BMU class
		this.bmuUtil = new BestMatchingUnit(network);
	}

	/**
	 * Loop over the synapses to be trained and apply any corrections that were
	 * determined by this training iteration.
	 */
	private void applyCorrection() {
		this.network.getWeights().set(this.correctionMatrix);
	}

	/**
	 * Should be called each iteration if autodecay is desired.
	 */
	public final void autoDecay() {
		if (this.radius > this.endRadius) {
			this.radius += this.autoDecayRadius;
		}

		if (this.learningRate > this.endRate) {
			this.learningRate += this.autoDecayRate;
		}
		getNeighborhood().setRadius(this.radius);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final boolean canContinue() {
		return false;
	}

	/**
	 * Copy the specified input pattern to the weight matrix. This causes an
	 * output neuron to learn this pattern "exactly". This is useful when a
	 * winner is to be forced.
	 * 
	 * @param matrix
	 *            The matrix that is the target of the copy.
	 * @param outputNeuron
	 *            The output neuron to set.
	 * @param input
	 *            The input pattern to copy.
	 */
	private void copyInputPattern(final Matrix matrix, final int outputNeuron,
			final MLData input) {
		for (int inputNeuron = 0; inputNeuron < this.inputNeuronCount; inputNeuron++) {
			matrix.set(outputNeuron, inputNeuron, input.getData(inputNeuron));
		}
	}

	/**
	 * Called to decay the learning rate and radius by the specified amount.
	 * 
	 * @param d
	 *            The percent to decay by.
	 */
	public final void decay(final double d) {
		this.radius *= (1.0 - d);
		this.learningRate *= (1.0 - d);
	}

	/**
	 * Decay the learning rate and radius by the specified amount.
	 * 
	 * @param decayRate
	 *            The percent to decay the learning rate by.
	 * @param decayRadius
	 *            The percent to decay the radius by.
	 */
	public final void decay(final double decayRate, final double decayRadius) {
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
	 * @param matrix
	 *            The synapse to modify.
	 * @return True if a winner was forced.
	 */
	private boolean forceWinners(final Matrix matrix, final int[] won,
			final MLData leastRepresented) {

		double maxActivation = Double.MIN_VALUE;
		int maxActivationNeuron = -1;

		final MLData output = compute(this.network, leastRepresented);

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
			copyInputPattern(matrix, maxActivationNeuron, leastRepresented);
			return true;
		} else {
			return false;
		}
	}

	/**
	 * @return The input neuron count.
	 */
	public final int getInputNeuronCount() {
		return this.inputNeuronCount;
	}

	/**
	 * @return The learning rate. This was set when the object was created.
	 */
	@Override
	public final double getLearningRate() {
		return this.learningRate;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final MLMethod getMethod() {
		return this.network;
	}

	/**
	 * @return The network neighborhood function.
	 */
	public final NeighborhoodFunction getNeighborhood() {
		return this.neighborhood;
	}

	/**
	 * @return The output neuron count.
	 */
	public final int getOutputNeuronCount() {
		return this.outputNeuronCount;
	}

	/**
	 * @return Is a winner to be forced of neurons that do not learn. See class
	 *         description for more info.
	 */
	public final boolean isForceWinner() {
		return this.forceWinner;
	}

	/**
	 * Perform one training iteration.
	 */
	@Override
	public final void iteration() {

		EncogLogging.log(EncogLogging.LEVEL_INFO,
				"Performing SOM Training iteration.");

		preIteration();

		// Reset the BMU and begin this iteration.
		this.bmuUtil.reset();
		final int[] won = new int[this.outputNeuronCount];
		double leastRepresentedActivation = Double.MAX_VALUE;
		MLData leastRepresented = null;

		// Reset the correction matrix for this synapse and iteration.
		this.correctionMatrix.clear();

		// Determine the BMU for each training element.
		for (final MLDataPair pair : getTraining()) {
			final MLData input = pair.getInput();

			final int bmu = this.bmuUtil.calculateBMU(input);
			won[bmu]++;

			// If we are to force a winner each time, then track how many
			// times each output neuron becomes the BMU (winner).
			if (this.forceWinner) {


				// Get the "output" from the network for this pattern. This
				// gets the activation level of the BMU.
				final MLData output = compute(this.network,pair.getInput());

				// Track which training entry produces the least BMU. This
				// pattern is the least represented by the network.
				if (output.getData(bmu) < leastRepresentedActivation) {
					leastRepresentedActivation = output.getData(bmu);
					leastRepresented = pair.getInput();
				}
			}

			train(bmu, this.network.getWeights(), input);

			if (this.forceWinner) {
				// force any non-winning neurons to share the burden somewhat\
				if (!forceWinners(this.network.getWeights(), won,
						leastRepresented)) {
					applyCorrection();
				}
			} else {
				applyCorrection();
			}
		}

		// update the error
		setError(this.bmuUtil.getWorstDistance() / 100.0);

		postIteration();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final TrainingContinuation pause() {
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void resume(final TrainingContinuation state) {

	}

	/**
	 * Setup autodecay. This will decrease the radius and learning rate from the
	 * start values to the end values.
	 * 
	 * @param plannedIterations
	 *            The number of iterations that are planned. This allows the
	 *            decay rate to be determined.
	 * @param startRate
	 *            The starting learning rate.
	 * @param endRate
	 *            The ending learning rate.
	 * @param startRadius
	 *            The starting radius.
	 * @param endRadius
	 *            The ending radius.
	 */
	public final void setAutoDecay(final int plannedIterations,
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
	public final void setForceWinner(final boolean forceWinner) {
		this.forceWinner = forceWinner;
	}

	/**
	 * Set the learning rate. This is the rate at which the weights are changed.
	 * 
	 * @param rate
	 *            The learning rate.
	 */
	@Override
	public final void setLearningRate(final double rate) {
		this.learningRate = rate;
	}

	/**
	 * Set the learning rate and radius.
	 * 
	 * @param rate
	 *            The new learning rate.
	 * @param radius
	 *            The new radius.
	 */
	public final void setParams(final double rate, final double radius) {
		this.radius = radius;
		this.learningRate = rate;
		getNeighborhood().setRadius(radius);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final String toString() {
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
	 * @param matrix
	 *            The synapse to train.
	 * @param input
	 *            The input to train for.
	 */
	private void train(final int bmu, final Matrix matrix, final MLData input) {
		// adjust the weight for the BMU and its neighborhood
		for (int outputNeuron = 0; outputNeuron < this.outputNeuronCount; outputNeuron++) {
			trainPattern(matrix, input, outputNeuron, bmu);
		}
	}

	/**
	 * Train for the specified pattern.
	 * 
	 * @param matrix
	 *            The synapse to train.
	 * @param input
	 *            The input pattern to train for.
	 * @param current
	 *            The current output neuron being trained.
	 * @param bmu
	 *            The best matching unit, or winning output neuron.
	 */
	private void trainPattern(final Matrix matrix, final MLData input,
			final int current, final int bmu) {

		for (int inputNeuron = 0; inputNeuron < this.inputNeuronCount; inputNeuron++) {

			final double currentWeight = matrix.get(current, inputNeuron);
			final double inputValue = input.getData(inputNeuron);

			final double newWeight = determineNewWeight(currentWeight,
					inputValue, current, bmu);

			this.correctionMatrix.set(current, inputNeuron, newWeight);
		}
	}

	/**
	 * Train the specified pattern. Find a winning neuron and adjust all neurons
	 * according to the neighborhood function.
	 * 
	 * @param pattern
	 *            The pattern to train.
	 */
	public final void trainPattern(final MLData pattern) {

		final MLData input = pattern;
		final int bmu = this.bmuUtil.calculateBMU(input);
		train(bmu, this.network.getWeights(), input);
		applyCorrection();
	}
	
	/**
	 * Calculate the output of the SOM, for each output neuron.  Typically,
	 * you will use the classify method instead of calling this method.
	 * @param input
	 *            The input pattern.
	 * @return The output activation of each output neuron.
	 */
	private MLData compute(final SOM som, final MLData input) {

		final MLData result = new BasicMLData(som.getOutputCount());

		for (int i = 0; i < som.getOutputCount(); i++) {
			final Matrix optr = som.getWeights().getRow(i);
			final Matrix inputMatrix = Matrix.createRowMatrix(input.getData());
			result.setData(i, MatrixMath.dotProduct(inputMatrix, optr));
		}

		return result;
	}


}
