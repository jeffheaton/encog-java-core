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

import org.encog.neural.cpn.CPN;
import org.encog.util.TempDir;
import org.encog.util.obj.SerializeObject;

public class TestPersistCPN extends TestCase {
	
	public final TempDir TEMP_DIR = new TempDir();
	public final File EG_FILENAME = TEMP_DIR.createFile("encogtest.eg");
	public final File SERIAL_FILENAME = TEMP_DIR.createFile("encogtest.ser");
	
	private CPN create() {
		CPN result = new CPN(5, 4, 3, 2);
		return result;
	}
	
	public void testPersistEG()
	{
		CPN network = create();

		EncogDirectoryPersistence.saveObject(EG_FILENAME, network);
		CPN network2 = (CPN)EncogDirectoryPersistence.loadObject(EG_FILENAME);

		validate(network2);
	}
	
	public void testPersistSerial() throws IOException, ClassNotFoundException
	{
		CPN network = create();
		
		SerializeObject.save(SERIAL_FILENAME, network);
		CPN network2 = (CPN)SerializeObject.load(SERIAL_FILENAME);
				
		validate(network2);
	}
	
	private void validate(CPN cpn) {
		Assert.assertEquals(5, cpn.getInputCount());
		Assert.assertEquals(4, cpn.getInstarCount());
		Assert.assertEquals(3, cpn.getOutputCount());
		Assert.assertEquals(3, cpn.getOutstarCount());
		Assert.assertEquals(2, cpn.getWinnerCount());
		Assert.assertEquals(5, cpn.getWeightsInputToInstar().getRows());
		Assert.assertEquals(4, cpn.getWeightsInputToInstar().getCols());
		Assert.assertEquals(4, cpn.getWeightsInstarToOutstar().getRows());
		Assert.assertEquals(3, cpn.getWeightsInstarToOutstar().getCols());
	}
	
	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		TEMP_DIR.dispose();
	}
}
