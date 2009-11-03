package org.encog.normalize;

import java.io.File;

import org.encog.NullStatusReportable;
import org.encog.normalize.input.InputField;
import org.encog.normalize.input.InputFieldArray1D;
import org.encog.normalize.input.InputFieldArray2D;
import org.encog.normalize.input.InputFieldCSV;
import org.encog.normalize.output.OutputFieldRangeMapped;
import org.encog.normalize.target.NormalizationStorageArray1D;
import org.encog.normalize.target.NormalizationStorageArray2D;
import org.junit.Assert;

import junit.framework.TestCase;

public class TestNormArray extends TestCase {
	
	public static final double[] ARRAY_1D = { 1.0,2.0,3.0,4.0,5.0 };
	public static final double[][] ARRAY_2D = { {1.0,2.0,3.0,4.0,5.0},
	{6.0,7.0,8.0,9.0} };
	
	public void testArray1D()
	{
		InputField a;
		double[] arrayOutput = new double[5];
		
		NormalizationStorageArray1D target = new NormalizationStorageArray1D(arrayOutput);
		
		DataNormalization norm = new DataNormalization();
		norm.setReport(new NullStatusReportable());
		norm.setTarget(target);
		norm.addInputField(a = new InputFieldArray1D(false,ARRAY_1D));
		norm.addOutputField(new OutputFieldRangeMapped(a,0.1,0.9));		
		norm.process();
		Assert.assertEquals(arrayOutput[0],0.1,0.1);
		Assert.assertEquals(arrayOutput[1],0.3,0.1);
		Assert.assertEquals(arrayOutput[2],0.5,0.1);
		Assert.assertEquals(arrayOutput[3],0.7,0.1);
		Assert.assertEquals(arrayOutput[4],0.9,0.1);
	}
	
	public void testArray2D()
	{
		InputField a,b;
		double[][] arrayOutput = new double[2][2];
		
		NormalizationStorageArray2D target = new NormalizationStorageArray2D(arrayOutput);
		
		DataNormalization norm = new DataNormalization();
		norm.setReport(new NullStatusReportable());
		norm.setTarget(target);
		norm.addInputField(a = new InputFieldArray2D(false,ARRAY_2D,0));
		norm.addInputField(b = new InputFieldArray2D(false,ARRAY_2D,1));
		norm.addOutputField(new OutputFieldRangeMapped(a,0.1,0.9));
		norm.addOutputField(new OutputFieldRangeMapped(b,0.1,0.9));
		norm.process();
		Assert.assertEquals(arrayOutput[0][0],0.1,0.1);
		Assert.assertEquals(arrayOutput[1][0],0.9,0.1);
		Assert.assertEquals(arrayOutput[0][1],0.1,0.1);
		Assert.assertEquals(arrayOutput[1][1],0.9,0.1);
		
	}
}
