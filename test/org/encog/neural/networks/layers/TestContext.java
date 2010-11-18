/*
 * Encog(tm) Core v2.6 Unit Test - Java Version
 * http://www.heatonresearch.com/encog/
 * http://code.google.com/p/encog-java/
 
 * Copyright 2008-2010 Heaton Research, Inc.
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
package org.encog.neural.networks.layers;

import org.encog.neural.data.NeuralDataSet;
import org.encog.neural.data.basic.BasicNeuralDataSet;
import org.encog.neural.networks.BasicNetwork;
import org.encog.persist.EncogPersistedCollection;
import org.encog.persist.location.PersistenceLocation;
import org.encog.persist.location.ResourcePersistence;
import org.junit.Assert;

import junit.framework.TestCase;

public class TestContext  extends TestCase {
	
	
	/**
	 * 1 xor 0 = 1, 0 xor 0 = 0, 0 xor 1 = 1, 1 xor 1 = 0
	 */
	public static final double[] SEQUENCE = { 1.0, 0.0, 1.0, 0.0, 0.0, 0.0,
			0.0, 1.0, 1.0, 1.0, 1.0, 0.0 };

	private double[][] input;
	private double[][] ideal;

	public NeuralDataSet generate(final int count) {
		this.input = new double[count][1];
		this.ideal = new double[count][1];

		for (int i = 0; i < this.input.length; i++) {
			this.input[i][0] = TestContext.SEQUENCE[i
					% TestContext.SEQUENCE.length];
			this.ideal[i][0] = TestContext.SEQUENCE[(i + 1)
					% TestContext.SEQUENCE.length];
		}

		return new BasicNeuralDataSet(this.input, this.ideal);
	}
	
	public void testContextLayer()
	{
		PersistenceLocation location = new ResourcePersistence("org/encog/data/networks.eg");
		EncogPersistedCollection encog = new EncogPersistedCollection(location);
		BasicNetwork network = (BasicNetwork)encog.find("elman");
		NeuralDataSet data = generate(100);
		int rate = (int)(network.calculateError(data)*100);
		//Assert.assertTrue(rate<35);
	}
}
