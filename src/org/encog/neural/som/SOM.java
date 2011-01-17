package org.encog.neural.som;

import org.encog.engine.util.EngineArray;
import org.encog.mathutil.matrices.Matrix;
import org.encog.mathutil.matrices.MatrixMath;
import org.encog.neural.data.NeuralData;
import org.encog.neural.data.basic.BasicNeuralData;
import org.encog.persist.BasicPersistedObject;

public class SOM extends BasicPersistedObject {
	/**
	 * Do not allow patterns to go below this very small number.
	 */
	public static final double VERYSMALL = 1.E-30;

	/**
	 * The weights of the output neurons base on the input from the input
	 * neurons.
	 */
	Matrix outputWeights;

	/**
	 * Output neuron activations
	 */
	protected double output[];

	/**
	 * Number of input neurons
	 */
	protected int inputNeuronCount;

	/**
	 * Number of output neurons
	 */
	protected int outputNeuronCount;

	/**
	 * The constructor.
	 * 
	 * @param inputCount
	 *            Number of input neurons
	 * @param outputCount
	 *            Number of output neurons
	 */
	public SOM(final int inputCount, final int outputCount) {

		this.inputNeuronCount = inputCount;
		this.outputNeuronCount = outputCount;
		this.outputWeights = new Matrix(this.outputNeuronCount,
				this.inputNeuronCount + 1);
		this.output = new double[this.outputNeuronCount];
	}

	/**
	 * Get the input neuron count.
	 * @return The input neuron count.
	 */
	public int getInputNeuronCount() {
		return this.inputNeuronCount;
	}

	/**
	 * Get the output neurons.
	 * @return The output neurons.
	 */
	public double[] getOutput() {
		return this.output;
	}

	/**
	 * Get the output neuron count.
	 * @return The output neuron count.
	 */
	public int getOutputNeuronCount() {
		return this.outputNeuronCount;
	}

	/**
	 * Get the output neuron weights.
	 * @return The output neuron weights.
	 */
	public Matrix getOutputWeights() {
		return this.outputWeights;
	}

	/**
	 * Set the output neuron weights.
	 * @param outputWeights The new output neuron weights.
	 */
	public void setOutputWeights(final Matrix outputWeights) {
		this.outputWeights = outputWeights;
	}

	/**
	 * Determine the winner for the specified input. This is the number of the
	 * winning neuron.
	 * @param input The input pattern.
	 * @return The winning neuron.
	 */
	public int winner(final NeuralData input) {

		NeuralData output = compute(input);
		int win = EngineArray.indexOfLargest(output.getData());		
		return win;
	}
	
	/**
	 * Determine the winner for the specified input. This is the number of the
	 * winning neuron.
	 * @param input The input pattern.
	 * @return The winning neuron.
	 */
	public NeuralData compute(final NeuralData input) {
		
		NeuralData result = new BasicNeuralData(this.outputNeuronCount);

		for (int i = 0; i < this.outputNeuronCount; i++) {
			final Matrix optr = this.outputWeights.getCol(i);
			final Matrix inputMatrix = Matrix.createRowMatrix(input.getData());
			result.setData(i,MatrixMath.dotProduct(inputMatrix, optr));
		}

		return result;
	}

	public Matrix getMatrix() {
		return this.outputWeights;
	}

	public void setMatrix(Matrix matrix) {
		this.outputWeights = matrix;
		
	}
}
