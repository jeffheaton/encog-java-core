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
package org.encog.neural.cpn;

import org.encog.Encog;
import org.encog.mathutil.matrices.Matrix;
import org.encog.mathutil.randomize.ConsistentRandomizer;
import org.encog.ml.BasicML;
import org.encog.ml.MLError;
import org.encog.ml.MLRegression;
import org.encog.ml.MLResettable;
import org.encog.ml.data.MLData;
import org.encog.ml.data.MLDataSet;
import org.encog.ml.data.basic.BasicMLData;
import org.encog.util.simple.EncogUtility;

/**
 * Counterpropagation Neural Networks (CPN) were developed by Professor 
 * Robert Hecht-Nielsen in 1987. CPN neural networks are a hybrid neural 
 * network, employing characteristics of both a feedforward neural 
 * network and a self-organzing map (SOM). The CPN is composed of 
 * three layers, the input, the instar and the outstar. The connection 
 * from the input to the instar layer is competitive, with only one 
 * neuron being allowed to win. The connection between the instar and 
 * outstar is feedforward. The layers are trained separately, 
 * using instar training and outstar training. The CPN network is 
 * good at regression.
 *
 */
public class CPN extends BasicML implements MLRegression, 
	MLResettable, MLError {

	/**
	 * Serial id.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * The number of neurons in the input layer.
	 */
	private final int inputCount;
	
	/**
	 * The number of neurons in the instar, or hidden, layer.
	 */
	private final int instarCount;
	
	/**
	 * The number of neurons in the outstar, or output, layer.
	 */
	private final int outstarCount;
	
	/**
	 * The number of winning neurons.
	 */
	private final int winnerCount;

	/**
	 * The weights from the input to the instar layer.
	 */
	private final Matrix weightsInputToInstar;
	
	/**
	 * The weights from the instar to the outstar layer.
	 */
	private final Matrix weightsInstarToOutstar;

	/**
	 * Construct the counterpropagation neural network.
	 * @param theInputCount The number of input neurons.
	 * @param theInstarCount The number of instar neurons.
	 * @param theOutstarCount The number of outstar neurons.
	 * @param theWinnerCount The winner count.
	 */
	public CPN(final int theInputCount, final int theInstarCount,
			final int theOutstarCount, final int theWinnerCount) {
		this.inputCount = theInputCount;
		this.instarCount = theInstarCount;
		this.outstarCount = theOutstarCount;

		this.weightsInputToInstar = new Matrix(inputCount, instarCount);
		this.weightsInstarToOutstar = new Matrix(instarCount, outstarCount);
		this.winnerCount = theWinnerCount;
	}

	/**
	 * Calculate the error for this neural network.
	 * 
	 * @param data
	 *            The training set.
	 * @return The error percentage.
	 */
	@Override
	public final double calculateError(final MLDataSet data) {
		return EncogUtility.calculateRegressionError(this, data);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final MLData compute(final MLData input) {
		final MLData temp = computeInstar(input);
		return computeOutstar(temp);
	}

	/**
	 * Compute the instar layer.
	 * @param input The input.
	 * @return The output.
	 */
	public final MLData computeInstar(final MLData input) {
		final MLData result = new BasicMLData(this.instarCount);
		int w, i, j;
		double sum, sumWinners, maxOut;
		int winner = 0;
		final boolean[] winners = new boolean[this.instarCount];

		for (i = 0; i < this.instarCount; i++) {
			sum = 0;
			for (j = 0; j < this.inputCount; j++) {
				sum += this.weightsInputToInstar.get(j, i) * input.getData(j);
			}
			result.setData(i, sum);
			winners[i] = false;
		}
		sumWinners = 0;
		for (w = 0; w < this.winnerCount; w++) {
			maxOut = Double.MIN_VALUE;
			for (i = 0; i < this.instarCount; i++) {
				if (!winners[i] && (result.getData(i) > maxOut)) {
					winner = i;
					maxOut = result.getData(winner);
				}
			}
			winners[winner] = true;
			sumWinners += result.getData(winner);
		}
		for (i = 0; i < this.instarCount; i++) {
			if (winners[i]
					&& (Math.abs(sumWinners) > Encog.DEFAULT_DOUBLE_EQUAL)) {
				result.getData()[i] /= sumWinners;
			} else {
				result.getData()[i] = 0;
			}
		}

		return result;
	}

	/**
	 * Compute the outstar layer.
	 * @param input The input.
	 * @return The output.
	 */
	public final MLData computeOutstar(final MLData input) {
		final MLData result = new BasicMLData(this.outstarCount);

		double sum = 0;

		for (int i = 0; i < this.outstarCount; i++) {
			sum = 0;
			for (int j = 0; j < this.instarCount; j++) {
				sum += this.weightsInstarToOutstar.get(j, i) * input.getData(j);
			}
			result.setData(i, sum);
		}
		return result;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final int getInputCount() {
		return this.inputCount;
	}

	/**
	 * @return The instar count, same as the input count.
	 */
	public final int getInstarCount() {
		return this.instarCount;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final int getOutputCount() {
		return this.outstarCount;
	}

	/**
	 * @return The outstar count, same as the output count.
	 */
	public final int getOutstarCount() {
		return this.outstarCount;
	}

	/**
	 * @return The weights between the input and instar.
	 */
	public final Matrix getWeightsInputToInstar() {
		return this.weightsInputToInstar;
	}

	/**
	 * @return The weights between the instar and outstar.
	 */
	public final Matrix getWeightsInstarToOutstar() {
		return this.weightsInstarToOutstar;
	}

	/**
	 * @return The winner count.
	 */
	public final int getWinnerCount() {
		return this.winnerCount;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final void reset() {
		reset(0);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final void reset(final int seed) {
		final ConsistentRandomizer randomize = new ConsistentRandomizer(-1, 1,
				seed);
		randomize.randomize(this.weightsInputToInstar);
		randomize.randomize(this.weightsInstarToOutstar);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void updateProperties() {
		// unneeded
	}

}
