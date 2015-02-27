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
package org.encog.persist;

import java.io.File;
import java.io.IOException;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.encog.neural.thermal.HopfieldNetwork;
import org.encog.util.TempDir;
import org.encog.util.obj.SerializeObject;

public class TestPersistLargeHopfield extends TestCase {
	
	public final TempDir TEMP_DIR = new TempDir();
	public final File EG_FILENAME = TEMP_DIR.createFile("encogtest.eg");
	public final File SERIAL_FILENAME = TEMP_DIR.createFile("encogtest.ser");
	
	public void testPersistEG()
	{
		HopfieldNetwork network = new HopfieldNetwork(1000);
		network.setWeight(1,1,1);
		network.setProperty("x", 10);
		
		EncogDirectoryPersistence.saveObject(EG_FILENAME, network);
		HopfieldNetwork network2 = (HopfieldNetwork)EncogDirectoryPersistence.loadObject((EG_FILENAME));
		
		validateHopfield(network2);
	}
	
	public void testPersistSerial() throws IOException, ClassNotFoundException
	{
		HopfieldNetwork network = new HopfieldNetwork(1000);
		network.setWeight(1,1,1);
		
		SerializeObject.save(SERIAL_FILENAME, network);
		HopfieldNetwork network2 = (HopfieldNetwork)SerializeObject.load(SERIAL_FILENAME);
				
		validateHopfield(network2);
	}
	
	private void validateHopfield(HopfieldNetwork network)
	{
		Assert.assertEquals(1000, network.getNeuronCount());
		Assert.assertEquals(1000, network.getCurrentState().size());
		Assert.assertEquals(1000000, network.getWeights().length);
		Assert.assertEquals(1.0,network.getWeight(1, 1));
	}
	
	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		TEMP_DIR.dispose();
	}
}
