package org.encog.persist;

import java.io.File;

import junit.framework.TestCase;

import org.encog.neural.data.basic.BasicNeuralDataSet;
import org.encog.neural.data.csv.TestCSVNeuralData;
import org.encog.neural.networks.XOR;
import org.encog.persist.EncogPersistedCollection;

public class TestDataPersist extends TestCase {
	
	public static final String FILENAME = "encogtest.xml";
	
	public void testCSVData() throws Exception 
	{
		BasicNeuralDataSet trainingData = new BasicNeuralDataSet(XOR.XOR_INPUT,XOR.XOR_IDEAL);

		EncogPersistedCollection encog = new EncogPersistedCollection(new File(TestDataPersist.FILENAME));
		encog.add("data", trainingData);
		
		//EncogPersistedCollection encog2 = new EncogPersistedCollection(new File(TestDataPersist.FILENAME));
		
		//BasicNeuralDataSet set = (BasicNeuralDataSet) encog2.getList().get(0);
		
		//XOR.testXORDataSet(set);
		
		//new File(TestCSVNeuralData.FILENAME).delete();
	}	
}
