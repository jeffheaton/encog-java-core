package org.encog.neural.data.buffer;

import java.io.File;

import org.encog.neural.data.buffer.BinaryDataLoader;
import org.encog.neural.data.buffer.codec.ArrayDataCODEC;
import org.encog.neural.data.buffer.codec.CSVDataCODEC;
import org.encog.neural.networks.XOR;
import org.encog.util.csv.CSVFormat;

import junit.framework.Assert;
import junit.framework.TestCase;

public class TestBinaryData extends TestCase {
	
	public void testArrayCODEC() throws Exception
	{
		ArrayDataCODEC codec = new ArrayDataCODEC(XOR.XOR_INPUT,XOR.XOR_IDEAL);
		BinaryDataLoader loader = new BinaryDataLoader(codec);
		loader.external2Binary(new File("encog.bin"));
	
		ArrayDataCODEC codec2 = new ArrayDataCODEC();
		BinaryDataLoader loader2 = new BinaryDataLoader(codec2);
		loader2.binary2External(new File("encog.bin"));
		
		double[][] input = codec2.getInput();
		double[][] ideal = codec2.getIdeal();
		
		for(int i=0;i<XOR.XOR_INPUT.length;i++)
		{
			for(int j=0;j<XOR.XOR_INPUT[i].length;j++)
			{
				Assert.assertEquals(input[i][j], XOR.XOR_INPUT[i][j], 0.01);
			}
			
			for(int j=0;j<XOR.XOR_IDEAL[i].length;j++)
			{
				Assert.assertEquals(ideal[i][j], XOR.XOR_IDEAL[i][j], 0.01);
			}
		}
		
	}
	
	public void testCSV()
	{
		ArrayDataCODEC codec = new ArrayDataCODEC(XOR.XOR_INPUT,XOR.XOR_IDEAL);
		BinaryDataLoader loader = new BinaryDataLoader(codec);
		loader.external2Binary(new File("encog.bin"));
	
		CSVDataCODEC codec2 = new CSVDataCODEC(new File("encog.csv"), CSVFormat.ENGLISH);
		BinaryDataLoader loader2 = new BinaryDataLoader(codec2);
		loader2.binary2External(new File("encog.bin"));
		
		CSVDataCODEC codec3 = new CSVDataCODEC(new File("encog.csv"), CSVFormat.ENGLISH, false, 2, 1);
		BinaryDataLoader loader3 = new BinaryDataLoader(codec3);
		loader3.external2Binary(new File("encog.bin"));

		ArrayDataCODEC codec4 = new ArrayDataCODEC();
		BinaryDataLoader loader4 = new BinaryDataLoader(codec4);
		loader4.binary2External(new File("encog.bin"));
		
		double[][] input = codec4.getInput();
		double[][] ideal = codec4.getIdeal();
		
		for(int i=0;i<XOR.XOR_INPUT.length;i++)
		{
			for(int j=0;j<XOR.XOR_INPUT[i].length;j++)
			{
				Assert.assertEquals(input[i][j], XOR.XOR_INPUT[i][j], 0.01);
			}
			
			for(int j=0;j<XOR.XOR_IDEAL[i].length;j++)
			{
				Assert.assertEquals(ideal[i][j], XOR.XOR_IDEAL[i][j], 0.01);
			}
		}

		
	}
	
}
