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
package org.encog.mathutil.matrices.hessian;

import org.encog.engine.network.activation.ActivationFunction;
import org.encog.mathutil.error.ErrorCalculation;
import org.encog.mathutil.matrices.Matrix;
import org.encog.ml.data.MLData;
import org.encog.ml.data.MLDataPair;
import org.encog.ml.data.MLDataSet;
import org.encog.neural.networks.BasicNetwork;
import org.encog.util.EngineArray;

/**
 * Calculate the Hessian matrix using the chain rule method. 
 * 
 */
public class HessianCR extends BasicHessian {

	private ChainRuleWorker worker;
	
	
	/**
	 * {@inheritDoc}
	 */
	public void init(BasicNetwork theNetwork, MLDataSet theTraining) {
		
		super.init(theNetwork,theTraining);
		int weightCount = theNetwork.getStructure().getFlat().getWeights().length;
		
		this.training = theTraining;
		this.network = theNetwork;
		
		this.hessianMatrix = new Matrix(weightCount,weightCount);
		this.hessian = this.hessianMatrix.getData();
		this.worker = new ChainRuleWorker(theNetwork,theTraining);
	}
	
	/**
	 * {@inheritDoc}
	 */
	public void compute() {
		double e = 0;
		int weightCount = this.network.getFlat().getWeights().length;
		
		for (int outputNeuron = 0; outputNeuron < this.network.getOutputCount(); outputNeuron++) {
		
			worker.setOutputNeuron(outputNeuron);
			worker.run();
			e+=worker.getError();
			
			for(int i=0;i<weightCount;i++) {
				this.gradients[i] += worker.getGradients()[i];
			}
			updateHessian(worker.getDerivative());			
		}
		
		sse= e/2;
	}
}
