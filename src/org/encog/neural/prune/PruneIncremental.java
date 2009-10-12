/*
 * Encog Artificial Intelligence Framework v2.x
 * Java Version
 * http://www.heatonresearch.com/encog/
 * http://code.google.com/p/encog-java/
 * 
 * Copyright 2008-2009, Heaton Research Inc., and individual contributors.
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
package org.encog.neural.prune;

import java.util.ArrayList;
import java.util.List;

import org.encog.StatusReportable;
import org.encog.neural.data.NeuralDataSet;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.layers.Layer;
import org.encog.neural.networks.training.Train;
import org.encog.neural.networks.training.propagation.resilient.ResilientPropagation;
import org.encog.neural.pattern.NeuralNetworkPattern;
import org.encog.util.concurrency.job.ConcurrentJob;
import org.encog.util.concurrency.job.JobUnitContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class is used to help determine the optimal configuration for the hidden
 * layers of a neural network. It can accept a pattern, which specifies the type
 * of neural network to create, and a list of the maximum and minimum hidden
 * layer neurons. It will then attempt to train the neural network at all
 * configurations and see which hidden neuron counts work the best.
 * 
 * @author jheaton
 * 
 */
public class PruneIncremental extends ConcurrentJob {

	/**
	 * Format the network as a human readable string that lists the hidden
	 * layers.
	 * 
	 * @param network
	 *            The network to format.
	 * @return A human readable string.
	 */
	public static String networkToString(final BasicNetwork network) {
		final StringBuilder result = new StringBuilder();
		int num = 1;

		Layer layer = network.getLayer(BasicNetwork.TAG_INPUT);

		// display only hidden layers
		while (layer.getNext().size() > 0) {
			layer = layer.getNext().get(0).getToLayer();

			if (result.length() > 0) {
				result.append(",");
			}
			result.append("H");
			result.append(num++);
			result.append("=");
			result.append(layer.getNeuronCount());
		}

		return result.toString();
	}

	/**
	 * Are we done?
	 */
	private boolean done = false;

	/**
	 * The logging object.
	 */
	@SuppressWarnings("unused")
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	/**
	 * The training set to use as different neural networks are evaluated.
	 */
	private final NeuralDataSet training;

	/**
	 * The pattern for which type of neural network we would like to create.
	 */
	private final NeuralNetworkPattern pattern;

	/**
	 * The ranges for the hidden layers.
	 */
	private final List<HiddenLayerParams> hidden = new ArrayList<HiddenLayerParams>();

	/**
	 * The number if training iterations that should be tried for each network.
	 */
	private final int iterations;

	/**
	 * The best error rate found so far.
	 */
	private double bestResult;

	/**
	 * The best network found so far.
	 */
	private BasicNetwork bestNetwork;

	/**
	 * How many networks have been tried so far?
	 */
	private int currentTry;

	/**
	 * The object that status should be reported to.
	 */
	private final StatusReportable report;

	/**
	 * Keeps track of how many neurons in each hidden layer as training the
	 * evaluation progresses.
	 */
	private int[] hiddenCounts;

	/**
	 * Construct an object to determine the optimal number of hidden layers and
	 * neurons for the specified training data and pattern.
	 * 
	 * @param training
	 *            The training data to use.
	 * @param pattern
	 *            The network pattern to use to solve this data.
	 * @param iterations
	 *            How many iterations to try per network.
	 * @param report
	 *            Object used to report status to.
	 */
	public PruneIncremental(final NeuralDataSet training,
			final NeuralNetworkPattern pattern, final int iterations,
			final StatusReportable report) {
		super(report);
		this.training = training;
		this.pattern = pattern;
		this.iterations = iterations;
		this.report = report;
	}

	/**
	 * Add a hidden layer's min and max. Call this once per hidden layer.
	 * Specify a zero min if it is possible to remove this hidden layer.
	 * 
	 * @param min
	 *            The minimum number of neurons for this layer.
	 * @param max
	 *            The maximum number of neurons for this layer.
	 */
	public void addHiddenLayer(final int min, final int max) {
		final HiddenLayerParams param = new HiddenLayerParams(min, max);
		this.hidden.add(param);
	}

