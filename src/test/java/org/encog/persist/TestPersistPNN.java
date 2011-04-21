package org.encog.persist;

import java.io.File;
import java.io.IOException;

import junit.framework.TestCase;

import org.encog.ml.data.basic.BasicMLDataSet;
import org.encog.neural.networks.XOR;
import org.encog.neural.networks.training.pnn.TrainBasicPNN;
import org.encog.neural.pnn.BasicPNN;
import org.encog.neural.pnn.PNNKernelType;
import org.encog.neural.pnn.PNNOutputMode;
import org.encog.util.TempDir;
import org.encog.util.obj.SerializeObject;

public class TestPersistPNN extends TestCase {
	
	public final TempDir TEMP_DIR = new TempDir();
	public final File EG_FILENAME = TEMP_DIR.createFile("encogtest.eg");
	public final File SERIAL_FILENAME = TEMP_DIR.createFile("encogtest.ser");
		
	public BasicPNN create() {
		PNNOutputMode mode = PNNOutputMode.Regression;

		BasicPNN network = new BasicPNN(PNNKernelType.Gaussian, mode, 2, 1);

		BasicMLDataSet trainingSet = new BasicMLDataSet(XOR.XOR_INPUT,
				XOR.XOR_IDEAL);

		System.out.println("Learning...");

		TrainBasicPNN train = new TrainBasicPNN(network, trainingSet);
		train.iteration();
		XOR.verifyXOR(network, 0.001);
		return network;		
	}
	
	public void testPersistEG()
	{
		BasicPNN network = create();

		EncogDirectoryPersistence.saveObject((EG_FILENAME), network);
		BasicPNN network2 = (BasicPNN)EncogDirectoryPersistence.loadObject((EG_FILENAME));

		XOR.verifyXOR(network2, 0.001);
	}
	
	public void testPersistSerial() throws IOException, ClassNotFoundException
	{
		BasicPNN network = create();
		
		SerializeObject.save(SERIAL_FILENAME, network);
		BasicPNN network2 = (BasicPNN)SerializeObject.load(SERIAL_FILENAME);
				
		XOR.verifyXOR(network2, 0.001);
	}
	
	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		TEMP_DIR.dispose();
	}
}
