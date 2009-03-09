package org.encog.neural.networks.training.hopfield;

import org.encog.matrix.Matrix;
import org.encog.matrix.MatrixMath;
import org.encog.neural.NeuralNetworkError;
import org.encog.neural.data.NeuralData;
import org.encog.neural.data.NeuralDataPair;
import org.encog.neural.data.NeuralDataSet;
import org.encog.neural.data.bipolar.BiPolarNeuralData;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.Network;
import org.encog.neural.networks.Train;
import org.encog.neural.networks.layers.BasicLayer;
import org.encog.neural.networks.layers.Layer;
import org.encog.neural.networks.synapse.Synapse;

public class TrainHopfield implements Train {

	private BasicNetwork network;
	private final NeuralDataSet trainingSet;
	
	public TrainHopfield(NeuralDataSet trainingSet, BasicNetwork network)
	{
		this.network = network;
		this.trainingSet = trainingSet;
	}
	
	public double getError() {
		return 0.0;
	}

	public Network getNetwork() {
		return network;
	}

	public void iteration() {
		/*for (final Layer layer : this.network.getLayers()) {
			Synapse recurrent = layer.getNextRecurrent();
			if( recurrent!=null ) {
				if( recurrent.getFromLayer()==recurrent.getToLayer()) {
					trainHopfieldSynapse(recurrent);
				}
			}
		}*/
	}		
	
	
	private void trainHopfieldSynapse(Synapse recurrent) {
		for(NeuralDataPair data: this.trainingSet)
		{
			trainHopfieldSynapse(recurrent,data.getInput());
		}
		
	}

	/**
	 * Train the neural network for the specified pattern. The neural network
	 * can be trained for more than one pattern. To do this simply call the
	 * train method more than once.
	 * 
	 * @param layer
	 *            The layer to train.
	 * @param pattern
	 * 		The pattern to train for.
	 */
	public void trainHopfieldSynapse(final Synapse synapse,
			final NeuralData pattern) {
		/*if (pattern.size() != synapse.getMatrix().getRows()) {
			throw new NeuralNetworkError("Can't train a pattern of size "
					+ pattern.size() + " on a hopfield network of size "
					+ synapse.getMatrix().getRows());
		}*/

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
		convertHopfieldMatrix(synapse,m4);
	}
	
	private void convertHopfieldMatrix(Synapse target, Matrix delta)
	{
		// add the new weight matrix to what is there already
		for(int row = 0; row< delta.getRows(); row++ )
		{
			for(int col = 0; col< delta.getRows(); col++ )
			{
				target.getMatrix().set(row,col,delta.get(row, col));
			}	
		}
		
		// set the threshold to zero, hopfield type nets do not use threshold
		for(int col = 0; col< delta.getRows(); col++ )
		{
			target.getMatrix().set(target.getFromNeuronCount(),col,0);
		}
		
	}

}
