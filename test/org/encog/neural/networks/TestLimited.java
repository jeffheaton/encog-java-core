package org.encog.neural.networks;

import org.encog.neural.data.NeuralDataSet;
import org.encog.neural.data.basic.BasicNeuralDataSet;
import org.encog.neural.networks.synapse.Synapse;
import org.encog.neural.networks.training.propagation.resilient.ResilientPropagation;
import org.encog.util.logging.Logging;
import org.junit.Assert;

import junit.framework.TestCase;

public class TestLimited extends TestCase {
	
	public void testLimited()
	{
		Logging.stopConsoleLogging();
		NeuralDataSet trainingData = new BasicNeuralDataSet(XOR.XOR_INPUT,XOR.XOR_IDEAL);
		BasicNetwork network = NetworkUtil.createXORNetworkUntrained();		
		
		Synapse synapse = network.getStructure().getSynapses().get(0);
		
		ResilientPropagation rprop = new ResilientPropagation(network,trainingData);
		rprop.iteration();
		rprop.iteration();
		network.enableConnection(synapse, 0, 0, false);
		network.enableConnection(synapse, 1, 0, false);
		Assert.assertEquals(synapse.getMatrix().get(0,0),0.0,0.1);
		Assert.assertTrue(network.getStructure().isConnectionLimited());
		network.getStructure().updateFlatNetwork();
		Assert.assertEquals(0.0, network.getStructure().getFlat().getWeights()[0], 0.01);
		Assert.assertEquals(0.0, network.getStructure().getFlat().getWeights()[1], 0.01);
		rprop.iteration();
		rprop.iteration();
		rprop.iteration();
		rprop.iteration();
		// these connections were removed, and should not have been "trained"
		Assert.assertEquals(0.0, network.getStructure().getFlat().getWeights()[0], 0.01);
		Assert.assertEquals(0.0, network.getStructure().getFlat().getWeights()[1], 0.01);		
		rprop.finishTraining();
	}
}
