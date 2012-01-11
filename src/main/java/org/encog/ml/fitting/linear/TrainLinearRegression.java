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
package org.encog.ml.fitting.linear;

import org.encog.EncogError;
import org.encog.ml.MLMethod;
import org.encog.ml.TrainingImplementationType;
import org.encog.ml.data.MLDataPair;
import org.encog.ml.data.MLDataSet;
import org.encog.ml.train.BasicTraining;
import org.encog.neural.networks.training.propagation.TrainingContinuation;
import org.encog.util.simple.EncogUtility;


public class TrainLinearRegression extends BasicTraining {

	private final LinearRegression method; 
	private final MLDataSet training;
	
	public TrainLinearRegression(LinearRegression theMethod, MLDataSet theTraining) {
		super(theMethod.getInputCount()==1?TrainingImplementationType.OnePass:TrainingImplementationType.Iterative);
		this.method = theMethod;
		this.training = theTraining;
	}
	
	/**
	 * @return the training
	 */
	public MLDataSet getTraining() {
		return training;
	}



	@Override
	public void iteration() {
		int m = (int)this.training.getRecordCount();
		double sumX = 0;
		double sumY = 0;
		double sumXY = 0;
		double sumX2 = 0;
		
		for(MLDataPair pair: this.training) {
			sumX+=pair.getInputArray()[0];
			sumY+=pair.getIdealArray()[0];
			sumX2+=Math.pow(pair.getInputArray()[0], 2);
			sumXY+=pair.getInputArray()[0]*pair.getIdealArray()[0];
		}
		
		this.method.getWeights()[1] = ((m*sumXY)-(sumX*sumY))/((m*sumX2)-Math.pow(sumX, 2));
		this.method.getWeights()[0] = ((1.0/m)*sumY)-( (this.method.getWeights()[1]/m) * sumX);
		
		this.setError(EncogUtility.calculateRegressionError(this.method, this.training));
	}

	@Override
	public boolean canContinue() {
		return false;
	}

	@Override
	public TrainingContinuation pause() {
		return null;
	}

	@Override
	public void resume(TrainingContinuation state) {
		throw new EncogError("Not supported");
	}

	@Override
	public MLMethod getMethod() {
		return this.method;
	}
	
}
