package org.encog.neural.networks.flat;

import org.encog.neural.data.Indexable;
import org.encog.neural.data.NeuralDataPair;
import org.encog.neural.data.basic.BasicNeuralDataPair;
import org.encog.util.Stopwatch;
import org.encog.util.cl.EncogCLDevice;
import org.encog.util.cl.kernels.KernelNetworkTrain;
import org.encog.util.cl.kernels.TrainingWorkload;

/**
 * A worker that uses OpenCL to process its workload.
 * 
 */
public class GradientWorkerCL implements FlatGradientWorker {
	/**
	 * The network to train.
	 */
	private final FlatNetwork network;

	/**
	 * The actual values from the neural network.
	 */
	private final double[] actual;

	/**
	 * The deltas for each layer.
	 */
	private final double[] layerDelta;

	/**
	 * The neuron counts, per layer.
	 */
	private final int[] layerCounts;

	/**
	 * The layer indexes.
	 */
	private final int[] layerIndex;

	/**
	 * The index to each layer's weights and thresholds.
	 */
	private final int[] weightIndex;

	/**
	 * The output from each layer.
	 */
	private final double[] layerOutput;

	/**
	 * The gradients.
	 */
	private final double[] gradients;

	/**
	 * The weights and thresholds.
	 */
	private final double[] weights;

	/**
	 * The neural network pair.
	 */
	private final NeuralDataPair pair;

	/**
	 * The training data.
	 */
	private final Indexable training;

	/**
	 * The low end of the training data.
	 */
	private final int low;

	/**
	 * The high end of the training data.
	 */
	private final int high;

	/**
	 * The owner.
	 */
	private final TrainFlatNetworkMulti owner;

	/**
	 * The elapsed time, for performance.
	 */
	private long elapsedTime;

	/**
	 * The stopwatch.
	 */
	private final Stopwatch stopwatch;

	/**
	 * The device to use.
	 */
	private final EncogCLDevice device;

	/**
	 * THe workload to use.
	 */
	private final TrainingWorkload workload;

	/**
	 * Construct a gradient worker.
	 * 
	 * @param device
	 *            The device to execute on.
	 * @param network
	 *            The network to train.
	 * @param owner
	 *            The owner that is doing the training.
	 * @param training
	 *            The training data.
	 * @param low
	 *            The low index to use in the training data.
	 * @param high
	 *            The high index to use in the training data.
	 */
	public GradientWorkerCL(final EncogCLDevice device,
			final FlatNetwork network, final TrainFlatNetworkMulti owner,
			final Indexable training, final int low, final int high) {
		this.network = network;
		this.training = training;
		this.low = low;
		this.high = high;
		this.owner = owner;

		this.stopwatch = new Stopwatch();

		this.layerDelta = new double[network.getLayerOutput().length];
		this.gradients = new double[network.getWeights().length];
		this.actual = new double[network.getOutputCount()];

		this.weights = network.getWeights();
		this.layerIndex = network.getLayerIndex();
		this.layerCounts = network.getLayerCounts();
		this.weightIndex = network.getWeightIndex();
		this.layerOutput = network.getLayerOutput();

		this.pair = BasicNeuralDataPair.createPair(network.getInputCount(),
				network.getOutputCount());

		this.device = device;

		this.workload = new TrainingWorkload(device, network, training, high,
				low);
	}

	/**
	 * @return Elapsed time for the last iteration.
	 */
	public long getElapsedTime() {
		return this.elapsedTime;
	}

	/**
	 * @return The weights for this network.
	 */
	public double[] getWeights() {
		return this.weights;
	}

	/**
	 * Perform the gradient calculation for the specified index range.
	 */
	public void run() {
		this.stopwatch.reset();
		this.stopwatch.start();

		final KernelNetworkTrain k = this.device.getPlatform()
				.getNetworkTrain();

		k.calculate(this.workload);

		for (int j = 0; j < this.gradients.length; j++) {
			this.gradients[j] = 0;
		}

		double e = 0;
		int index = 0;
		int errorIndex = 0;

		for (int i = 0; i < this.workload.getMaxUnits(); i++) {
			e += this.workload.getErrors()[errorIndex++];

			for (int j = 0; j < this.gradients.length; j++) {
				this.gradients[j] += this.workload.getGradients()[index++];
			}
		}

		final int count = (this.high - this.low) + 1;
		final double error = Math.sqrt(e
				/ (count * this.training.getIdealSize()));
		this.owner.report(this.gradients, error);

		this.stopwatch.stop();
		this.elapsedTime = this.stopwatch.getElapsedTicks();
	}
}
