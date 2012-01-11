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
package org.encog.util.benchmark;

import org.encog.ml.data.MLDataSet;
import org.encog.ml.data.basic.BasicMLDataSet;

/**
 * This benchmark implements a Fahlman Encoder.  Though probably not invented by Scott 
 * Fahlman, such encoders were used in many of his papers, particularly:
 * 
 * "An Empirical Study of Learning Speed in Backpropagation Networks" 
 * (Fahlman,1988)
 * 
 * It provides a very simple way of evaluating classification neural networks.
 *   Basically, the input and output neurons are the same in count.  However, 
 *   there is a smaller number of hidden neurons.  This forces the neural 
 *   network to learn to encode the patterns from the input neurons to a 
 *   smaller vector size, only to be expanded again to the outputs.
 * 
 * The training data is exactly the size of the input/output neuron count.  
 * Each training element will have a single column set to 1 and all other 
 * columns set to zero.  You can also perform in "complement mode", where 
 * the opposite is true.  In "complement mode" all columns are set to 1, 
 * except for one column that is 0.  The data produced in "complement mode" 
 * is more difficult to train.
 * 
 * Fahlman used this simple training data to benchmark neural networks when 
 * he introduced the Quickprop algorithm in the above paper.
 *
 */
public class EncoderTrainingFactory {
	
	/**
	 * Generate an encoder training set over the range [0.0,1.0].  This is the range used by
	 * Fahlman.
	 * @param inputCount The number of inputs and outputs.
	 * @param compl True if the complement mode should be use.
	 * @return The training set.
	 */
	public static MLDataSet generateTraining(int inputCount, boolean compl) {
		return generateTraining(inputCount,compl,0,1.0);
	}
	
	/**
	 * Generate an encoder over the specified range.
	 * @param inputCount The number of inputs and outputs.
	 * @param compl True if the complement mode should be use. 
	 * @param min The minimum value to use(i.e. 0 or -1)
	 * @param max The maximum value to use(i.e. 1 or 0)
	 * @return The training set.
	 */
	public static MLDataSet generateTraining(int inputCount, boolean compl, double min, double max) {		
		return generateTraining(inputCount,compl,min,max,min,max);
	}
	
	
	public static MLDataSet generateTraining(int inputCount, boolean compl, double inputMin, double inputMax, double outputMin, double outputMax) {
		double[][] input = new double[inputCount][inputCount];
		double[][] ideal = new double[inputCount][inputCount];

		for (int i = 0; i < inputCount; i++) {
			for (int j = 0; j < inputCount; j++) {
				if (compl) {
					input[i][j] = (j == i) ? inputMax : inputMin;
					ideal[i][j] = (j == i) ? outputMin : outputMax;					
				} else {
					input[i][j] = (j == i) ? inputMax : inputMin;
					ideal[i][j] = (j == i) ? inputMax : inputMin;
				}				
			}
		}
		return new BasicMLDataSet(input, ideal);
	}
}
