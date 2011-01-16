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

import junit.framework.Assert;
import junit.framework.TestCase;

import org.encog.neural.thermal.HopfieldNetwork;
import org.encog.util.obj.SerializeObject;

public class TestPersistHopfield extends TestCase {
	
	public final String EG_FILENAME = "encogtest.eg";
	public final String EG_RESOURCE = "test";
	public final String SERIAL_FILENAME = "encogtest.ser";
	
	public void testPersistEG()
	{
		HopfieldNetwork network = new HopfieldNetwork(4);
		network.setWeight(1,1,1);
		EncogMemoryCollection encog = new EncogMemoryCollection();
		encog.add(EG_RESOURCE, network);
		encog.save(EG_FILENAME);
		
		EncogMemoryCollection encog2 = new EncogMemoryCollection();
		encog2.load(EG_FILENAME);
		HopfieldNetwork network2 = (HopfieldNetwork)encog2.find(EG_RESOURCE);
		
		validateHopfield(network2);
	}
	
	public void testPersistSerial() throws IOException, ClassNotFoundException
	{
		HopfieldNetwork network = new HopfieldNetwork(4);
		network.setWeight(1,1,1);
		
		SerializeObject.save(SERIAL_FILENAME, network);
		HopfieldNetwork network2 = (HopfieldNetwork)SerializeObject.load(SERIAL_FILENAME);
				
		validateHopfield(network2);
	}
	
	public void testPersistSerialEG() throws IOException, ClassNotFoundException, InstantiationException, IllegalAccessException
	{
		HopfieldNetwork network = new HopfieldNetwork(4);
		network.setWeight(1,1,1);
		
		SerializeObject.saveEG(SERIAL_FILENAME, network);
		HopfieldNetwork network2 = (HopfieldNetwork)SerializeObject.loadEG(SERIAL_FILENAME);
				
		validateHopfield(network2);
	}
	
	private void validateHopfield(HopfieldNetwork network)
	{
		Assert.assertEquals(4, network.getNeuronCount());
		Assert.assertEquals(4, network.getCurrentState().size());
		Assert.assertEquals(16, network.getWeights().length);
		Assert.assertEquals(1.0,network.getWeight(1, 1));
	}
}
