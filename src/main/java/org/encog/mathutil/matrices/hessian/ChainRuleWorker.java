package org.encog.mathutil.matrices.hessian;

import org.encog.engine.network.activation.ActivationFunction;
import org.encog.mathutil.matrices.Matrix;
import org.encog.ml.data.MLData;
import org.encog.ml.data.MLDataPair;
import org.encog.ml.data.MLDataSet;
import org.encog.neural.data.NeuralDataSet;
import org.encog.neural.flat.FlatNetwork;
import org.encog.neural.networks.BasicNetwork;
import org.encog.util.EngineArray;
import org.encog.util.concurrency.EngineTask;

public class ChainRuleWorker implements EngineTask {

	/**
	 * The actual values from the neural network.
	 */
	private double[] actual;

	/**
	 * The deltas for each layer.
	 */
	private double[] layerDelta;

	/**
	 * The neuron counts, per layer.
	 */
	private int[] layerCounts;

	/**
	 * The feed counts, per layer.
	 */
	private int[] layerFeedCounts;

	/**
	 * The layer indexes.
	 */
	private int[] layerIndex;

	/**
	 * The index to each layer's weights and thresholds.
	 */
	private int[] weightIndex;

	/**
	 * The output from each layer.
	 */
	private double[] layerOutput;
	
	/**
	 * The sums.
	 */
	private double[] layerSums;

	
	/**
	 * The weights and thresholds.
	 */
	private double[] weights;	
	
	private BasicNetwork network;
	
	private FlatNetwork flat;

	private double[] derivative;
	
	private MLDataSet training;
	
	private int outputNeuron;
	
	private double[] totDeriv;
	
	private double[] gradients;
	
	private double error;
	
	public ChainRuleWorker(BasicNetwork theNetwork, MLDataSet theTraining) {
		
		int weightCount = theNetwork.getStructure().getFlat().getWeights().length;
		
		this.training = theTraining;
		this.network = theNetwork;
		this.flat = this.network.getFlat();
		
		this.layerDelta = new double[flat.getLayerOutput().length];	
		this.actual = new double[flat.getOutputCount()];
		this.derivative = new double[weightCount];
		this.totDeriv = new double[weightCount];
		this.gradients = new double[weightCount];

		this.weights = flat.getWeights();
		this.layerIndex = flat.getLayerIndex();
		this.layerCounts = flat.getLayerCounts();
		this.weightIndex = flat.getWeightIndex();
		this.layerOutput = flat.getLayerOutput();
		this.layerSums = flat.getLayerSums();
		this.layerFeedCounts = flat.getLayerFeedCounts();
		
	}
	
	
	@Override
	public void run() {
		this.error = 0;
		EngineArray.fill(this.totDeriv, 0);
		EngineArray.fill(this.gradients, 0);
		
		
		// Loop over every training element
		for (final MLDataPair pair : this.training) {
			final MLData networkOutput = this.network.compute(pair
					.getInput());

			EngineArray.fill(this.derivative, 0);

			double e = pair.getIdeal().getData(outputNeuron) - networkOutput.getData(outputNeuron);
			this.error+=e*e;

			process(outputNeuron, pair.getInputArray(), pair.getIdealArray());

			// calculate gradients
			for (int i = 0; i < this.weights.length; i++) {
				this.gradients[i] += e * this.derivative[i];
				totDeriv[i] += this.derivative[i];
			}
		}
		
	}

	/**
	 * Process one training set element.
	 * 
	 * @param input
	 *            The network input.
	 * @param ideal
	 *            The ideal values.      
	 */
	private void process(int outputNeuron, final double[] input, final double[] ideal) {
				
		this.network.compute(input, this.actual);

		for (int i = 0; i < this.actual.length; i++) {

			if (i == outputNeuron) {
				this.layerDelta[i] = this.flat.getActivationFunctions()[0]
						.derivativeFunction(this.layerSums[i],
								this.layerOutput[i]);
			} else {
				this.layerDelta[i] = 0;
			}
		}

		for (int i = this.flat.getBeginTraining(); i < this.flat.getEndTraining(); i++) {
			processLevel(i);
		}
	}

	/**
	 * Process one level.
	 * 
	 * @param currentLevel
	 *            The level.
	 */
	private void processLevel(final int currentLevel) {
		final int fromLayerIndex = this.layerIndex[currentLevel + 1];
		final int toLayerIndex = this.layerIndex[currentLevel];
		final int fromLayerSize = this.layerCounts[currentLevel + 1];
		final int toLayerSize = this.layerFeedCounts[currentLevel];

		final int index = this.weightIndex[currentLevel];
		final ActivationFunction activation = this.flat
				.getActivationFunctions()[currentLevel + 1];

		// handle weights
		int yi = fromLayerIndex;
		for (int y = 0; y < fromLayerSize; y++) {
			final double output = this.layerOutput[yi];
			double sum = 0;
			int xi = toLayerIndex;
			int wi = index + y;
			for (int x = 0; x < toLayerSize; x++) {
				this.derivative[wi] += output * this.layerDelta[xi];
				sum += this.weights[wi] * this.layerDelta[xi];
				wi += fromLayerSize;
				xi++;
			}

			this.layerDelta[yi] = sum
					* (activation.derivativeFunction(this.layerSums[yi],this.layerOutput[yi]));
			yi++;
		}
	}


	/**
	 * @return the outputNeuron
	 */
	public int getOutputNeuron() {
		return outputNeuron;
	}


	/**
	 * @param outputNeuron the outputNeuron to set
	 */
	public void setOutputNeuron(int outputNeuron) {
		this.outputNeuron = outputNeuron;
	}
	
	public double[] getDerivative() {
		return this.derivative;
	}


	/**
	 * @return the gradients
	 */
	public double[] getGradients() {
		return gradients;
	}

	public double getError() {
		return this.error;
	}
	
}
