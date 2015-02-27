/*
 * Encog(tm) Core v3.3 - Java Version
 * http://www.heatonresearch.com/encog/
 * https://github.com/encog/encog-java-core
 
 * Copyright 2008-2014 Heaton Research, Inc.
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
package org.encog.neural.networks;

import junit.framework.TestCase;

import org.encog.ml.data.MLDataSet;
import org.encog.neural.networks.training.propagation.resilient.ResilientPropagation;
import org.encog.neural.pattern.ElmanPattern;
import org.encog.neural.pattern.JordanPattern;
import org.encog.util.benchmark.RandomTrainingFactory;

public class TestSRN  extends TestCase {
	
	public void performElmanTest(int input, int hidden, int ideal)
	{
		// we are really just making sure no array out of bounds errors occur
		ElmanPattern elmanPattern = new ElmanPattern();
		elmanPattern.setInputNeurons(input);
		elmanPattern.addHiddenLayer(hidden);
		elmanPattern.setOutputNeurons(ideal);
		BasicNetwork network = (BasicNetwork)elmanPattern.generate();
		MLDataSet training = RandomTrainingFactory.generate(1000, 5, network.getInputCount(), network.getOutputCount(), -1, 1);
		ResilientPropagation prop = new ResilientPropagation(network,training);
		prop.iteration();
		prop.iteration();		
	}
	
	public void performJordanTest(int input, int hidden, int ideal)
	{
		// we are really just making sure no array out of bounds errors occur
		JordanPattern jordanPattern = new JordanPattern();
		jordanPattern.setInputNeurons(input);
		jordanPattern.addHiddenLayer(hidden);
		jordanPattern.setOutputNeurons(ideal);
		BasicNetwork network = (BasicNetwork)jordanPattern.generate();
		MLDataSet training = RandomTrainingFactory.generate(1000, 5, network.getInputCount(), network.getOutputCount(), -1, 1);
		ResilientPropagation prop = new ResilientPropagation(network,training);
		prop.iteration();
		prop.iteration();		
	}
	
	public void testElman()	
	{		
		performElmanTest(1,2,1);
		performElmanTest(1,5,1);
		performElmanTest(1,25,1);
		performElmanTest(2,2,2);
		performElmanTest(8,2,8);
	}
	
	public void testJordan()	
	{		
		performJordanTest(1,2,1);
		performJordanTest(1,5,1);
		performJordanTest(1,25,1);
		performJordanTest(2,2,2);
		performJordanTest(8,2,8);
	}
}
