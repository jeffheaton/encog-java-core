/*
 * Encog(tm) Core v3.1 - Java Version
 * http://www.heatonresearch.com/encog/
 * http://code.google.com/p/encog-java/
 
 * Copyright 2008-2012 Heaton Research, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *   
 * For more information on Heaton Research copyrights, licenses 
 * and trademarks visit:
 * http://www.heatonresearch.com/copyright
 */
package org.encog.neural.art;

import org.encog.mathutil.matrices.Matrix;
import org.encog.ml.MLClassification;
import org.encog.ml.MLResettable;
import org.encog.ml.data.MLData;
import org.encog.ml.data.specific.BiPolarNeuralData;
import org.encog.neural.NeuralNetworkError;

/**
 * Implements an ART1 neural network. An ART1 neural network is trained to
 * recognize bipolar patterns as it is presented data. There is no distinct
 * learning phase, like there is with other neural network types.
 * 
 * The ART1 neural network is a type of Adaptive Resonance Theory (ART) neural 
 * network. ART1 was developed by Stephen Grossberg and Gail Carpenter. 
 * This neural network type supports only bipolar input. The ART1 neural 
 * network is trained as it is used. New patterns are presented to the ART1 
 * network, and they are classified into either new, or existing, classes. 
 * Once the maximum number of classes have been used the network will report 
 * that it is out of classes. ART1 neural networks are used for classification. 
 *
 * There are essentially 2 layers in an ART1 network. The first, named the 
 * F1 layer, acts as the input. The F1 layer receives bipolar patterns that 
 * the network is to classify. The F2 layer specifies the maximum number 
 * classes that the ART1 network can recognize. 
 *
 * Plasticity is an important part for all Adaptive Resonance Theory (ART) 
 * neural networks. Unlike most neural networks, ART1 does not have a 
 * distinct training and usage stage. The ART1 network will learn as it is 
 * used. 


 */
public class ART1 extends ART implements MLResettable, MLClassification {

	/**
	 * Serial id.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * last winner in F2 layer.
	 */
	private int winner;

	/**
	 * A parameter for F1 layer.
	 */
	private double a1 = 1;

	/**
	 * B parameter for F1 layer.
	 */
	private double b1 = 1.5;

	/**
	 * C parameter for F1 layer.
	 */
	private double c1 = 5;

	/**
	 * D parameter for F1 layer.
	 */
	private double d1 = 0.9;

	/**
	 * L parameter for net.
	 */
	private double l = 3;

	/**
	 * The vigilance parameter.
	 */
	private double vigilance = 0.9;

	/**
	 * Allows members of the F2 layer to be inhibited.
	 */
	private transient boolean[] inhibitF2;

	/**
	 * This is the value that is returned if there is no winner.  
	 * This value is generally set to the number of classes, plus 1.
	 */
	private int noWinner;

	/**
	 * The output from the F1 layer.
	 */
	private BiPolarNeuralData outputF1;

	/**
	 * The output from the F2 layer.
	 */
	private BiPolarNeuralData outputF2;

	/**
	 * The F1 layer neuron count.
	 */
	private int f1Count;

	/**
	 * The F2 layer neuron count.
	 */
	private int f2Count;

	/**
	 * Weights from f1 to f2.
	 */
	private Matrix weightsF1toF2;

	/**
	 * Weights from f2 to f1.
	 */
	private Matrix weightsF2toF1;

	/**
	 * Default constructor, used mainly for persistence.
	 */
	public ART1() {

	}

	/**
	 * Construct the ART1 network.
	 * 
	 * @param theF1Count
	 *            The neuron count for the f1 layer.
	 * @param theF2Count
	 *            The neuron count for the f2 layer.
	 */
	public ART1(final int theF1Count, final int theF2Count) {
		this.f1Count = theF1Count;
		this.f2Count = theF2Count;

		this.weightsF1toF2 = new Matrix(this.f1Count, this.f2Count);
		this.weightsF2toF1 = new Matrix(this.f2Count, this.f1Count);

		this.inhibitF2 = new boolean[this.f2Count];

		this.outputF1 = new BiPolarNeuralData(this.f1Count);
		this.outputF2 = new BiPolarNeuralData(this.f2Count);

		this.noWinner = this.f2Count;
		reset();
	}

	/**
	 * Adjust the weights for the pattern just presented.
	 */
	public final void adjustWeights() {
		double magnitudeInput;

		for (int i = 0; i < this.f1Count; i++) {
			if (this.outputF1.getBoolean(i)) {
				magnitudeInput = magnitude(this.outputF1);
				this.weightsF1toF2.set(i, this.winner, 1);
				this.weightsF2toF1.set(this.winner, i, this.l
						/ (this.l - 1 + magnitudeInput));
			} else {
				this.weightsF1toF2.set(i, this.winner, 0);
				this.weightsF2toF1.set(this.winner, i, 0);
			}
		}
	}

