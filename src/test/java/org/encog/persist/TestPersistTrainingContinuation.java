package org.encog.persist;

import java.io.File;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.encog.ml.data.MLDataSet;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.XOR;
import org.encog.neural.networks.training.propagation.TrainingContinuation;
import org.encog.neural.networks.training.propagation.resilient.ResilientPropagation;
import org.encog.util.TempDir;

public class TestPersistTrainingContinuation extends TestCase {
	
	public final TempDir TEMP_DIR = new TempDir();
	public final File EG_FILENAME = TEMP_DIR.createFile("encogtest.eg");
	public final File SERIAL_FILENAME = TEMP_DIR.createFile("encogtest.ser");

	public void testRPROPCont() {
		MLDataSet trainingSet = XOR.createXORDataSet();
		BasicNetwork net1 = XOR.createUnTrainedXOR();
		BasicNetwork net2 = XOR.createUnTrainedXOR();
		
		ResilientPropagation rprop1 = new ResilientPropagation(net1,trainingSet);
		ResilientPropagation rprop2 = new ResilientPropagation(net2,trainingSet);
		
		rprop1.iteration();
		rprop1.iteration();
		
		rprop2.iteration();
		rprop2.iteration();
		
		TrainingContinuation cont = rprop2.pause();
		
		ResilientPropagation rprop3 = new ResilientPropagation(net2,trainingSet);
		rprop3.resume(cont);
		
		rprop1.iteration();
		rprop3.iteration();
		
		
		for(int i=0;i<net1.getFlat().getWeights().length;i++) {
			Assert.assertEquals(net1.getFlat().getWeights()[i], net2.getFlat().getWeights()[i],0.0001);
		}
	}
	
	public void testRPROPContPersistEG() {
		MLDataSet trainingSet = XOR.createXORDataSet();
		BasicNetwork net1 = XOR.createUnTrainedXOR();
		BasicNetwork net2 = XOR.createUnTrainedXOR();
		
		ResilientPropagation rprop1 = new ResilientPropagation(net1,trainingSet);
		ResilientPropagation rprop2 = new ResilientPropagation(net2,trainingSet);
		
		rprop1.iteration();
		rprop1.iteration();
		
		rprop2.iteration();
		rprop2.iteration();
		
		TrainingContinuation cont = rprop2.pause();
		
		EncogDirectoryPersistence.saveObject(EG_FILENAME, cont);
		TrainingContinuation cont2 = (TrainingContinuation)EncogDirectoryPersistence.loadObject(EG_FILENAME);
		
		ResilientPropagation rprop3 = new ResilientPropagation(net2,trainingSet);
		rprop3.resume(cont2);
		
		rprop1.iteration();
		rprop3.iteration();
		
		
		for(int i=0;i<net1.getFlat().getWeights().length;i++) {
			Assert.assertEquals(net1.getFlat().getWeights()[i], net2.getFlat().getWeights()[i],0.0001);
		}
	}
	
}
