/*
 * Encog(tm) Unit Tests v2.5 - Java Version
 * http://www.heatonresearch.com/encog/
 * http://code.google.com/p/encog-java/
 
 * Copyright 2008-2010 Heaton Research, Inc.
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

package org.encog.neural.networks.logic;

import org.encog.neural.data.NeuralDataSet;
import org.encog.neural.data.basic.BasicNeuralDataSet;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.NetworkUtil;
import org.encog.neural.networks.XOR;
import org.encog.neural.networks.training.Train;
import org.encog.neural.networks.training.simple.TrainAdaline;
import org.encog.neural.pattern.ADALINEPattern;

import junit.framework.TestCase;

public class TestADALINE extends TestCase {

	public void testAdalineNet() throws Throwable
	{
		ADALINEPattern pattern = new ADALINEPattern();
		pattern.setInputNeurons(2);
		pattern.setOutputNeurons(1);
		BasicNetwork network = pattern.generate();
		
		// train it
		NeuralDataSet training = new BasicNeuralDataSet(XOR.XOR_INPUT,XOR.XOR_IDEAL);
		Train train = new TrainAdaline(network,training,0.01);
		NetworkUtil.testTraining(train,0.01);
	}
	
}
