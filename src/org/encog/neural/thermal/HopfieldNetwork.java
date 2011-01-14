package org.encog.neural.thermal;

import org.encog.mathutil.matrices.Matrix;
import org.encog.mathutil.matrices.MatrixMath;
import org.encog.neural.data.NeuralData;
import org.encog.persist.map.PersistConst;
import org.encog.persist.map.PersistedObject;

public class HopfieldNetwork extends ThermalNetwork {

	public HopfieldNetwork(int neuronCount) {
		super(neuronCount);
	}
	
	public HopfieldNetwork()
	{
		
	}
	
	/**
	 * Train the neural network for the specified pattern. The neural network
	 * can be trained for more than one pattern. To do this simply call the
	 * train method more than once.
	 * 
	 * @param pattern
	 *            The pattern to train for.
	 */
	public void addPattern(final NeuralData pattern) {

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
		convertHopfieldMatrix(m4);
	}

	/**
	 * Update the Hopfield weights after training.
	 * 
	 * @param delta
	 *            The amount to change the weights by.
	 */
	private void convertHopfieldMatrix(final Matrix delta) {
		// add the new weight matrix to what is there already
		for (int row = 0; row < delta.getRows(); row++) {
			for (int col = 0; col < delta.getRows(); col++) {
				addWeight(row, col,
						delta.get(row, col));
			}
		}
	}

	/**
	 * Perform one Hopfield iteration.
	 */
	public void run() {
		
		for(int toNeuron = 0; toNeuron<getNeuronCount() ; toNeuron++ )
		{
			double sum = 0;
			for(int fromNeuron = 0; fromNeuron<getNeuronCount() ; fromNeuron++)
			{
				sum += getCurrentState().getData(fromNeuron) * getWeight(fromNeuron,toNeuron);
			}
			getCurrentState().setData(toNeuron,sum);
		}
	}

	/**
	 * Run the network until it becomes stable and does not change from more
	 * runs.
	 * 
	 * @param max
	 *            The maximum number of cycles to run before giving up.
	 * @return The number of cycles that were run.
	 */
	public int runUntilStable(final int max) {
		boolean done = false;
		String lastStateStr = getCurrentState().toString();
		String currentStateStr = getCurrentState().toString();

		int cycle = 0;
		do {
			run();
			cycle++;

			lastStateStr = getCurrentState().toString();

			if (!currentStateStr.equals(lastStateStr)) {
				if (cycle > max) {
					done = true;
				}
			} else {
				done = true;
			}

			currentStateStr = lastStateStr;

		} while (!done);

		return cycle;
	}
	
	public boolean supportsMapPersistence()
	{
		return true;
	}
	
	public void persistToMap(PersistedObject obj)
	{
		obj.clear(PersistConst.TYPE_HOPFIELD);
		obj.setStandardProperties(this);
		obj.setProperty(PersistConst.WEIGHTS, this.getWeights());
		obj.setProperty(PersistConst.OUTPUT, this.getCurrentState().getData());
		obj.setProperty(PersistConst.NEURON_COUNT, this.getNeuronCount(),false);
	}
	
	public void persistFromMap(PersistedObject obj)
	{
		obj.requireType(PersistConst.TYPE_HOPFIELD);
		this.setWeights(obj.getPropertyDoubleArray(PersistConst.WEIGHTS,true));
	}

}
