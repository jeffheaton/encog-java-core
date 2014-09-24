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

import junit.framework.TestCase;

import org.encog.mathutil.rbf.RBFEnum;
import org.encog.ml.data.MLDataSet;
import org.encog.neural.networks.XOR;
import org.encog.neural.rbf.RBFNetwork;
import org.encog.neural.rbf.training.SVDTraining;
import org.encog.util.TempDir;

public class TestPersistRBF extends TestCase {
	
	public final TempDir TEMP_DIR = new TempDir();
	public final File EG_FILENAME = TEMP_DIR.createFile("encogtest.eg");
	public final File SERIAL_FILENAME = TEMP_DIR.createFile("encogtest.ser");
	
	public void testPersistNetworkRBF()
	{
		MLDataSet trainingSet = XOR.createXORDataSet();
		RBFNetwork network = new RBFNetwork(2,4,1, RBFEnum.Gaussian);

		SVDTraining training = new SVDTraining(network,trainingSet);
		training.iteration();
		XOR.verifyXOR(network, 0.1);
		
		EncogDirectoryPersistence.saveObject(EG_FILENAME, network);
		RBFNetwork network2 = (RBFNetwork)EncogDirectoryPersistence.loadObject(EG_FILENAME);

		XOR.verifyXOR(network2, 0.1);		
	}
	
	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		TEMP_DIR.dispose();
	}
}
