package org.encog.neural.networks.hopfield;

import org.encog.matrix.Matrix;
import org.encog.matrix.MatrixMath;
import org.encog.neural.NeuralNetworkError;
import org.encog.neural.data.NeuralData;
import org.encog.neural.data.NeuralDataPair;
import org.encog.neural.data.NeuralDataSet;
import org.encog.neural.data.basic.BasicNeuralDataPair;
import org.encog.neural.data.basic.BasicNeuralDataSet;
import org.encog.neural.networks.Layer;
import org.encog.neural.networks.Train;
import org.encog.neural.networks.feedforward.FeedforwardNetwork;

public class TrainHopfield implements Train {

	private NeuralDataSet trainingSet;
	private FeedforwardNetwork network;

	public TrainHopfield(NeuralDataSet trainingSet, FeedforwardNetwork network) {
		this.trainingSet = trainingSet;
		this.network = network;
	}

	public TrainHopfield(NeuralData pattern, FeedforwardNetwork network) {
		this.network = network;
		this.trainingSet = new BasicNeuralDataSet();
		this.trainingSet.add(new BasicNeuralDataPair(pattern));
	}

	@Override
	public double getError() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public FeedforwardNetwork getNetwork() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void iteration() {
		for (Layer layer : this.network.getLayers()) {
			if (layer instanceof HopfieldLayer) {
				for (NeuralDataPair pair : this.trainingSet) {
					this.trainHopfieldLayer((HopfieldLayer)layer, pair.getInput());
				}
			}
		}
	}

	/**
	 * Train the neural network for the specified pattern. The neural network
	 * can be trained for more than one pattern. To do this simply call the
	 * train method more than once.
	 * 
	 * @param pattern
	 *            The pattern to train on.
	 * @throws HopfieldException
	 *             The pattern size must match the size of this neural network.
	 */
	public void trainHopfieldLayer(final HopfieldLayer layer,
			final NeuralData pattern) {
		if (pattern.size() != layer.getMatrix().getRows()) {
			throw new NeuralNetworkError("Can't train a pattern of size "
					+ pattern.size() + " on a hopfield network of size "
					+ layer.getMatrix().getRows());
		}

		// Create a row matrix from the input, convert boolean to bipolar
		final Matrix m2 = Matrix.createRowMatrix(pattern.getData());
		// Transpose the matrix and multiply by the original input matrix
		final Matrix m1 = MatrixMath.transpose(m2);
		final Matrix m3 = MatrixMath.multiply(m1, m2);

		// matrix 3 should be square by now, so create an identity
		// matrix of the same size.
		final Matrix identity = MatrixMath.identity(m3.getRows());

		// subtract the identity matrix
		final Matrix m4 = MatrixMath.subtract(m3, identity);

		// now add the calculated matrix, for this pattern, to the
		// existing weight matrix.
		layer.setMatrix(MatrixMath.add(layer.getMatrix(), m4));
	}
}
