package org.encog.neural.freeform;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.encog.engine.network.activation.ActivationSigmoid;
import org.encog.ml.data.MLDataSet;
import org.encog.ml.data.basic.BasicMLDataSet;
import org.encog.ml.train.MLTrain;
import org.encog.neural.freeform.training.FreeformResilientPropagation;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.NetworkUtil;
import org.encog.neural.networks.XOR;
import org.encog.neural.networks.layers.BasicLayer;

public class TestFreeform extends TestCase {
	
	public void testCreation() {
		// create a neural network, without using a factory
		BasicNetwork basicNetwork = new BasicNetwork();
		basicNetwork.addLayer(new BasicLayer(null, true, 2));
		basicNetwork.addLayer(new BasicLayer(new ActivationSigmoid(), true, 3));
		basicNetwork
				.addLayer(new BasicLayer(new ActivationSigmoid(), false, 1));
		basicNetwork.getStructure().finalizeStructure();
		basicNetwork.reset();

		FreeformNetwork freeformNetwork = new FreeformNetwork(basicNetwork);
		Assert.assertEquals(basicNetwork.getInputCount(),
				freeformNetwork.getInputCount());
		Assert.assertEquals(basicNetwork.getOutputCount(),
				freeformNetwork.getOutputCount());
		Assert.assertEquals(basicNetwork.encodedArrayLength(),
				freeformNetwork.encodedArrayLength());
	}
	
	public void testEncode() {
		
		// train (and test) a network
		MLDataSet trainingData = new BasicMLDataSet(XOR.XOR_INPUT,XOR.XOR_IDEAL);		
		FreeformNetwork trainedNetwork = NetworkUtil.createXORFreeformNetworkUntrained();
		MLTrain bprop = new FreeformResilientPropagation(trainedNetwork, trainingData);
		NetworkUtil.testTraining(bprop,0.01);
		
		trainedNetwork = (FreeformNetwork) bprop.getMethod();
		
		// allocate space to encode to
		double[] encoded = new double[trainedNetwork.encodedArrayLength()];
		
		// encode the network
		trainedNetwork.encodeToArray(encoded);
		
		// create untrained network
		FreeformNetwork untrainedNetwork = NetworkUtil.createXORFreeformNetworkUntrained();
		
		// copy the trained network to the untrained
		untrainedNetwork.decodeFromArray(encoded);
		
		// compare error levels
		Assert.assertEquals(trainedNetwork.calculateError(trainingData), trainedNetwork.calculateError(trainingData), 0.01);
	}
}
