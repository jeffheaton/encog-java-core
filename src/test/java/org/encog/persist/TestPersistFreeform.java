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

import junit.framework.TestCase;

import org.encog.neural.freeform.FreeformNetwork;
import org.encog.neural.networks.XOR;
import org.encog.util.TempDir;
import org.encog.util.obj.SerializeObject;
import org.junit.Test;

public class TestPersistFreeform extends TestCase {

	public final TempDir TEMP_DIR = new TempDir();
	public final File EG_FILENAME = TEMP_DIR.createFile("encogtest.eg");
	public final File SERIAL_FILENAME = TEMP_DIR.createFile("encogtest.ser");

	public FreeformNetwork create()
	{
		FreeformNetwork network = XOR.createTrainedFreeformXOR();
		XOR.verifyXOR(network, 0.1);
		
		network.setProperty("test", "test2");

		return network;
	}
	
	public void validate(FreeformNetwork network)
	{
		network.clearContext();
		XOR.verifyXOR(network, 0.1);
	}
	
	@Test
	public void testPersistSerial() throws IOException, ClassNotFoundException
	{
		FreeformNetwork network = create();
		
		SerializeObject.save(SERIAL_FILENAME, network);
		FreeformNetwork network2 = (FreeformNetwork)SerializeObject.load(SERIAL_FILENAME);
				
		validate(network2);
	}
}
