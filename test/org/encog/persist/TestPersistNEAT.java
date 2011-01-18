/*
 * Encog(tm) Core v2.6 Unit Test - Java Version
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
package org.encog.persist;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.encog.engine.network.activation.ActivationFunction;
import org.encog.engine.network.activation.ActivationStep;
import org.encog.neural.art.ART1;
import org.encog.neural.neat.NEATNetwork;
import org.encog.neural.neat.NEATNeuron;
import org.encog.util.obj.SerializeObject;

public class TestPersistNEAT extends TestCase {
	
	public final String EG_FILENAME = "encogtest.eg";
	public final String EG_RESOURCE = "test";
	public final String SERIAL_FILENAME = "encogtest.ser";
	
	private NEATNetwork create()
	{
		List<NEATNeuron> neurons = new ArrayList<NEATNeuron>();
		ActivationFunction af = new ActivationStep();
		
		NEATNetwork result = new NEATNetwork(2, 
				1,
				neurons,
				af, 
				3);
		return result;
	}
	
	public void testPersistEG()
	{
		NEATNetwork network = create();

		EncogMemoryCollection encog = new EncogMemoryCollection();
		encog.add(EG_RESOURCE, network);
		encog.save(EG_FILENAME);
		
		EncogMemoryCollection encog2 = new EncogMemoryCollection();
		encog2.load(EG_FILENAME);
		NEATNetwork network2 = (NEATNetwork)encog2.find(EG_RESOURCE);
		validate(network2);
	}
	
	public void testPersistSerial() throws IOException, ClassNotFoundException
	{
		NEATNetwork network = create();
		
		SerializeObject.save(SERIAL_FILENAME, network);
		NEATNetwork network2 = (NEATNetwork)SerializeObject.load(SERIAL_FILENAME);
				
		validate(network2);
	}
	
	public void testPersistSerialEG() throws IOException, ClassNotFoundException, InstantiationException, IllegalAccessException
	{
		NEATNetwork network = create();
		
		SerializeObject.saveEG(SERIAL_FILENAME, network);
		NEATNetwork network2 = (NEATNetwork)SerializeObject.loadEG(SERIAL_FILENAME);
				
		validate(network2);
	}
	
	private void validate(NEATNetwork network)
	{
		/*Assert.assertEquals(6, network.getF1Count());
		Assert.assertEquals(3, network.getF2Count());
		Assert.assertEquals(18, network.getWeightsF1toF2().size());
		Assert.assertEquals(18, network.getWeightsF2toF1().size());
		Assert.assertEquals(2.0, network.getWeightsF1toF2().get(1, 1));
		Assert.assertEquals(3.0, network.getWeightsF2toF1().get(2, 2));
		Assert.assertEquals(1.0, network.getA1());
		Assert.assertEquals(1.5, network.getB1());
		Assert.assertEquals(5.0, network.getC1());
		Assert.assertEquals(0.9, network.getD1());
		Assert.assertEquals(0.9, network.getVigilance());*/
	}
}
