package org.encog.neural.data.buffer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;

import junit.framework.TestCase;

import org.encog.neural.data.basic.BasicNeuralData;
import org.encog.neural.networks.XOR;

public class TestBufferedNeuralDataSet extends TestCase {

	public static final String FILENAME = "xor.bin";
	
	
	public void testBufferData() throws Exception 
	{
		BufferedNeuralDataSet set = new BufferedNeuralDataSet(new File(FILENAME));
		set.beginLoad(2, 1);
		for(int i=0;i<XOR.XOR_INPUT.length;i++) {
			BasicNeuralData input = new BasicNeuralData(XOR.XOR_INPUT[i]);
			BasicNeuralData ideal = new BasicNeuralData(XOR.XOR_IDEAL[i]);
			set.add(input,ideal);
		}
		set.endLoad();
		
		XOR.testXORDataSet(set);
		
	}	
}
