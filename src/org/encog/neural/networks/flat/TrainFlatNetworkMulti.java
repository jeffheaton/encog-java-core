package org.encog.neural.networks.flat;

import java.util.List;

import org.encog.mathutil.IntRange;
import org.encog.mathutil.error.ErrorCalculation;
import org.encog.neural.data.Indexable;
import org.encog.neural.data.NeuralDataPair;
import org.encog.neural.data.NeuralDataSet;
import org.encog.neural.networks.training.propagation.resilient.ResilientPropagation;
import org.encog.util.concurrency.DetermineWorkload;
import org.encog.util.concurrency.EncogConcurrency;
import org.encog.util.concurrency.TaskGroup;

public class TrainFlatNetworkMulti {
	
	/**
	 * The gradients
	 */
	private final double[] gradients;
	
	/**
	 * The last gradients, from the last training iteration.
	 */
	private final double[] lastGradient;
	
	/**
	 * The neuron counts, per layer.
	 */
	private final int[] layerCounts;
	
	/**
	 * The deltas for each layer
	 */
	private final double[] layerDelta;
	
	/**
	 * The layer indexes
	 */
	private final int[] layerIndex;
	
	/**
	 * The output from each layer
	 */
	private final double[] layerOutput;
	
	/**
	 * The network to train.
	 */
	private final FlatNetwork network;
	
	/**
	 * The training data.
	 */
	private final NeuralDataSet training;
	
	/**
	 * The update values, for the weights and thresholds.
	 */
	private final double[] updateValues;
	
	/**
	 * The index to each layer's weights and thresholds.
	 */
	private final int[] weightIndex;
	
	private Indexable indexable;
	
	/**
	 * The weights and thresholds.
	 */
	private final double[] weights;
	
	private GradientWorker[] workers;
	
	private double totalError;
	
	private double currentError;

	public TrainFlatNetworkMulti(final FlatNetwork network,
			final NeuralDataSet training) {
		
		this.training = training;
		this.network = network;

		this.indexable = (Indexable)training;
		
		layerDelta = new double[network.getLayerOutput().length];
		gradients = new double[network.getWeights().length];
		updateValues = new double[network.getWeights().length];
		lastGradient = new double[network.getWeights().length];

		weights = network.getWeights();
		layerIndex = network.getLayerIndex();
		layerCounts = network.getLayerCounts();
		weightIndex = network.getWeightIndex();
		layerOutput = network.getLayerOutput();

		for (int i = 0; i < updateValues.length; i++) {
			updateValues[i] = ResilientPropagation.DEFAULT_INITIAL_UPDATE;
		}
		
		DetermineWorkload determine = new DetermineWorkload(0,(int)this.indexable.getRecordCount());
		this.workers = new GradientWorker[ determine.getThreadCount() ];
		List<IntRange> range = determine.calculateWorkers();
		
		int index = 0;
		for(IntRange r : range)
		{
			this.workers[index++] = new GradientWorker(network.clone(), this, indexable, r.getLow(), r.getHigh());
		}
	}
	
	public void report(double[] gradients, double error)
	{
		synchronized(this)
		{
			for(int i=0;i<gradients.length;i++)
			{
				this.gradients[i]+=gradients[i];
			}
			this.totalError+=error;
		}
	}

	public double derivativeSigmoid(final double d) {
		return d * (1.0 - d);
	}

	public double derivativeTANH(final double d) {
		return ((1 + d) * (1 - d));
	}

	public double getError() {
		return this.currentError;
	}

	public void iteration() {
		
		TaskGroup group = EncogConcurrency.getInstance().createTaskGroup();
		this.totalError = 0;
		
		for(GradientWorker worker: this.workers)
		{
			EncogConcurrency.getInstance().processTask(worker,group);
		}
		
		group.waitForComplete();
		
		learn();
		this.currentError = this.totalError/this.workers.length;
		
		for(GradientWorker worker: this.workers)
		{
			System.arraycopy(this.weights, 0, worker.getWeights(), 0, this.weights.length);
		}
	}

	private void learn() {
		for (int i = 0; i < gradients.length; i++) {
			weights[i] += updateWeight(gradients, i);
			gradients[i] = 0;
		}
	}


	/**
	 * Determine the sign of the value.
	 * 
	 * @param value
	 *            The value to check.
	 * @return -1 if less than zero, 1 if greater, or 0 if zero.
	 */
	private int sign(final double value) {
		if (Math.abs(value) < ResilientPropagation.DEFAULT_ZERO_TOLERANCE) {
			return 0;
		} else if (value > 0) {
			return 1;
		} else {
			return -1;
		}
	}

	/**
	 * Determine the amount to change a weight by.
	 * 
	 * @param gradients
	 *            The gradients.
	 * @param index
	 *            The weight to adjust.
	 * @return The amount to change this weight by.
	 */
	private double updateWeight(final double[] gradients, final int index) {
		// multiply the current and previous gradient, and take the
		// sign. We want to see if the gradient has changed its sign.
		final int change = sign(this.gradients[index] * lastGradient[index]);
		double weightChange = 0;

		// if the gradient has retained its sign, then we increase the
		// delta so that it will converge faster
		if (change > 0) {
			double delta = updateValues[index]
					* ResilientPropagation.POSITIVE_ETA;
			delta = Math.min(delta, ResilientPropagation.DEFAULT_MAX_STEP);
			weightChange = sign(this.gradients[index]) * delta;
			updateValues[index] = delta;
			lastGradient[index] = this.gradients[index];
		} else if (change < 0) {
			// if change<0, then the sign has changed, and the last
			// delta was too big
			double delta = updateValues[index]
					* ResilientPropagation.NEGATIVE_ETA;
			delta = Math.max(delta, ResilientPropagation.DELTA_MIN);
			updateValues[index] = delta;
			// set the previous gradent to zero so that there will be no
			// adjustment the next iteration
			lastGradient[index] = 0;
		} else if (change == 0) {
			// if change==0 then there is no change to the delta
			final double delta = lastGradient[index];
			weightChange = sign(this.gradients[index]) * delta;
			lastGradient[index] = this.gradients[index];
		}

		// apply the weight change, if any
		return weightChange;
	}

}
