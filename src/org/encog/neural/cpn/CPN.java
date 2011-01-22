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
package org.encog.neural.cpn;

import org.encog.mathutil.matrices.Matrix;
import org.encog.ml.MLRegression;
import org.encog.neural.data.NeuralData;
import org.encog.neural.data.basic.BasicNeuralData;
import org.encog.persist.BasicPersistedObject;

public class CPN extends BasicPersistedObject implements MLRegression {

	private final int inputCount;
	private final int instarCount;
	private final int outstarCount;
	private final int winnerCount;

	private final Matrix weightsInputToInstar;
	private final Matrix weightsInstarToOutstar;

	public CPN(int inputCount, int instarCount, int outstarCount,
			int winnerCount) {
		this.inputCount = inputCount;
		this.instarCount = instarCount;
		this.outstarCount = outstarCount;

		this.weightsInputToInstar = new Matrix(inputCount, instarCount);
		this.weightsInstarToOutstar = new Matrix(instarCount, outstarCount);
		this.winnerCount = winnerCount;
	}

	public int getInputCount() {
		return inputCount;
	}

	public int getInstarCount() {
		return instarCount;
	}

	public int getOutstarCount() {
		return outstarCount;
	}

	@Override
	public int getOutputCount() {
		// TODO Auto-generated method stub
		return 0;
	}
	
	public NeuralData computeOutstar(NeuralData input)
	{
		NeuralData result = new BasicNeuralData(this.outstarCount);

		double Sum = 0;
		
		for (int i = 0; i < this.outstarCount; i++) {
			Sum = 0;
			for (int j = 0; j < this.instarCount; j++) {
				Sum += this.weightsInstarToOutstar.get(j, i) * input.getData(j);
			}
			result.setData(i, Sum);
		}
		return result;
	}
	
	public NeuralData computeInstar(NeuralData input)
	{
		NeuralData result = new BasicNeuralData(this.instarCount);
		int w, i, j;
		double Sum, SumWinners, MaxOut;
		int Winner = 0;
		boolean[] winners = new boolean[this.instarCount];

		for (i = 0; i < this.instarCount; i++) {
			Sum = 0;
			for (j = 0; j < this.inputCount; j++) {
				Sum += this.weightsInputToInstar.get(j, i) * input.getData(j);
			}
			result.setData(i, Sum);
			winners[i] = false;
		}
		SumWinners = 0;
		for (w = 0; w < winnerCount; w++) {
			MaxOut = Double.MIN_VALUE;
			for (i = 0; i < this.instarCount; i++) {
				if (!winners[i] && result.getData(i) > MaxOut)
					MaxOut = result.getData(Winner = i);
			}
			winners[Winner] = true;
			SumWinners += result.getData(Winner);
		}
		for (i = 0; i < this.instarCount; i++) {
			if (winners[i])
				result.getData()[i] /= SumWinners;
			else
				result.getData()[i] = 0;
		}		
		
		return result;
	}

	@Override
	public NeuralData compute(NeuralData input) {
		NeuralData temp = computeInstar(input);
		return computeOutstar(temp);
	}

	public Matrix getWeightsInputToInstar() {
		return weightsInputToInstar;
	}

	public Matrix getWeightsInstarToOutstar() {
		return weightsInstarToOutstar;
	}
	
	

}
