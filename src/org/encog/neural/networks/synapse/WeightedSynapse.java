package org.encog.neural.networks.synapse;

import org.encog.matrix.Matrix;
import org.encog.neural.networks.Layer;

public class WeightedSynapse extends BasicSynapse {



	/**
	 * The weight and threshold matrix.
	 */
	private Matrix matrix;
	
	public WeightedSynapse(Layer fromLayer,Layer toLayer)
	{
		this.setFromLayer(fromLayer);
		this.setToLayer(toLayer);	
		this.matrix = new Matrix(getFromNeuronCount() + 1, getToNeuronCount());		
	}
	
	/**
	 * Get the weight and threshold matrix.
	 * 
	 * @return The weight and threshold matrix.
	 */
	public Matrix getMatrix() {
		return this.matrix;
	}

	/**
	 * Get the size of the matrix, or zero if one is not defined.
	 * 
	 * @return The size of the matrix.
	 */
	public int getMatrixSize() {
		if (this.matrix == null) {
			return 0;
		}
		return this.matrix.size();
	}
	

	/**
	 * Assign a new weight and threshold matrix to this layer.
	 * 
	 * @param matrix
	 *            The new matrix.
	 */
	public void setMatrix(final Matrix matrix) {
		this.matrix = matrix;

	}
	

	public String toString()
	{
		StringBuilder result = new StringBuilder();
		result.append("[Synapse: from=");
		result.append(this.getFromNeuronCount());
		result.append(",to=");
		result.append(this.getToNeuronCount());
		result.append("]");
		return result.toString();
	}

}
