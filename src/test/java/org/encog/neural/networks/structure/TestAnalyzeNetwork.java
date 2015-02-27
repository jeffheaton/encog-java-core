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
package org.encog.neural.networks.structure;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.encog.neural.networks.BasicNetwork;
import org.encog.util.EngineArray;
import org.encog.util.simple.EncogUtility;

public class TestAnalyzeNetwork extends TestCase {
	public void testAnalyze()
	{
		BasicNetwork network = EncogUtility.simpleFeedForward(2, 2, 0, 1, false);
		double[] weights = new double[network.encodedArrayLength()];
		EngineArray.fill(weights, 1.0);
		network.decodeFromArray(weights);
		AnalyzeNetwork analyze = new AnalyzeNetwork(network);
		Assert.assertEquals(weights.length, analyze.getWeightsAndBias().getSamples());
		Assert.assertEquals(3,analyze.getBias().getSamples());
		Assert.assertEquals(6,analyze.getWeights().getSamples());
	}
}
