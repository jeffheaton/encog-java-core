package org.encog.util.simple;

import org.encog.neural.data.NeuralDataSet;
import org.encog.neural.data.basic.BasicNeuralDataSet;
import org.encog.neural.networks.XOR;

import junit.framework.TestCase;

public class TestImportExport extends TestCase {
	
	public static final String FILENAME = "xor.csv";
	
	public void testImportExportEnglish()
	{
		NeuralDataSet set = XOR.createXORDataSet();
		ImportExportUtility.exportCSV(set, FILENAME);
		NeuralDataSet set2 = new BasicNeuralDataSet();
		ImportExportUtility.importCSV(set2, set.getInputSize(), set.getIdealSize(), FILENAME, false);
		XOR.testXORDataSet(set2);
	}
}
