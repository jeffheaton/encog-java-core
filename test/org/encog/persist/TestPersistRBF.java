package org.encog.persist;

import java.io.File;

import junit.framework.TestCase;

import org.encog.mathutil.rbf.RBFEnum;
import org.encog.neural.art.ART1;
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
		
		EncogDirectoryPersistence.saveObject(new File(EG_FILENAME), network);
		RBFNetwork network2 = (RBFNetwork)EncogDirectoryPersistence.loadObject(new File(EG_FILENAME));

		XOR.verifyXOR(network2, 0.1);		
	}
}
