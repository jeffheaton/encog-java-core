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

import org.encog.neural.thermal.BoltzmannMachine;
import org.encog.util.TempDir;
import org.encog.util.obj.SerializeObject;

public class TestPersistBoltzmann extends TestCase {
	
	public final TempDir TEMP_DIR = new TempDir();
	public final File EG_FILENAME = TEMP_DIR.createFile("encogtest.eg");
	public final File SERIAL_FILENAME = TEMP_DIR.createFile("encogtest.ser");
		
	public void testPersistEG()
	{
		BoltzmannMachine network = new BoltzmannMachine(4);
		network.setWeight(1,1,1);
		network.getThreshold()[2] = 2;
		
		EncogDirectoryPersistence.saveObject(EG_FILENAME, network);
		BoltzmannMachine network2 = (BoltzmannMachine)EncogDirectoryPersistence.loadObject(EG_FILENAME);
		
		validateHopfield(network2);
	}
	
	public void testPersistSerial() throws IOException, ClassNotFoundException
	{
		BoltzmannMachine network = new BoltzmannMachine(4);
		network.setWeight(1,1,1);
		network.getThreshold()[2] = 2;
		
		SerializeObject.save(SERIAL_FILENAME, network);
		BoltzmannMachine network2 = (BoltzmannMachine)SerializeObject.load(SERIAL_FILENAME);
				
		validateHopfield(network2);
	}
	
	private void validateHopfield(BoltzmannMachine network)
	{
		network.run();// at least see if it can run without an exception
		Assert.assertEquals(4, network.getNeuronCount());
		Assert.assertEquals(4, network.getCurrentState().size());
		Assert.assertEquals(16, network.getWeights().length);
		Assert.assertEquals(1.0,network.getWeight(1, 1));
		Assert.assertEquals(2.0,network.getThreshold()[2]);
	}
	
	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		TEMP_DIR.dispose();
	}
}
