package org.encog.neural.networks.training.lma;

import java.util.List;

import org.encog.neural.activation.ActivationFunction;
import org.encog.neural.data.Indexable;
import org.encog.neural.data.NeuralData;
import org.encog.neural.data.NeuralDataPair;
import org.encog.neural.data.basic.BasicNeuralData;
import org.encog.neural.data.basic.BasicNeuralDataPair;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.NeuralOutputHolder;
import org.encog.neural.networks.layers.Layer;
import org.encog.neural.networks.synapse.Synapse;

public class ComputeJacobian {
	private BasicNetwork network;
	private Indexable indexableTraining;
	private int inputLength;
	private int parameterSize;
	private double[][] jacobian;
	private int jacobianRow;
	private int jacobianCol;
	private NeuralDataPair pair;
	private Layer outputLayer;
	private NeuralOutputHolder outputHolder;
	private double weightDerivatives[][][];
	private double thresholdsDerivatives[][];
	private double[] rowErrors;
	private double error;

	public ComputeJacobian(BasicNetwork network, Indexable indexableTraining) {
		this.indexableTraining = indexableTraining;
		this.network = network;
		this.outputLayer = network.getLayer(BasicNetwork.TAG_OUTPUT);
		this.parameterSize = network.getStructure().calculateSize();
		this.inputLength = (int) this.indexableTraining.getRecordCount();
		this.jacobian = new double[this.inputLength][this.parameterSize];
		this.rowErrors = new double[this.inputLength];

		this.outputHolder = new NeuralOutputHolder();

		BasicNeuralData input = new BasicNeuralData(this.indexableTraining
				.getInputSize());
		BasicNeuralData ideal = new BasicNeuralData(this.indexableTraining
				.getIdealSize());
		this.pair = new BasicNeuralDataPair(input, ideal);
	}

	public double calculate(double[] weights) {
		double result = 0.0;

		for (int i = 0; i < this.inputLength; i++) {
			this.jacobianRow = i;
			this.jacobianCol = 0;

			this.indexableTraining.getRecord(i, pair);

			double e = CalculateDerivatives(pair);
			this.rowErrors[i] = e;
			result += e * e;

		}

		return result / 2.0;
	}

	private double CalculateDerivatives(NeuralDataPair pair) {
		// error values
		double e = 0.0;
		double sum = 0.0;

		ActivationFunction function = this.network.getLayer(
				BasicNetwork.TAG_INPUT).getActivationFunction();

		NeuralOutputHolder holder = new NeuralOutputHolder();
		NeuralData actual = this.network.compute(pair.getInput(), holder);

		List<Synapse> synapses = this.network.getStructure().getSynapses();

		int synapseNumber = 0;
		
		Synapse synapse = synapses.get(synapseNumber++);

		double output = holder.getOutput().getData(0);
		e = pair.getIdeal().getData(0) - output;

		this.jacobian[this.jacobianRow][this.jacobianCol++] = this.calcDerivative(function, output);

		for (int i = 0; i < synapse.getFromNeuronCount(); i++) {
			double lastOutput = holder.getResult().get(synapse).getData(i);

			this.jacobian[this.jacobianRow][this.jacobianCol++] = 
				calcDerivative(function, lastOutput) * lastOutput;
		}

		while(synapseNumber<synapses.size()) {
			synapse = synapses.get(synapseNumber++);
			NeuralData outputData = holder.getResult().get(synapse);
			
			// for each neuron in the input layer
			for (int neuron = 0; neuron < synapse.getFromNeuronCount(); neuron++) {
				output = outputData.getData(neuron);

				int thresholdCol = this.jacobianCol++;

				// for each weight of the input neuron
				for (int i = 0; i < synapse.getFromNeuronCount(); i++) {
					sum = 0.0;
					// for each neuron in the next layer
					for (int j = 0; j < synapse.getToNeuronCount(); j++) {
						// for each weight of the next neuron
						for (int k = 0; k < synapse.getFromNeuronCount(); k++) {
							sum += synapse.getMatrix().get(k, j)
									* outputData.getData(k);
						}
						sum += synapse.getToLayer().getThreshold(j);
					}

					double w = synapse.getMatrix().get(neuron, i);
					double val = calcDerivative(function,output)*calcDerivative(function,sum)* w;

					this.jacobian[this.jacobianRow][this.jacobianCol++] = val
							* holder.getResult().get(synapse).getData(i);
					this.jacobian[this.jacobianRow][thresholdCol] = val;
				}
			}
		}

		// return error
		return e;
	}

	private double calcDerivative(ActivationFunction a, double d) {
		double[] temp = new double[1];
		temp[0] = d;
		a.activationFunction(temp);
		a.derivativeFunction(temp);
		return temp[0];
	}

	public double[][] getJacobian() {
		return this.jacobian;
	}

	public double[] getRowErrors() {
		return this.rowErrors;
	}

	public double getError() {
		return error;
	}
}
