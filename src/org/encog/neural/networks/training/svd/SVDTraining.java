package org.encog.neural.networks.training.svd;

import org.encog.engine.network.rbf.RadialBasisFunction;
import org.encog.engine.util.ObjectPair;
import org.encog.neural.data.NeuralDataPair;
import org.encog.neural.data.NeuralDataSet;
import org.encog.neural.data.basic.BasicNeuralData;
import org.encog.neural.data.basic.BasicNeuralDataPair;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.layers.Layer;
import org.encog.neural.networks.layers.RadialBasisFunctionLayer;
import org.encog.neural.networks.structure.FlatUpdateNeeded;
import org.encog.neural.networks.training.BasicTraining;
import org.encog.neural.networks.training.TrainingError;
import org.encog.neural.pattern.RadialBasisPattern;
import org.encog.util.simple.TrainingSetUtil;

/**
 * Train a RBF neural network using a SVD.
 * 
 * Contributed to Encog By M.Fletcher and M.Dean University of Cambridge, Dept.
 * of Physics, UK
 *
 */
public class SVDTraining extends BasicTraining {

	/**
	 * The network that is to be trained.
	 */
	private BasicNetwork network;

	/**
	 * The RBF layer we want to solve.
	 */
	private RadialBasisFunctionLayer rbfLayer;

	/**
	 * Construct the training object.
	 * @param network The network to train. Must have a single output
	 * neuron.
	 * @param training The training data to use. Must be
	 * indexable.
	 */
	public SVDTraining(BasicNetwork network, NeuralDataSet training) {
		Layer outputLayer = network.getLayer(BasicNetwork.TAG_OUTPUT);

		if (outputLayer == null) {
			throw new TrainingError("SVD requires an output layer.");
		}

		if (outputLayer.getNeuronCount() != 1) {
			throw new TrainingError(
					"SVD requires an output layer with a single neuron.");
		}

		if (network.getLayer(RadialBasisPattern.RBF_LAYER) == null)
			throw new TrainingError(
					"SVD is only tested to work on radial basis function networks.");

		rbfLayer = (RadialBasisFunctionLayer) network
				.getLayer(RadialBasisPattern.RBF_LAYER);

		this.setTraining(training);
		this.network = network;
	}

	/**
	 * @return The trained network.
	 */
	public BasicNetwork getNetwork() {
		return this.network;
	}

	/**
	 * Perform one iteration.
	 */
	public void iteration() {
		RadialBasisFunction[] funcs = new RadialBasisFunction[rbfLayer
				.getNeuronCount()];

		// Iteration over neurons and determine the necessaries
		for (int i = 0; i < rbfLayer.getNeuronCount(); i++) {
			RadialBasisFunction basisFunc = rbfLayer.getRadialBasisFunction()[i];

			funcs[i] = basisFunc;

			// This is the value that is changed using other training methods.
			// weights[i] =
			// network.Structure.Synapses[0].WeightMatrix.Data[i][j];
		}

		ObjectPair<double[][], double[][]> data = TrainingSetUtil
				.trainingToArray(getTraining());

		double[][] weights = network.getStructure().getSynapses().get(0)
				.getMatrix().getData();

		setError(SVD.svdfit(data.getA(), data.getB(), weights, funcs));

		this.network.getStructure().setFlatUpdate(FlatUpdateNeeded.Flatten);
	}
}
