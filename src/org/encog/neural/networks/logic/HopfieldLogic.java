package org.encog.neural.networks.logic;

import org.encog.matrix.Matrix;
import org.encog.matrix.MatrixMath;
import org.encog.neural.data.NeuralData;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.pattern.HopfieldPattern;

public class HopfieldLogic extends ThermalLogic {
	
	
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
	 * @param delta The amount to change the weights by.
	 */
	private void convertHopfieldMatrix(final Matrix delta) {
		// add the new weight matrix to what is there already
		for (int row = 0; row < delta.getRows(); row++) {
			for (int col = 0; col < delta.getRows(); col++) {
				this.getThermalSynapse().getMatrix().add(
						row, col, delta.get(row, col));
			}
		}
	}
	
	/**
	 * Perform one Hopfield iteration.
	 */
	public void run()
	{
		NeuralData temp = this.compute(this.getCurrentState(),null);
		for(int i=0;i<temp.size();i++)
		{
			this.getCurrentState().setData(i, temp.getData(i)>0 );
		}
	}
	
	/**
	 * Run the network until it becomes stable and does not change from
	 * more runs.
	 * @param max The maximum number of cycles to run before giving up.
	 * @return The number of cycles that were run.
	 */
	public int runUntilStable(int max)
	{
		boolean done = false;
		String lastStateStr = this.getCurrentState().toString();
		String currentStateStr = this.getCurrentState().toString();
		
		int cycle = 0;
		do
		{
			run();
			cycle++;
			
			lastStateStr = this.getCurrentState().toString();
			
			if( !currentStateStr.equals(lastStateStr) )
			{
				if( cycle>max )
					done = true;
			}
			else
				done = true;
			
			currentStateStr = lastStateStr;
			
		} while( !done );
		
		return cycle;
	}
}
