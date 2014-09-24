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

import org.encog.ml.bayesian.BayesianEvent;
import org.encog.ml.bayesian.BayesianNetwork;
import org.encog.util.TempDir;
import org.encog.util.obj.SerializeObject;

public class TestPersistBayes extends TestCase {
	
	public final TempDir TEMP_DIR = new TempDir();
	public final File EG_FILENAME = TEMP_DIR.createFile("encogtest.eg");
	public final File SERIAL_FILENAME = TEMP_DIR.createFile("encogtest.ser");
		
	public BayesianNetwork create()
	{
		BayesianNetwork network = new BayesianNetwork();
		BayesianEvent a = network.createEvent("a");
		BayesianEvent b = network.createEvent("b");

		network.createDependency(a, b);
		network.finalizeStructure();
		a.getTable().addLine(0.5, true); // P(A) = 0.5
		b.getTable().addLine(0.2, true, true); // p(b|a) = 0.2
		b.getTable().addLine(0.8, true, false);// p(b|~a) = 0.8		
		network.validate();
		return network;
	}
	
	public void validate(BayesianNetwork network)
	{
		Assert.assertEquals(3, network.calculateParameterCount());
	}
	
	public void testPersistEG()
	{
		BayesianNetwork network = create();

		EncogDirectoryPersistence.saveObject(EG_FILENAME, network);
		BayesianNetwork network2 = (BayesianNetwork)EncogDirectoryPersistence.loadObject(EG_FILENAME);

		validate(network2);
	}
	
	public void testPersistSerial() throws IOException, ClassNotFoundException
	{
		BayesianNetwork network = create();
		
		SerializeObject.save(SERIAL_FILENAME, network);
		BayesianNetwork network2 = (BayesianNetwork)SerializeObject.load(SERIAL_FILENAME);
				
		validate(network2);
	}
	
	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		TEMP_DIR.dispose();
	}

	
}
