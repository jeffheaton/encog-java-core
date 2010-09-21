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

package org.encog.util;

import java.io.ByteArrayOutputStream;

import org.encog.neural.data.NeuralDataSet;
import org.encog.neural.data.basic.BasicNeuralDataSet;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.XOR;
import org.encog.neural.networks.layers.BasicLayer;
import org.encog.neural.networks.layers.RadialBasisFunctionLayer;
import org.encog.neural.pattern.ElmanPattern;
import org.encog.neural.pattern.RadialBasisPattern;

import junit.framework.Assert;
import junit.framework.TestCase;


public class TestSerializeObject extends TestCase {

	public void testSerializeXOR() throws Throwable
	{
		BasicNeuralDataSet set = new BasicNeuralDataSet(XOR.XOR_INPUT,
				XOR.XOR_IDEAL);
		SerializeObject.save("encog.ser", set);
		set = (BasicNeuralDataSet) SerializeObject.load("encog.ser");
		XOR.testXORDataSet(set);
	}
	
	public void testSerializeNetwork() throws Throwable
	{
		RadialBasisPattern pattern = new RadialBasisPattern();
		pattern.setInputNeurons(1);
		pattern.addHiddenLayer(2);
		pattern.setOutputNeurons(3);
		BasicNetwork net = pattern.generate();

		SerializeObject.save("encog.ser", net);
		net = (BasicNetwork) SerializeObject.load("encog.ser");
		Assert.assertEquals(3, net.getStructure().getLayers().size());
		
	}
	

	public void testSerializeNetwork2() throws Throwable
	{
		ElmanPattern pattern = new ElmanPattern();
		pattern.setInputNeurons(1);
		pattern.addHiddenLayer(2);
		pattern.setOutputNeurons(3);
		BasicNetwork net = pattern.generate();

		SerializeObject.save("encog.ser", net);
		net = (BasicNetwork) SerializeObject.load("encog.ser");
		Assert.assertEquals(4, net.getStructure().getLayers().size());
		
	}
	
}
