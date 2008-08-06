package org.encog.neural.persist;

import java.io.File;

import org.encog.neural.XOR;
import org.encog.neural.data.NeuralDataSet;
import org.encog.neural.data.basic.BasicNeuralDataSet;
import org.encog.neural.data.csv.CSVNeuralDataSet;
import org.encog.neural.data.csv.TestCSVNeuralData;

import junit.framework.TestCase;

public class TestDataPersist extends TestCase {
	
	public static final String FILENAME = "encogtest.xml";
	
	public void testCSVData() throws Exception 
	{
		BasicNeuralDataSet trainingData = new BasicNeuralDataSet(XOR.XOR_INPUT,XOR.XOR_IDEAL);

		EncogPersistedCollection encog = new EncogPersistedCollection();
		encog.add(trainingData);
		encog.save(TestDataPersist.FILENAME);

		EncogPersistedCollection encog2 = new EncogPersistedCollection();
		encog2.load(TestDataPersist.FILENAME);
		
		BasicNeuralDataSet set = (BasicNeuralDataSet) encog2.getList().get(0);
		
		XOR.testXORDataSet(set);
		
		new File(TestCSVNeuralData.FILENAME).delete();
	}	
}
