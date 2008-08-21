package org.encog.neural.networks.layers;

import org.encog.matrix.Matrix;
import org.encog.matrix.MatrixMath;
import org.encog.neural.data.NeuralData;
import org.encog.neural.data.bipolar.BiPolarNeuralData;
import org.encog.neural.persist.EncogPersistedObject;



/**
 * HopfieldNetwork: This class implements a Hopfield neural network.
 * A Hopfield neural network is fully connected and consists of a 
 * single layer.  Hopfield neural networks are usually used for 
 * pattern recognition.    
 */
public class HopfieldLayer extends BasicLayer implements EncogPersistedObject {


	public HopfieldLayer(final int size) {
		super(size);
		this.setFire(new BiPolarNeuralData(size));
		this.setMatrix(new Matrix(size, size));		
	}

	/**
	 * Present a pattern to the neural network and receive the result.
	 * 
	 * @param pattern
	 *            The pattern to be presented to the neural network.
	 * @return The output from the neural network.
	 * @throws HopfieldException
	 *             The pattern caused a matrix math error.
	 */
	public NeuralData compute(final NeuralData pattern) {		

		// convert the input pattern into a matrix with a single row.
		// also convert the boolean values to bipolar(-1=false, 1=true)
		final Matrix inputMatrix = Matrix.createRowMatrix(pattern.getData());

		// Process each value in the pattern
		for (int col = 0; col < pattern.size(); col++) {
			Matrix columnMatrix = this.getMatrix().getCol(col);
			columnMatrix = MatrixMath.transpose(columnMatrix);

			// The output for this input element is the dot product of the
			// input matrix and one column from the weight matrix.
			final double dotProduct = MatrixMath.dotProduct(inputMatrix,
					columnMatrix);

			// Convert the dot product to either true or false.
			if (dotProduct > 0) {
				this.getFire().setData(col,true);
			} else {
				this.getFire().setData(col,false);
			}
		}

		return this.getFire();
	}

	public String getName() {
		return "HopfieldNetwork";
	}
	
	@Override
	public BiPolarNeuralData getFire()
	{
		return (BiPolarNeuralData)super.getFire();
	}
}