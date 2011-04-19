/*
 * Encog(tm) Core v3.0 - Java Version
 * http://www.heatonresearch.com/encog/
 * http://code.google.com/p/encog-java/
 
 * Copyright 2008-2011 Heaton Research, Inc.
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
import org.encog.neural.NeuralNetworkError;
import org.encog.neural.data.bipolar.BiPolarNeuralData;

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
	 * Tracks if there was no winner.
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
	
	private int f1Count;
	private int f2Count;
	
	private Matrix weightsF1toF2;
	private Matrix weightsF2toF1;

	public ART1()
	{
	
	}
	
	public ART1(int f1Count,int f2Count)
	{
		this.f1Count = f1Count;
		this.f2Count = f2Count;
		
		this.weightsF1toF2 = new Matrix(f1Count,f2Count);
		this.weightsF2toF1 = new Matrix(f2Count,f1Count);
		
		this.inhibitF2 = new boolean[f2Count];

		this.outputF1 = new BiPolarNeuralData(f1Count);
		this.outputF2 = new BiPolarNeuralData(f2Count);

		this.noWinner = f2Count;
		reset();
	}
	
	/**
	 * Adjust the weights for the pattern just presented.
	 */
	public void adjustWeights() {
		double magnitudeInput;

		for (int i = 0; i < this.f1Count; i++) {
			if (this.outputF1.getBoolean(i)) {
				magnitudeInput = magnitude(this.outputF1);
				weightsF1toF2.set(i, this.winner, 1);
				weightsF2toF1.set(this.winner, i,
						this.l / (this.l - 1 + magnitudeInput));
			} else {
				weightsF1toF2.set(i, this.winner, 0);
				weightsF2toF1.set(this.winner, i, 0);
			}
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
	public void compute(final BiPolarNeuralData input,
			final BiPolarNeuralData output) {
		int i;
		boolean resonance, exhausted;
		double magnitudeInput1, magnitudeInput2;

		for (i = 0; i < f2Count; i++) {
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
	public MLData compute(final MLData input) {
		if (!(input instanceof BiPolarNeuralData)) {
			throw new NeuralNetworkError("Input to ART1 logic network must be BiPolarNeuralData.");
		}

		final BiPolarNeuralData output = new BiPolarNeuralData(f1Count);
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

		for (int i = 0; i < f1Count; i++) {
			sum = weightsF1toF2.get(i, this.winner)
					* (this.outputF2.getBoolean(this.winner) ? 1 : 0);
			activation = ((input.getBoolean(i) ? 1 : 0) 
					+ this.d1 * sum - this.b1)
					/ (1 + this.a1
							* ((input.getBoolean(i) ? 1 : 0) + this.d1 * sum) 
							+ this.c1);
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
		for (i = 0; i < f2Count; i++) {
			if (!this.inhibitF2[i]) {
				sum = 0;
				for (j = 0; j < f1Count; j++) {
					sum += weightsF2toF1.get(i, j)
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
	public double getA1() {
		return this.a1;
	}

	/**
	 * @return The B1 parameter.
	 */
	public double getB1() {
		return this.b1;
	}

	/**
	 * @return The C1 parameter.
	 */
	public double getC1() {
		return this.c1;
	}

	/**
	 * @return The D1 parameter.
	 */
	public double getD1() {
		return this.d1;
	}

	/**
	 * @return The L parameter.
	 */
	public double getL() {
		return this.l;
	}

	/**
	 * Copy the output from the network to another object.
	 * 
	 * @param output
	 *            The target object for the output from the network.
	 */
	private void getOutput(final BiPolarNeuralData output) {
		for (int i = 0; i < f2Count; i++) {
			output.setData(i, this.outputF2.getBoolean(i));
		}
	}

	/**
	 * @return The vigilance parameter.
	 */
	public double getVigilance() {
		return this.vigilance;
	}

	/**
	 * @return The winning neuron.
	 */
	public int getWinner() {
		return this.winner;
	}

	/**
	 * @return Does this network have a "winner"?
	 */
	public boolean hasWinner() {
		return this.winner != this.noWinner;
	}


	/**
	 * Get the magnitude of the specified input.
	 * 
	 * @param input
	 *            The input to calculate the magnitude for.
	 * @return The magnitude of the specified pattern.
	 */
	public double magnitude(final BiPolarNeuralData input) {
		double result;

		result = 0;
		for (int i = 0; i < f1Count; i++) {
			result += input.getBoolean(i) ? 1 : 0;
		}
		return result;
	}

	/**
	 * Reset the weight matrix back to starting values.
	 */
	public void reset() {
		reset(0);
	}

	/**
	 * Set the A1 parameter.
	 * 
	 * @param a1
	 *            The new value.
	 */
	public void setA1(final double a1) {
		this.a1 = a1;
	}

	/**
	 * Set the B1 parameter.
	 * 
	 * @param b1
	 *            The new value.
	 */
	public void setB1(final double b1) {
		this.b1 = b1;
	}

	/**
	 * Set the C1 parameter.
	 * 
	 * @param c1
	 *            The new value.
	 */
	public void setC1(final double c1) {
		this.c1 = c1;
	}

	/**
	 * Set the D1 parameter.
	 * 
	 * @param d1
	 *            The new value.
	 */
	public void setD1(final double d1) {
		this.d1 = d1;
	}

	/**
	 * Set the input to the neural network.
	 * 
	 * @param input
	 *            The input.
	 */
	private void setInput(final BiPolarNeuralData input) {
		double activation;

		for (int i = 0; i < f1Count; i++) {
			activation = (input.getBoolean(i) ? 1 : 0)
					/ (1 + this.a1 * ((input.getBoolean(i) ? 1 : 0) 
							+ this.b1) + this.c1);
			this.outputF1.setData(i, (activation > 0));
		}
	}

	/**
	 * Set the L parameter.
	 * 
	 * @param l
	 *            The new value.
	 */
	public void setL(final double l) {
		this.l = l;
	}

	/**
	 * Set the vigilance.
	 * 
	 * @param vigilance
	 *            The new value.
	 */
	public void setVigilance(final double vigilance) {
		this.vigilance = vigilance;
	}
	
	public boolean supportsMapPersistence()
	{
		return true;
	}
	
	/**
	 * @return the weightsF1toF2
	 */
	public Matrix getWeightsF1toF2() {
		return weightsF1toF2;
	}

	/**
	 * @return the weightsF2toF1
	 */
	public Matrix getWeightsF2toF1() {
		return weightsF2toF1;
	}

	/**
	 * @return the f1Count
	 */
	public int getF1Count() {
		return f1Count;
	}

	/**
	 * @return the f2Count
	 */
	public int getF2Count() {
		return f2Count;
	}

	@Override
	public int getInputCount() {
		return this.f1Count;
	}

	@Override
	public int getOutputCount() {
		return this.f2Count;
	}

	@Override
	public int classify(MLData input) {
		BiPolarNeuralData input2 = new BiPolarNeuralData(this.f1Count);
		BiPolarNeuralData output = new BiPolarNeuralData(this.f2Count);
		
		if( input.size()!=input2.size() ) {
			throw new NeuralNetworkError("Input array size does not match.");
		}
		
		for(int i=0;i<input2.size();i++) {
			input2.setData(i, input.getData(i)>0);
		}
		
		this.compute(input2, output);
		
		if( this.hasWinner() )
			return this.winner;
		else
			return -1;
	}

	@Override
	public void reset(int seed) {
		for (int i = 0; i < f1Count; i++) {
			for (int j = 0; j < f2Count; j++) {
				weightsF1toF2.set(i, j,
						(this.b1 - 1) / this.d1 + 0.2);
				weightsF2toF1.set(
						j,
						i,
						this.l / (this.l - 1 + f1Count)
								- 0.1);
			}
		}		
	}

	public int getNoWinner() {
		return this.noWinner;
	}

	public void setF1Count(int i) {
		this.f1Count = i;	
		this.outputF1 = new BiPolarNeuralData(f1Count);
		
	}
	
	public void setF2Count(int i) {
		this.f2Count = i;		
		this.inhibitF2 = new boolean[f2Count];
		this.outputF2 = new BiPolarNeuralData(f2Count);
	}

	public void setNoWinner(int i) {
		this.noWinner = i;
		
	}

	public void setWeightsF1toF2(Matrix matrix) {
		this.weightsF1toF2 = matrix;		
	}
	
	public void setWeightsF2toF1(Matrix matrix) {
		this.weightsF2toF1 = matrix;		
	}

	
	
	
}
