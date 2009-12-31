package org.encog.normalize;

import org.encog.NullStatusReportable;
import org.encog.normalize.input.InputField;
import org.encog.normalize.input.InputFieldArray1D;
import org.encog.normalize.input.InputFieldArray2D;
import org.encog.normalize.output.OutputFieldRangeMapped;
import org.encog.normalize.output.mapped.OutputFieldEncode;
import org.encog.normalize.target.NormalizationStorageArray1D;
import org.encog.normalize.target.NormalizationStorageArray2D;
import org.junit.Assert;

import junit.framework.TestCase;

public class TestMapped extends TestCase {
	
	public static final double[][] ARRAY_2D = { {1.0,2.0,3.0,4.0,5.0},
	{6.0,7.0,8.0,9.0} };
	
	public void testOutputFieldEncode()
	{
		InputField a,b;
		double[][] arrayOutput = new double[2][2];
		
		NormalizationStorageArray2D target = new NormalizationStorageArray2D(arrayOutput);
		OutputFieldEncode a1;
		OutputFieldEncode b1;
		
		DataNormalization norm = new DataNormalization();
		norm.setReport(new NullStatusReportable());
		norm.setTarget(target);
		norm.addInputField(a = new InputFieldArray2D(false,ARRAY_2D,0));
		norm.addInputField(b = new InputFieldArray2D(false,ARRAY_2D,1));
		norm.addOutputField(a1 = new OutputFieldEncode(a));
		norm.addOutputField(b1 = new OutputFieldEncode(b));
		a1.addRange(1.0, 2.0, 0.1);
		b1.addRange(0, 100, 0.2);
		norm.process();
		Assert.assertEquals(arrayOutput[0][0],0.1,0.1);
		Assert.assertEquals(arrayOutput[1][0],0.0,0.1);
		Assert.assertEquals(arrayOutput[0][1],0.1,0.1);
		Assert.assertEquals(arrayOutput[1][1],0.2,0.1);
		
	}
}