	/**
	 * Classify the input data to a class number.
	 * 
	 * @param input
	 *            The input data.
	 * @return The class that the data belongs to.
	 */
	@Override
	public final int classify(final MLData input) {
		final BiPolarNeuralData input2 = new BiPolarNeuralData(this.f1Count);
		final BiPolarNeuralData output = new BiPolarNeuralData(this.f2Count);

		if (input.size() != input2.size()) {
			throw new NeuralNetworkError("Input array size does not match.");
		}

		for (int i = 0; i < input2.size(); i++) {
			input2.setData(i, input.getData(i) > 0);
		}

		this.compute(input2, output);

		if (hasWinner()) {
			return this.winner;
		} else {
			return -1;
		}
	}

	/**
	 * Compute the output from the ART1 network. This can be called directly or
	 * used by the BasicNetwork class. Both input and output should be bipolar
	 * numbers.
	 * 
	 * @param input
	 *            The input to the network.
	 * @param output
	 *            The output from the network.
	 */
	public final void compute(final BiPolarNeuralData input,
			final BiPolarNeuralData output) {
		int i;
		boolean resonance, exhausted;
		double magnitudeInput1, magnitudeInput2;

		for (i = 0; i < this.f2Count; i++) {
			this.inhibitF2[i] = false;
		}
		resonance = false;
		exhausted = false;
		do {
			setInput(input);
			computeF2();
			getOutput(output);
			if (this.winner != this.noWinner) {
				computeF1(input);
				magnitudeInput1 = magnitude(input);
				magnitudeInput2 = magnitude(this.outputF1);
				if ((magnitudeInput2 / magnitudeInput1) < this.vigilance) {
					this.inhibitF2[this.winner] = true;
				} else {
					resonance = true;
				}
			} else {
				exhausted = true;
			}
		} while (!(resonance || exhausted));
		if (resonance) {
			adjustWeights();
		}
	}

	/**
	 * Compute the output for the BasicNetwork class.
	 * 
	 * @param input
	 *            The input to the network.
	 * @return The output from the network.
	 */
	public final MLData compute(final MLData input) {
		if (!(input instanceof BiPolarNeuralData)) {
			throw new NeuralNetworkError(
					"Input to ART1 logic network must be BiPolarNeuralData.");
		}

		final BiPolarNeuralData output = new BiPolarNeuralData(this.f1Count);
		compute((BiPolarNeuralData) input, output);
		return output;
	}

	/**
	 * Compute the output from the F1 layer.
	 * 
	 * @param input
	 *            The input to the F1 layer.
	 */
	private void computeF1(final BiPolarNeuralData input) {
		double sum, activation;

		for (int i = 0; i < this.f1Count; i++) {
			sum = this.weightsF1toF2.get(i, this.winner)
					* (this.outputF2.getBoolean(this.winner) ? 1 : 0);
			activation = ((input.getBoolean(i) ? 1 : 0) + this.d1 * sum - this.b1)
					/ (1 + this.a1
							* ((input.getBoolean(i) ? 1 : 0) + this.d1 * sum) + this.c1);
			this.outputF1.setData(i, activation > 0);
		}
	}

	/**
	 * Compute the output from the F2 layer.
	 */
	private void computeF2() {
		int i, j;
		double sum, maxOut;

		maxOut = Double.NEGATIVE_INFINITY;
		this.winner = this.noWinner;
		for (i = 0; i < this.f2Count; i++) {
			if (!this.inhibitF2[i]) {
				sum = 0;
				for (j = 0; j < this.f1Count; j++) {
					sum += this.weightsF2toF1.get(i, j)
							* (this.outputF1.getBoolean(j) ? 1 : 0);
				}
				if (sum > maxOut) {
					maxOut = sum;
					this.winner = i;
				}
			}
			this.outputF2.setData(i, false);
		}
		if (this.winner != this.noWinner) {
			this.outputF2.setData(this.winner, true);
		}
	}

	/**
	 * @return The A1 parameter.
	 */
	public final double getA1() {
		return this.a1;
	}

	/**
	 * @return The B1 parameter.
	 */
	public final double getB1() {
		return this.b1;
	}

	/**
	 * @return The C1 parameter.
	 */
	public final double getC1() {
		return this.c1;
	}

	/**
	 * @return The D1 parameter.
	 */
	public final double getD1() {
		return this.d1;
	}

	/**
	 * @return the f1Count
	 */
	public final int getF1Count() {
		return this.f1Count;
	}

	/**
	 * @return the f2Count
	 */
	public final int getF2Count() {
		return this.f2Count;
	}

	@Override
	public final int getInputCount() {
		return this.f1Count;
	}

	/**
	 * @return The L parameter.
	 */
	public final double getL() {
		return this.l;
	}

	/**
	 * @return This is the value that is returned if there is no winner.  
	 * This value is generally set to the index of the last classes, plus 1.
	 * For example, if there were 3 classes, the network would return 0-2 to
	 * represent what class was found, in this case the no winner property
	 * would be set to 3.
	 */
	public final int getNoWinner() {
		return this.noWinner;
	}

