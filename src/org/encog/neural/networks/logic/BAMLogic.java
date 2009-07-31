package org.encog.neural.networks.logic;

import org.encog.neural.data.NeuralData;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.NeuralDataMapping;
import org.encog.neural.networks.NeuralOutputHolder;
import org.encog.neural.networks.layers.Layer;
import org.encog.neural.networks.synapse.Synapse;
import org.encog.neural.pattern.BAMPattern;

public class BAMLogic implements NeuralLogic  {
	/**
	 * The Hopfield neural network.
	 */
	private BasicNetwork network;

	/**
	 * The input layer.
	 */
	private Layer inputLayer;

	/**
	 * The output layer.
	 */
	private Layer outputLayer;

	private Synapse synapseInputToOutput;
	private Synapse synapseOutputToInput;

	public int getInputNeurons() {
		return this.inputLayer.getNeuronCount();
	}

	public int getOutputNeurons() {
		return this.outputLayer.getNeuronCount();
	}

	public void addPattern(final NeuralData inputPattern,
			final NeuralData outputPattern) {

		int weight;

		for (int i = 0; i < getInputNeurons(); i++) {
			for (int j = 0; j < getOutputNeurons(); j++) {
				weight = (int) (inputPattern.getData(i) * outputPattern
						.getData(j));
				this.synapseInputToOutput.getMatrix().add(i, j, weight);
				this.synapseOutputToInput.getMatrix().add(j, i, weight);
			}
		}

	}

	/**
	 * Clear any connection weights.
	 */
	public void clear() {
		this.synapseInputToOutput.getMatrix().clear();
		this.synapseOutputToInput.getMatrix().clear();
	}

	/**
	 * @return The Hopfield network.
	 */
	public BasicNetwork getNetwork() {
		return network;
	}
	
	private double getWeight(Synapse synapse, NeuralData input, int x,int y)
	{
		if( synapse.getFromNeuronCount()!=input.size())
			return synapse.getMatrix().get(x, y);
		else
			return synapse.getMatrix().get(y, x);
	}

	private boolean propagateLayer(Synapse synapse, NeuralData input,
			NeuralData output) {
		int i, j;
		int sum, out = 0;
		boolean stable;

		stable = true;

		for (i = 0; i < output.size(); i++) {
			sum = 0;
			for (j = 0; j < input.size(); j++) {
				sum += getWeight(synapse,input,i,j) * input.getData(j);
			}
			if (sum != 0) {
				if (sum < 0)
					out = -1;
				else
					out = 1;
				if (out != (int) output.getData(i)) {
					stable = false;
					output.setData(i, out);
				}
			}
		}
		return stable;
	}

	public NeuralDataMapping compute(NeuralDataMapping input) {

		boolean stable1 = true, stable2 = true;

		do {
			
				stable1 = propagateLayer(this.synapseInputToOutput,
						input.getFrom(), input.getTo());
				stable2 = propagateLayer(this.synapseOutputToInput, input.getTo(),
						input.getFrom());

			
		} while (!stable1 && !stable2);
		return null;
	}

	public NeuralData compute(NeuralData input, NeuralOutputHolder useHolder) {
		// TODO Auto-generated method stub
		return null;
	}

	public void init(BasicNetwork network) {
		this.network = network;
		this.inputLayer = network.getInputLayer();
		this.outputLayer = network.getOutputLayer();
		this.synapseInputToOutput = network.getStructure().findSynapse(this.inputLayer, this.outputLayer, true);
		this.synapseOutputToInput =  network.getStructure().findSynapse(this.outputLayer, this.inputLayer, true);		
		
	}
}
