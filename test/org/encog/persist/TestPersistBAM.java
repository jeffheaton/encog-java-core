/*
 * Encog(tm) Core v3.0 Unit Test - Java Version
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
package org.encog.persist;

import java.io.IOException;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.encog.neural.art.ART1;
import org.encog.neural.bam.BAM;
import org.encog.util.obj.SerializeObject;

public class TestPersistBAM extends TestCase {
	
	public final String EG_FILENAME = "encogtest.eg";
	public final String EG_RESOURCE = "test";
	public final String SERIAL_FILENAME = "encogtest.ser";
	
	private BAM create()
	{
		BAM network = new BAM(6,3);
		network.getWeightsF1toF2().set(1, 1, 2.0);
		network.getWeightsF2toF1().set(2, 2, 3.0);
		return network;
	}
	
	public void testPersistEG()
	{
		BAM network = create();

		EncogMemoryCollection encog = new EncogMemoryCollection();
		encog.add(EG_RESOURCE, network);
		encog.save(EG_FILENAME);
		
		EncogMemoryCollection encog2 = new EncogMemoryCollection();
		encog2.load(EG_FILENAME);
		BAM network2 = (BAM)encog2.find(EG_RESOURCE);
		validateBAM(network2);
	}
	
	public void testPersistSerial() throws IOException, ClassNotFoundException
	{
		BAM network = create();
		
		SerializeObject.save(SERIAL_FILENAME, network);
		BAM network2 = (BAM)SerializeObject.load(SERIAL_FILENAME);
				
		validateBAM(network2);
	}
	
	public void testPersistSerialEG() throws IOException, ClassNotFoundException, InstantiationException, IllegalAccessException
	{
		BAM network = create();
		
		SerializeObject.saveEG(SERIAL_FILENAME, network);
		BAM network2 = (BAM)SerializeObject.loadEG(SERIAL_FILENAME);
				
		validateBAM(network2);
	}
	
	private void validateBAM(BAM network)
	{
		Assert.assertEquals(6, network.getF1Count());
		Assert.assertEquals(3, network.getF2Count());
		Assert.assertEquals(18, network.getWeightsF1toF2().size());
		Assert.assertEquals(18, network.getWeightsF2toF1().size());
		Assert.assertEquals(2.0, network.getWeightsF1toF2().get(1, 1));
		Assert.assertEquals(3.0, network.getWeightsF2toF1().get(2, 2));
	}
}
