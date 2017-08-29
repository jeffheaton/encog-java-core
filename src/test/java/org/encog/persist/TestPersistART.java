/*
 * Encog(tm) Core v3.4 - Java Version
 * http://www.heatonresearch.com/encog/
 * https://github.com/encog/encog-java-core
 
 * Copyright 2008-2017 Heaton Research, Inc.
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

import org.encog.Encog;
import org.encog.neural.art.ART1;
import org.encog.util.TempDir;
import org.encog.util.obj.SerializeObject;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;

public class TestPersistART {
	
	public final TempDir TEMP_DIR = new TempDir();
	public final File EG_FILENAME = TEMP_DIR.createFile("encogtest.eg");
	public final File SERIAL_FILENAME = TEMP_DIR.createFile("encogtest.ser");
		
	private ART1 create()
	{
		ART1 network = new ART1(6,3);
		network.getWeightsF1toF2().set(1, 1, 2.0);
		network.getWeightsF2toF1().set(2, 2, 3.0);
		return network;
	}

	@Test
	public void testPersistEG()
	{
		ART1 network = create();

		EncogDirectoryPersistence.saveObject(EG_FILENAME, network);
		ART1 network2 = (ART1)EncogDirectoryPersistence.loadObject(EG_FILENAME);

		validate(network2);
	}

	@Test
	public void testPersistSerial() throws IOException, ClassNotFoundException
	{
		ART1 network = create();
		
		SerializeObject.save(SERIAL_FILENAME, network);
		ART1 network2 = (ART1)SerializeObject.load(SERIAL_FILENAME);
				
		validate(network2);
	}
		
	private void validate(ART1 network)
	{
		Assert.assertEquals(6, network.getF1Count());
		Assert.assertEquals(3, network.getF2Count());
		Assert.assertEquals(18, network.getWeightsF1toF2().size());
		Assert.assertEquals(18, network.getWeightsF2toF1().size());
		Assert.assertEquals(2.0, network.getWeightsF1toF2().get(1, 1), Encog.DEFAULT_DOUBLE_EQUAL);
		Assert.assertEquals(3.0, network.getWeightsF2toF1().get(2, 2), Encog.DEFAULT_DOUBLE_EQUAL);
		Assert.assertEquals(1.0, network.getA1(), Encog.DEFAULT_DOUBLE_EQUAL);
		Assert.assertEquals(1.5, network.getB1(), Encog.DEFAULT_DOUBLE_EQUAL);
		Assert.assertEquals(5.0, network.getC1(), Encog.DEFAULT_DOUBLE_EQUAL);
		Assert.assertEquals(0.9, network.getD1(), Encog.DEFAULT_DOUBLE_EQUAL);
		Assert.assertEquals(0.9, network.getVigilance(), Encog.DEFAULT_DOUBLE_EQUAL);
	}

	@After
	public void tearDown() throws Exception {
		TEMP_DIR.dispose();
	}
	
	
}
