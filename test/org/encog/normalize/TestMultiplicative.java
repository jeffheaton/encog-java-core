package org.encog.normalize;

import org.encog.NullStatusReportable;
import org.encog.normalize.input.InputField;
import org.encog.normalize.input.InputFieldArray1D;
import org.encog.normalize.input.InputFieldArray2D;
import org.encog.normalize.output.OutputFieldRangeMapped;
import org.encog.normalize.output.multiplicative.MultiplicativeGroup;
import org.encog.normalize.output.multiplicative.OutputFieldMultiplicative;
import org.encog.normalize.target.NormalizationStorageArray2D;
import org.junit.Assert;

import junit.framework.TestCase;

public class TestMultiplicative extends TestCase {
	double[][] SAMPLE1 = {{-10,5,15},{-2,1,3}};
	
	public void testAbsolute()
	{
		InputField a;
		InputField b;
		InputField c;
		double[][] arrayOutput = new double[2][3];
		
		NormalizationStorageArray2D target = new NormalizationStorageArray2D(arrayOutput);
		MultiplicativeGroup group = new MultiplicativeGroup();
		Normalization norm = new Normalization();
		norm.setReport(new NullStatusReportable());
		norm.setTarget(target);
		norm.addInputField(a = new InputFieldArray2D(SAMPLE1,0));
		norm.addInputField(b = new InputFieldArray2D(SAMPLE1,1));
		norm.addInputField(c = new InputFieldArray2D(SAMPLE1,2));
		norm.addOutputField(new OutputFieldMultiplicative(group,a));
		norm.addOutputField(new OutputFieldMultiplicative(group,b));
		norm.addOutputField(new OutputFieldMultiplicative(group,c));
		norm.process();
		Assert.assertArrayEquals(arrayOutput[0], arrayOutput[1],0.01);

	}
}
