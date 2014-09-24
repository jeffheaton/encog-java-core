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

import org.encog.ml.data.basic.BasicMLDataSet;
import org.encog.neural.networks.XOR;
import org.encog.neural.networks.training.pnn.TrainBasicPNN;
import org.encog.neural.pnn.BasicPNN;
import org.encog.neural.pnn.PNNKernelType;
import org.encog.neural.pnn.PNNOutputMode;
import org.encog.util.TempDir;
import org.encog.util.obj.SerializeObject;

public class TestPersistPNN extends TestCase {
	
	public final TempDir TEMP_DIR = new TempDir();
	public final File EG_FILENAME = TEMP_DIR.createFile("encogtest.eg");
	public final File SERIAL_FILENAME = TEMP_DIR.createFile("encogtest.ser");
		
	public BasicPNN create() {
		PNNOutputMode mode = PNNOutputMode.Regression;

		BasicPNN network = new BasicPNN(PNNKernelType.Gaussian, mode, 2, 1);

		BasicMLDataSet trainingSet = new BasicMLDataSet(XOR.XOR_INPUT,
				XOR.XOR_IDEAL);

		System.out.println("Learning...");

		TrainBasicPNN train = new TrainBasicPNN(network, trainingSet);
		train.iteration();
		XOR.verifyXOR(network, 0.001);
		return network;		
	}
	
	public void testPersistEG()
	{
		BasicPNN network = create();

		EncogDirectoryPersistence.saveObject((EG_FILENAME), network);
		BasicPNN network2 = (BasicPNN)EncogDirectoryPersistence.loadObject((EG_FILENAME));

		XOR.verifyXOR(network2, 0.001);
	}
	
	public void testPersistSerial() throws IOException, ClassNotFoundException
	{
		BasicPNN network = create();
		
		SerializeObject.save(SERIAL_FILENAME, network);
		BasicPNN network2 = (BasicPNN)SerializeObject.load(SERIAL_FILENAME);
				
		XOR.verifyXOR(network2, 0.001);
	}
	
	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		TEMP_DIR.dispose();
	}
}
