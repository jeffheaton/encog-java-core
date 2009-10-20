package org.encog.normalize;

import java.io.File;

import junit.framework.TestCase;

import org.encog.NullStatusReportable;
import org.encog.normalize.input.InputField;
import org.encog.normalize.input.InputFieldArray2D;
import org.encog.normalize.input.InputFieldCSV;
import org.encog.normalize.output.OutputFieldDirect;
import org.encog.normalize.output.OutputFieldRangeMapped;
import org.encog.normalize.target.NormalizationStorageArray2D;
import org.encog.normalize.target.NormalizationStorageCSV;
import org.junit.Assert;

public class TestNormCSV extends TestCase {

	public static final double[][] ARRAY_2D = { {1.0,2.0,3.0,4.0,5.0},
		{6.0,7.0,8.0,9.0,10.0} };
	
	public static final File FILENAME = new File("norm.csv"); 
	
	private void generate()
	{
		InputField a;
		InputField b;
		InputField c;
		InputField d;
		InputField e;
		
		Normalization norm = new Normalization();
		norm.setReport(new NullStatusReportable());
		norm.setTarget(new NormalizationStorageCSV(FILENAME));
		norm.addInputField(a = new InputFieldArray2D(false,ARRAY_2D,0));
		norm.addInputField(b = new InputFieldArray2D(false,ARRAY_2D,1));
		norm.addInputField(c = new InputFieldArray2D(false,ARRAY_2D,2));
		norm.addInputField(d = new InputFieldArray2D(false,ARRAY_2D,3));
		norm.addInputField(e = new InputFieldArray2D(false,ARRAY_2D,4));
		norm.addOutputField(new OutputFieldDirect(a));
		norm.addOutputField(new OutputFieldDirect(b));
		norm.addOutputField(new OutputFieldDirect(c));
		norm.addOutputField(new OutputFieldDirect(d));
		norm.addOutputField(new OutputFieldDirect(e));
		norm.setTarget(new NormalizationStorageCSV(FILENAME));
		norm.process();
	}
	
	public void testGenerateAndLoad()
	{
		double[][] outputArray = new double[2][5];
		generate();
		
		InputField a;
		InputField b;
		InputField c;
		InputField d;
		InputField e;
		
		Normalization norm = new Normalization();
		norm.setReport(new NullStatusReportable());
		norm.setTarget(new NormalizationStorageCSV(FILENAME));
		norm.addInputField(a = new InputFieldCSV(false,FILENAME,0));
		norm.addInputField(b = new InputFieldCSV(false,FILENAME,1));
		norm.addInputField(c = new InputFieldCSV(false,FILENAME,2));
		norm.addInputField(d = new InputFieldCSV(false,FILENAME,3));
		norm.addInputField(e = new InputFieldCSV(false,FILENAME,4));
		norm.addOutputField(new OutputFieldRangeMapped(a,0.1,0.9));
		norm.addOutputField(new OutputFieldRangeMapped(b,0.1,0.9));
		norm.addOutputField(new OutputFieldRangeMapped(c,0.1,0.9));
		norm.addOutputField(new OutputFieldRangeMapped(d,0.1,0.9));
		norm.addOutputField(new OutputFieldRangeMapped(e,0.1,0.9));
		norm.setTarget(new NormalizationStorageArray2D(outputArray));
		norm.process();
		Assert.assertEquals(1.0,a.getMin(),0.1);
		Assert.assertEquals(6.0,a.getMax(),0.1);
		Assert.assertEquals(2.0,b.getMin(),0.1);
		Assert.assertEquals(7.0,b.getMax(),0.1);
		for(int i=0;i<5;i++)
		{
			Assert.assertEquals(0.1,outputArray[0][i],0.1);
			Assert.assertEquals(0.9,outputArray[1][i],0.1);
		}

	}
}
