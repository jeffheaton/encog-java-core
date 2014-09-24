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
import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.encog.engine.network.activation.ActivationFunction;
import org.encog.engine.network.activation.ActivationSteepenedSigmoid;
import org.encog.neural.neat.NEATLink;
import org.encog.neural.neat.NEATNetwork;
import org.encog.util.TempDir;
import org.encog.util.obj.SerializeObject;
import org.junit.Assert;

public class TestPersistNEAT extends TestCase {
	
	public final TempDir TEMP_DIR = new TempDir();
	public final File EG_FILENAME = TEMP_DIR.createFile("encogtest.eg");
	public final File SERIAL_FILENAME = TEMP_DIR.createFile("encogtest.ser");
		
	private NEATNetwork create()
	{
		// simple network, 1 input, 1 bais , those two both conned into output neuron, so that 2 links!
		ActivationFunction[] activationFunctions = new ActivationFunction[3];
		List<NEATLink> links = new ArrayList<NEATLink>(2);
		
		for(int i=0;i<activationFunctions.length;i++) {
			activationFunctions[i] = new ActivationSteepenedSigmoid();
		}
		
		links.add( new NEATLink(0,2,1.0));
		links.add( new NEATLink(1,2,2.0));
		
		NEATNetwork result = new NEATNetwork(1,1,links,activationFunctions);
				
		return result;
	}
	
	public void testPersistSerial() throws IOException, ClassNotFoundException
	{
		NEATNetwork network = create();
		
		SerializeObject.save(SERIAL_FILENAME, network);
		NEATNetwork network2 = (NEATNetwork)SerializeObject.load(SERIAL_FILENAME);
				
		validate(network2);
	}
		
	private void validate(NEATNetwork network)
	{
		Assert.assertEquals(1, network.getOutputCount());
		Assert.assertEquals(1, network.getInputCount());

	}
	
	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		TEMP_DIR.dispose();
	}
}
