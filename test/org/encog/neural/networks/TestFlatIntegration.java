package org.encog.neural.networks;

import org.encog.neural.data.NeuralData;
import org.encog.neural.data.basic.BasicNeuralData;
import org.encog.neural.networks.structure.FlatUpdateNeeded;

import junit.framework.Assert;
import junit.framework.TestCase;

public class TestFlatIntegration extends TestCase {

	public void testNetworkOutput()
	{
		BasicNetwork network1 = NetworkUtil.createXORNetworkUntrained();
		network1.getStructure().finalizeStructure();
		Assert.assertNotNull(network1.getStructure().getFlat());
		Assert.assertEquals(network1.getStructure().getFlatUpdate(),FlatUpdateNeeded.None);
		double[] inputArray = {1.0,1.0};
		NeuralData input = new BasicNeuralData(inputArray);
		
		// using a holder will cause the network to calculate without the flat network,
		// should calculate to exactly the same number, with or without flat.
		NeuralOutputHolder holder = new NeuralOutputHolder();
		NeuralData output1 = network1.compute(input);
		NeuralData output2 = network1.compute(input,holder);
		int i1 = (int)(output1.getData(0) * 10000);
		int i2 = (int)(output2.getData(0) * 10000);
		Assert.assertEquals(i1, i2);
	}
}
