package org.encog.persist;

import junit.framework.TestCase;

import org.encog.mathutil.rbf.RBFEnum;
import org.encog.neural.data.NeuralDataSet;
import org.encog.neural.networks.XOR;
import org.encog.neural.rbf.RBFNetwork;
import org.encog.neural.rbf.training.SVDTraining;

public class TestPersistRBF extends TestCase {
	
	public final String EG_FILENAME = "encogtest.eg";
	public final String EG_RESOURCE = "test";
	public final String SERIAL_FILENAME = "encogtest.ser";
	
	public void testPersistNetworkRBF()
	{
		NeuralDataSet trainingSet = XOR.createXORDataSet();
		RBFNetwork network = new RBFNetwork(2,4,1, RBFEnum.Gaussian);

		SVDTraining training = new SVDTraining(network,trainingSet);
		training.iteration();
		XOR.verifyXOR(network, 0.1);
		
		EncogMemoryCollection encog = new EncogMemoryCollection();
		encog.add(EG_RESOURCE, network);
		encog.save(EG_FILENAME);
		
		EncogMemoryCollection encog2 = new EncogMemoryCollection();
		encog2.load(EG_FILENAME);
		RBFNetwork network2 = (RBFNetwork)encog2.find(EG_RESOURCE);
		XOR.verifyXOR(network2, 0.1);		
	}
}
