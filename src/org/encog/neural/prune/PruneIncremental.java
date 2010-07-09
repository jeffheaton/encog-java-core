/*
 * Encog(tm) Core v2.5 
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

package org.encog.neural.prune;

import java.util.ArrayList;
import java.util.List;

import org.encog.StatusReportable;
import org.encog.neural.data.Indexable;
import org.encog.neural.data.NeuralDataSet;
import org.encog.neural.data.buffer.BufferedNeuralDataSet;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.layers.Layer;
import org.encog.neural.networks.training.propagation.Propagation;
import org.encog.neural.networks.training.propagation.resilient.ResilientPropagation;
import org.encog.neural.networks.training.strategy.StopTrainingStrategy;
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
 * This method does not simply choose the network with the lowest error rate. A
 * specifiable number of best networks are kept, which represent the networks
 * with the lowest error rates. From this collection of networks, the best
 * network is defined to be the one with the fewest number of connections.
 * 
 * Not all starting random weights are created equal. Because of this, an option
 * is provided to allow you to choose how many attempts you want the process to
 * make, with different weights. All random weights are created using the
 * default Nguyen-Widrow method normally used by Encog.
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

			if (layer.getNext().size() > 0) {
				if (result.length() > 0) {
					result.append(",");
				}
				result.append("H");
				result.append(num++);
				result.append("=");
				result.append(layer.getNeuronCount());
			}
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
	 * An array of the top networks.
	 */
	private final BasicNetwork[] topNetworks;

	/**
	 * An array of the top errors.
	 */
	private final double[] topErrors;

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
	@SuppressWarnings("unused")
	private final StatusReportable report;

	/**
	 * Keeps track of how many neurons in each hidden layer as training the
	 * evaluation progresses.
	 */
	private int[] hiddenCounts;

	/**
	 * The current highest error.
	 */
	private double high;

	/**
	 * The current lowest error.
	 */
	private double low;

	/**
	 * The results in a 2d array.
	 */
	private double[][] results;

	/**
	 * The size of the first hidden layer.
	 */
	private int hidden1Size;

	/**
	 * The size of the second hidden layer.
	 */
	private int hidden2Size;

	/**
	 * The number of tries with random weights.
	 */
	private final int weightTries;

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
	 * @param weightTries
	 *            The number of random weights to use.
	 * @param numTopResults
	 *            The number of "top networks" to choose the most simple "best
	 *            network" from.
	 * @param report
	 *            Object used to report status to.
	 */
	public PruneIncremental(final NeuralDataSet training,
			final NeuralNetworkPattern pattern, final int iterations,
			final int weightTries, final int numTopResults,
			final StatusReportable report) {
		super(report);
		this.training = training;
		this.pattern = pattern;
		this.iterations = iterations;
		this.report = report;
		this.weightTries = weightTries;
		this.topNetworks = new BasicNetwork[numTopResults];
		this.topErrors = new double[numTopResults];
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
	 * @return The size of the first hidden layer.
	 */
	public int getHidden1Size() {
		return this.hidden1Size;
	}

	/**
	 * @return The size of the second hidden layer.
	 */
	public int getHidden2Size() {
		return this.hidden2Size;
	}

	/**
	 * @return The higest error so far.
	 */
	public double getHigh() {
		return this.high;
	}

	/**
	 * @return The number of training iterations to try for each network.
	 */
	public int getIterations() {
		return this.iterations;
	}

	/**
	 * @return The lowest error so far.
	 */
	public double getLow() {
		return this.low;
	}

	/**
	 * @return The network pattern to use.
	 */
	public NeuralNetworkPattern getPattern() {
		return this.pattern;
	}

	/**
	 * @return The error results.
	 */
	public double[][] getResults() {
		return this.results;
	}

	/**
	 * @return the topErrors
	 */
	public double[] getTopErrors() {
		return this.topErrors;
	}

	/**
	 * @return the topNetworks
	 */
	public BasicNetwork[] getTopNetworks() {
		return this.topNetworks;
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
	 * Init for prune.
	 */
	public void init() {
		// handle display for one layer
		if (this.hidden.size() == 1) {
			this.hidden1Size = (this.hidden.get(0).getMax() - this.hidden
					.get(0).getMin()) + 1;
			this.hidden2Size = 0;
			this.results = new double[this.hidden1Size][1];
		} else if (this.hidden.size() == 2) {
			// handle display for two layers
			this.hidden1Size = (this.hidden.get(0).getMax() - this.hidden
					.get(0).getMin()) + 1;
			this.hidden2Size = (this.hidden.get(1).getMax() - this.hidden
					.get(1).getMin()) + 1;
			this.results = new double[this.hidden1Size][this.hidden2Size];
		} else {
			// we don't handle displays for more than two layers
			this.hidden1Size = 0;
			this.hidden2Size = 0;
			this.results = null;
		}

		// reset min and max
		this.high = Double.NEGATIVE_INFINITY;
		this.low = Double.POSITIVE_INFINITY;

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

		init();

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
		BufferedNeuralDataSet buffer = null;
		NeuralDataSet useTraining = this.training;
		
		if( this.training instanceof BufferedNeuralDataSet )
		{
			buffer = (BufferedNeuralDataSet)this.training;
			useTraining = buffer.openAdditional();
		}
		

		// train the neural network
	
		double error = Double.POSITIVE_INFINITY;
		for (int z = 0; z < this.weightTries; z++) {
			network.reset();
			final Propagation train = new ResilientPropagation(network,
					useTraining);
			final StopTrainingStrategy strat = new StopTrainingStrategy(0.001,
					5);

			train.addStrategy(strat);
			train.setNumThreads(1); // force single thread mode

			for (int i = 0; (i < this.iterations) && !getShouldStop()
					&& !strat.shouldStop(); i++) {
				train.iteration();
			}

			error = Math.min(error, train.getError());
		}
		
		if( buffer!=null )
			buffer.close();

		if (!getShouldStop()) {
			// update min and max

			this.high = Math.max(this.high, error);
			this.low = Math.min(this.low, error);

			if (this.hidden1Size > 0) {
				int networkHidden1Count;
				int networkHidden2Count;

				if (network.getStructure().getLayers().size() > 3) {
					networkHidden2Count = network.getStructure().getLayers()
							.get(1).getNeuronCount();
					networkHidden1Count = network.getStructure().getLayers()
							.get(2).getNeuronCount();
				} else {
					networkHidden2Count = 0;
					networkHidden1Count = network.getStructure().getLayers()
							.get(1).getNeuronCount();
				}

				int row, col;

				if (this.hidden2Size == 0) {
					row = networkHidden1Count - this.hidden.get(0).getMin();
					col = 0;
				} else {
					row = networkHidden1Count - this.hidden.get(0).getMin();
					col = networkHidden2Count - this.hidden.get(1).getMin();
				}

				this.results[row][col] = error;
			}

			// report status
			this.currentTry++;

			updateBest(network, error);
			reportStatus(context, "Current: "
					+ PruneIncremental.networkToString(network) + "; Best: "
					+ PruneIncremental.networkToString(this.bestNetwork));
		}

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
		if (this.done || getShouldStop()) {
			return null;
		}

		final BasicNetwork network = generateNetwork();

		if (!increaseHiddenCounts()) {
			this.done = true;
		}

		return network;
	}

	/**
	 * Update the best network.
	 * @param network The network to consider.
	 * @param error The error for this network.
	 */
	private synchronized void updateBest(final BasicNetwork network,
			final double error) {
		this.high = Math.max(this.high, error);
		this.low = Math.min(this.low, error);

		int selectedIndex = -1;

		// find a place for this in the top networks, if it is a top network
		for (int i = 0; i < this.topNetworks.length; i++) {
			if (this.topNetworks[i] == null) {
				selectedIndex = i;
				break;
			} else if (this.topErrors[i] > error) {
				// this network might be worth replacing, see if the one
				// already selected is a better option.
				if ((selectedIndex == -1)
						|| (this.topErrors[selectedIndex] < this.topErrors[i])) {
					selectedIndex = i;
				}
			}
		}

		// replace the selected index
		if (selectedIndex != -1) {
			this.topErrors[selectedIndex] = error;
			this.topNetworks[selectedIndex] = network;
		}

		// now select the best network, which is the most simple of the
		// top networks.

		BasicNetwork choice = null;

		for (final BasicNetwork n : this.topNetworks) {
			if (n == null) {
				continue;
			}

			if (choice == null) {
				choice = n;
			} else {
				if (n.getStructure().calculateSize() < choice.getStructure()
						.calculateSize()) {
					choice = n;
				}
			}
		}

		if (choice != this.bestNetwork) {
			if (this.logger.isDebugEnabled()) {
				this.logger.debug("Prune found new best network: error="
						+ error + ", network=" + choice);
			}
			this.bestNetwork = choice;
		}

	}
}
