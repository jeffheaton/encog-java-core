package org.encog.neural.som.training.clustercopy;

import org.encog.ml.MLMethod;
import org.encog.ml.TrainingImplementationType;
import org.encog.ml.data.MLDataPair;
import org.encog.neural.data.MLDataArray;
import org.encog.neural.data.NeuralDataSet;
import org.encog.neural.networks.training.BasicTraining;
import org.encog.neural.networks.training.propagation.TrainingContinuation;
import org.encog.neural.som.SOM;

public class SOMClusterCopyTraining extends BasicTraining {

	private SOM network;
	
	public SOMClusterCopyTraining(SOM network, NeuralDataSet training) {
		super(TrainingImplementationType.OnePass);
		this.network = network;
		setTraining(training);
	}
	
	@Override
	public MLMethod getNetwork() {
		return this.network;
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
	private void copyInputPattern(final int outputNeuron, final MLDataArray input) {
		for (int inputNeuron = 0; inputNeuron < this.network.getInputCount();
			inputNeuron++) {
			this.network.getWeights().set(inputNeuron, outputNeuron,
					input.getData(inputNeuron));
		}
	}

	@Override
	public void iteration() {
		int outputNeuron = 0;
		for(MLDataPair pair: this.getTraining() ) {
			copyInputPattern(outputNeuron++,pair.getInput());
		}
	}

	@Override
	public boolean canContinue() {
		return false;
	}

	@Override
	public TrainingContinuation pause() {
		return null;
	}

	@Override
	public void resume(TrainingContinuation state) {
	}

}