	/**
	 * Copy the output from the network to another object.
	 * 
	 * @param output
	 *            The target object for the output from the network.
	 */
	private void getOutput(final BiPolarNeuralData output) {
		for (int i = 0; i < this.f2Count; i++) {
			output.setData(i, this.outputF2.getBoolean(i));
		}
	}

	/**
	 * @return The number of neurons in the output count, which is the f2 layer
	 *         count.
	 */
	@Override
	public final int getOutputCount() {
		return this.f2Count;
	}

	/**
	 * @return The vigilance parameter.
	 */
	public final double getVigilance() {
		return this.vigilance;
	}

	/**
	 * @return the weightsF1toF2
	 */
	public final Matrix getWeightsF1toF2() {
		return this.weightsF1toF2;
	}

	/**
	 * @return the weightsF2toF1
	 */
	public final Matrix getWeightsF2toF1() {
		return this.weightsF2toF1;
	}

	/**
	 * @return The winning neuron.
	 */
	public final int getWinner() {
		return this.winner;
	}

	/**
	 * @return Does this network have a "winner"?
	 */
	public final boolean hasWinner() {
		return this.winner != this.noWinner;
	}

	/**
	 * Get the magnitude of the specified input.
	 * 
	 * @param input
	 *            The input to calculate the magnitude for.
	 * @return The magnitude of the specified pattern.
	 */
	public final double magnitude(final BiPolarNeuralData input) {
		double result;

		result = 0;
		for (int i = 0; i < this.f1Count; i++) {
			result += input.getBoolean(i) ? 1 : 0;
		}
		return result;
	}

	/**
	 * Reset the weight matrix back to starting values.
	 */
	@Override
	public final void reset() {
		reset(0);
	}

	/**
	 * Reset with a specic seed.
	 * @param seed The seed to reset with.
	 */
	@Override
	public final void reset(final int seed) {
		for (int i = 0; i < this.f1Count; i++) {
			for (int j = 0; j < this.f2Count; j++) {
				this.weightsF1toF2.set(i, j, (this.b1 - 1) / this.d1 + 0.2);
				this.weightsF2toF1.set(j, i, this.l
						/ (this.l - 1 + this.f1Count) - 0.1);
			}
		}
	}

	/**
	 * Set the A1 parameter.
	 * 
	 * @param theA1
	 *            The new value.
	 */
	public final void setA1(final double theA1) {
		this.a1 = theA1;
	}

	/**
	 * Set the B1 parameter.
	 * 
	 * @param theB1
	 *            The new value.
	 */
	public final void setB1(final double theB1) {
		this.b1 = theB1;
	}

	/**
	 * Set the C1 parameter.
	 * 
	 * @param theC1
	 *            The new value.
	 */
	public final void setC1(final double theC1) {
		this.c1 = theC1;
	}

	/**
	 * Set the D1 parameter.
	 * 
	 * @param theD1
	 *            The new value.
	 */
	public final void setD1(final double theD1) {
		this.d1 = theD1;
	}

	/**
	 * Set the F1 count.  The F1 layer is the input layer.
	 * @param i The count.
	 */
	public final void setF1Count(final int i) {
		this.f1Count = i;
		this.outputF1 = new BiPolarNeuralData(this.f1Count);

	}

	/**
	 * Set the F2 count.  The F2 layer is the output layer.
	 * @param i The count.
	 */
	public final void setF2Count(final int i) {
		this.f2Count = i;
		this.inhibitF2 = new boolean[this.f2Count];
		this.outputF2 = new BiPolarNeuralData(this.f2Count);
	}

	/**
	 * Set the input to the neural network.
	 * 
	 * @param input
	 *            The input.
	 */
	private void setInput(final BiPolarNeuralData input) {
		double activation;

		for (int i = 0; i < this.f1Count; i++) {
			activation = (input.getBoolean(i) ? 1 : 0)
					/ (1 + this.a1 * ((input.getBoolean(i) ? 1 : 0) + this.b1) + this.c1);
			this.outputF1.setData(i, (activation > 0));
		}
	}

	/**
	 * Set the L parameter.
	 * 
	 * @param theL
	 *            The new value.
	 */
	public final void setL(final double theL) {
		this.l = theL;
	}

	/**
	 * Set the i parameter.
	 * 
	 * @param i
	 *            The new value.
	 */
	public final void setNoWinner(final int i) {
		this.noWinner = i;

	}

	/**
	 * Set the vigilance.
	 * 
	 * @param theVigilance
	 *            The new value.
	 */
	public final void setVigilance(final double theVigilance) {
		this.vigilance = theVigilance;
	}

	/**
	 * Set the f1 to f2 matrix.
	 * @param matrix The new matrix.
	 */
	public final void setWeightsF1toF2(final Matrix matrix) {
		this.weightsF1toF2 = matrix;
	}

	/**
	 * Set the f2 to f1 matrix.
	 * @param matrix The new matrix.
	 */
	public final void setWeightsF2toF1(final Matrix matrix) {
		this.weightsF2toF1 = matrix;
	}
}
