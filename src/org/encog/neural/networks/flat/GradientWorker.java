package org.encog.neural.networks.flat;

import java.util.Arrays;

import org.encog.mathutil.error.ErrorCalculation;
import org.encog.neural.data.Indexable;
import org.encog.neural.data.NeuralDataPair;
import org.encog.neural.data.basic.BasicNeuralDataPair;
import org.encog.util.concurrency.EncogTask;

public class GradientWorker implements EncogTask {

	/**
	 * The network to train.
	 */
	private final FlatNetwork network;
	
	/**
	 * The error calculation method.
	 */
	private final ErrorCalculation errorCalculation = new ErrorCalculation();
	
	/**
	 * The actual values from the neural network.
	 */
	private double[] actual;
	
	/**
	 * The deltas for each layer
	 */
	private final double[] layerDelta;
	
	/**
	 * The neuron counts, per layer.
	 */
	private final int[] layerCounts;
	
	/**
	 * The layer indexes
	 */
	private final int[] layerIndex;
	
	/**
	 * The index to each layer's weights and thresholds.
	 */
	private final int[] weightIndex;
	
	/**
	 * The output from each layer
	 */
	private final double[] layerOutput;
	
	/**
	 * The gradients
	 */
	private final double[] gradients;
	
	/**
	 * The weights and thresholds.
	 */
	private final double[] weights;
	
	private NeuralDataPair pair;
	
	private Indexable training;
	private int low;
	private int high;
	private TrainFlatNetworkMulti owner;
	
	public GradientWorker(FlatNetwork network, TrainFlatNetworkMulti owner, Indexable training, int low, int high )
	{
		this.network = network;
		this.training = training;
		this.low = low;
		this.high = high;
		this.owner = owner;
		
		layerDelta = new double[network.getLayerOutput().length];
		gradients = new double[network.getWeights().length];
		this.actual = new double[network.getOutputCount()];

		weights = network.getWeights();
		layerIndex = network.getLayerIndex();
		layerCounts = network.getLayerCounts();
		weightIndex = network.getWeightIndex();
		layerOutput = network.getLayerOutput();
		
		this.pair = BasicNeuralDataPair.createPair(network.getInputCount(), network.getOutputCount());
	}
	
	
	public double derivativeSigmoid(final double d) {
		return d * (1.0 - d);
	}

	public double derivativeTANH(final double d) {
		return ((1 + d) * (1 - d));
	}
	
	
	@Override
	public void run() {
		this.errorCalculation.reset();
		for(int i = this.low; i<high; i++)
		{
			this.training.getRecord(i, this.pair);
			process(pair.getInput().getData(),pair.getIdeal().getData());
		}
		this.owner.report(this.gradients, this.errorCalculation.calculateRMS());
		Arrays.fill(this.gradients, 0);
	}
	
	private void process(double[] input, double[] ideal)
	{
		network.calculate(input, actual);

		errorCalculation.updateError(actual, ideal);

		for (int i = 0; i < actual.length; i++) {
			if (network.isTanh()) {
				layerDelta[i] = derivativeTANH(actual[i])
						* (ideal[i] - actual[i]);
			} else {
				layerDelta[i] = derivativeSigmoid(actual[i])
						* (ideal[i] - actual[i]);
			}
		}

		for (int i = 0; i < layerCounts.length - 1; i++) {
			processLevel(i);
		}
	}
	
	private void processLevel(final int currentLevel) {
		final int fromLayerIndex = layerIndex[currentLevel + 1];
		final int toLayerIndex = layerIndex[currentLevel];
		final int fromLayerSize = layerCounts[currentLevel + 1];
		final int toLayerSize = layerCounts[currentLevel];

		// clear the to-deltas
		for (int i = 0; i < fromLayerSize; i++) {
			layerDelta[fromLayerIndex + i] = 0;
		}

		int index = weightIndex[currentLevel] + toLayerSize;

		for (int x = 0; x < toLayerSize; x++) {
			for (int y = 0; y < fromLayerSize; y++) {
				final double value = layerOutput[fromLayerIndex + y]
						* layerDelta[toLayerIndex + x];
				gradients[index] += value;
				layerDelta[fromLayerIndex + y] += weights[index]
						* layerDelta[toLayerIndex + x];
				index++;
			}
		}

		for (int i = 0; i < fromLayerSize; i++) {
			if (network.isTanh()) {
				layerDelta[fromLayerIndex + i] *= derivativeTANH(layerOutput[fromLayerIndex
						+ i]);
			} else {
				layerDelta[fromLayerIndex + i] *= derivativeSigmoid(layerOutput[fromLayerIndex
						+ i]);
			}
		}
	}


	public double[] getWeights() {
		return weights;
	}
	
	

}
