package org.encog.persist;

import java.io.File;

import junit.framework.TestCase;

import org.encog.mathutil.rbf.RBFEnum;
import org.encog.neural.art.ART1;
import org.encog.neural.data.NeuralDataSet;
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
		NeuralDataSet trainingSet = XOR.createXORDataSet();
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
