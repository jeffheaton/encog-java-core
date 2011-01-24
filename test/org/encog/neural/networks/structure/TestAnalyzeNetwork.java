package org.encog.neural.networks.structure;

import org.encog.engine.util.EngineArray;
import org.encog.neural.networks.BasicNetwork;
import org.encog.util.simple.EncogUtility;

import junit.framework.Assert;
import junit.framework.TestCase;

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