	/**
	 * Generate a network according to the current hidden layer counts.
	 * 
	 * @return The network based on current hidden layer counts.
	 */
	private BasicNetwork generateNetwork() {
		this.pattern.clear();

		for (final int element : this.hiddenCounts) {
			if (element > 0) {
				this.pattern.addHiddenLayer(element);
			}
		}

		return this.pattern.generate();
	}

	/**
	 * @return The network being processed.
	 */
	public BasicNetwork getBestNetwork() {
		return this.bestNetwork;
	}

	/**
	 * @return The hidden layer max and min.
	 */
	public List<HiddenLayerParams> getHidden() {
		return this.hidden;
	}

	/**
	 * @return The number of training iterations to try for each network.
	 */
	public int getIterations() {
		return this.iterations;
	}

	/**
	 * @return The network pattern to use.
	 */
	public NeuralNetworkPattern getPattern() {
		return this.pattern;
	}

	/**
	 * @return The training set to use.
	 */
	public NeuralDataSet getTraining() {
		return this.training;
	}

	/**
	 * Increase the hidden layer counts according to the hidden layer
	 * parameters. Increase the first hidden layer count by one, if it is maxed
	 * out, then set it to zero and increase the next hidden layer.
	 * 
	 * @return False if no more increases can be done, true otherwise.
	 */
	private boolean increaseHiddenCounts() {
		int i = 0;
		do {
			final HiddenLayerParams param = this.hidden.get(i);
			this.hiddenCounts[i]++;

			// is this hidden layer still within the range?
			if (this.hiddenCounts[i] <= param.getMax()) {
				return true;
			}

			// increase the next layer if we've maxed out this one
			this.hiddenCounts[i] = param.getMin();
			i++;

		} while (i < this.hiddenCounts.length);

		// can't increase anymore, we're done!

		return false;
	}

	/**
	 * Get the next workload. This is the number of hidden neurons. This is the
	 * total amount of work to be processed.
	 * 
	 * @return The amount of work to be processed by this.
	 */
	@Override
	public int loadWorkload() {
		int result = 1;

		for (final HiddenLayerParams param : this.hidden) {
			result *= (param.getMax() - param.getMin()) + 1;
		}

		return result;
	}

	/**
	 * Perform an individual job unit, which is a single network to train and
	 * evaluate.
	 * 
	 * @param context
	 *            Contains information about the job unit.
	 */
	@Override
	public void performJobUnit(final JobUnitContext context) {

		final BasicNetwork network = (BasicNetwork) context.getJobUnit();

		// train the neural network
		final Train train = new ResilientPropagation(network, this.training);

		for (int i = 0; i < this.iterations; i++) {
			train.iteration();
		}

		final double error = train.getError();

		if ((error < this.bestResult) || (this.bestNetwork == null)) {
			if (this.logger.isDebugEnabled()) {
				this.logger.debug("Prune found new best network: error="
						+ error + ", network=" + network);
			}
			this.bestNetwork = network;
			this.bestResult = error;
		}
		this.currentTry++;

		reportStatus(context, "Current: "
				+ PruneIncremental.networkToString(network) + ", Best: "
				+ PruneIncremental.networkToString(this.bestNetwork));

	}

	/**
	 * Begin the prune process.
	 */
	@Override
	public void process() {

		if (this.hidden.size() == 0) {
			final String str = "To calculate the optimal hidden size, at least "
					+ "one hidden layer must be defined.";
			if (this.logger.isErrorEnabled()) {
				this.logger.error(str);
			}
		}

		this.hiddenCounts = new int[this.hidden.size()];

		// set the best network
		this.bestNetwork = null;
		this.bestResult = Double.MAX_VALUE;

		// set to minimums
		int i = 0;
		for (final HiddenLayerParams parm : this.hidden) {
			this.hiddenCounts[i++] = parm.getMin();
		}

		// make sure hidden layer 1 has at least one neuron
		if (this.hiddenCounts[0] == 0) {
			final String str = "To calculate the optimal hidden size, at least "
					+ "one neuron must be the minimum for the first hidden layer.";
			if (this.logger.isErrorEnabled()) {
				this.logger.error(str);
			}

		}

		super.process();
	}

	/**
	 * Request the next task. This is the next network to attempt to train.
	 * 
	 * @return The next network to train.
	 */
	@Override
	public Object requestNextTask() {
		if (this.done) {
			return null;
		}

		final BasicNetwork network = generateNetwork();

		if (!increaseHiddenCounts()) {
			this.done = true;
		}

		return network;
	}

}
