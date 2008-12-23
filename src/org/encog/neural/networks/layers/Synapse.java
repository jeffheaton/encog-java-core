package org.encog.neural.networks.layers;

import org.encog.matrix.Matrix;
import org.encog.neural.data.NeuralData;
import org.encog.neural.data.basic.BasicNeuralData;

public class Synapse {
	
	public Synapse(int sourceNeurons,int targetNeurons)
	{
		this.fire = new BasicNeuralData(sourceNeurons);
		this.matrix = new Matrix(sourceNeurons + 1, targetNeurons);
		
	}
	
	
	/**
	 * Results from the last time that the outputs were calculated for this
	 * layer.
	 */
	private NeuralData fire;

	/**
	 * The weight and threshold matrix.
	 */
	private Matrix matrix;
	
	/**
	 * Get the output array from the last time that the output of this layer was
	 * calculated.
	 * 
	 * @return The output array.
	 */
	public NeuralData getFire() {
		return this.fire;
	}

	/**
	 * Get the output from an individual neuron.
	 * 
	 * @param index
	 *            The neuron specified.
	 * @return The output from the specified neuron.
	 */
	public double getFire(final int index) {
		return this.fire.getData(index);
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
	 * Get the neuron count for this layer.
	 * 
	 * @return the neuronCount
	 */
	public int getNeuronCount() {
		return this.fire.size();
	}
	
	/**
	 * Determine if this layer has a matrix.
	 * 
	 * @return True if this layer has a matrix.
	 */
	public boolean hasMatrix() {
		return this.matrix != null;
	}
	
	/**
	 * Set the fire data.
	 * 
	 * @param fire
	 *            The fire data.
	 */
	public void setFire(final NeuralData fire) {
		this.fire = fire;
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
}
