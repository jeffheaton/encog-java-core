package org.encog.neural.data.csv;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;

import junit.framework.TestCase;

import org.encog.neural.networks.XOR;

public class TestCSVNeuralData extends TestCase {

	public static final String FILENAME = "xor.csv";
	
	private void generateCSV() throws FileNotFoundException
	{
		PrintStream ps = new PrintStream(new FileOutputStream(TestCSVNeuralData.FILENAME));
		for(int count = 0; count<XOR.XOR_INPUT.length; count++)
		{
			StringBuilder builder = new StringBuilder();
			
			for(int i=0;i<XOR.XOR_INPUT[0].length;i++ )
			{
				if( builder.length()>0 )
					builder.append(',');
				builder.append(XOR.XOR_INPUT[count][i]);
			}
			
			for(int i=0;i<XOR.XOR_IDEAL[0].length;i++ )
			{
				if( builder.length()>0 )
					builder.append(',');
				builder.append(XOR.XOR_IDEAL[count][i]);
			}
			ps.println(builder.toString());
		}		
		ps.close();
	}
	
	public void testCSVData() throws Exception 
	{
		generateCSV();
		
		CSVNeuralDataSet set = new CSVNeuralDataSet("xor.csv",2,1,false);
		
		XOR.testXORDataSet(set);
		
		set.close();
		new File(TestCSVNeuralData.FILENAME).delete();
	}	
}
