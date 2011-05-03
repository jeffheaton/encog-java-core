/*
 * Encog(tm) Core Unit Tests v3.0 - Java Version
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

import java.io.File;
import java.io.IOException;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.encog.neural.art.ART1;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.XOR;
import org.encog.util.TempDir;
import org.encog.util.obj.SerializeObject;

public class TestPersistBasicNetwork extends TestCase {
	
	public final TempDir TEMP_DIR = new TempDir();
	public final File EG_FILENAME = TEMP_DIR.createFile("encogtest.eg");
	public final File SERIAL_FILENAME = TEMP_DIR.createFile("encogtest.ser");
		
	public BasicNetwork create()
	{
		BasicNetwork network = XOR.createTrainedXOR();
		XOR.verifyXOR(network, 0.1);
		
		network.setProperty("test", "test2");

		
		return network;
	}
	
	public void validate(BasicNetwork network)
	{
		network.clearContext();
		XOR.verifyXOR(network, 0.1);
	}
	
	public void testPersistEG()
	{
		BasicNetwork network = create();

		EncogDirectoryPersistence.saveObject(EG_FILENAME, network);
		BasicNetwork network2 = (BasicNetwork)EncogDirectoryPersistence.loadObject(EG_FILENAME);

		validate(network2);
	}
	
	public void testPersistSerial() throws IOException, ClassNotFoundException
	{
		BasicNetwork network = create();
		
		SerializeObject.save(SERIAL_FILENAME, network);
		BasicNetwork network2 = (BasicNetwork)SerializeObject.load(SERIAL_FILENAME);
				
		validate(network2);
	}
	
	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		TEMP_DIR.dispose();
	}

	
}
