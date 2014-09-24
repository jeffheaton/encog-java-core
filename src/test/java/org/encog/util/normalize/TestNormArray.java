/*
 * Encog(tm) Core v3.3 - Java Version
 * http://www.heatonresearch.com/encog/
 * https://github.com/encog/encog-java-core
 
 * Copyright 2008-2014 Heaton Research, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *   
 * For more information on Heaton Research copyrights, licenses 
 * and trademarks visit:
 * http://www.heatonresearch.com/copyright
 */
package org.encog.util.normalize;

import junit.framework.TestCase;

import org.encog.NullStatusReportable;
import org.encog.util.SerializeRoundTrip;
import org.encog.util.normalize.input.InputField;
import org.encog.util.normalize.input.InputFieldArray1D;
import org.encog.util.normalize.input.InputFieldArray2D;
import org.encog.util.normalize.output.OutputFieldRangeMapped;
import org.encog.util.normalize.target.NormalizationStorageArray1D;
import org.encog.util.normalize.target.NormalizationStorageArray2D;
import org.junit.Assert;

public class TestNormArray extends TestCase {
	
	public static final double[] ARRAY_1D = { 1.0,2.0,3.0,4.0,5.0 };
	public static final double[][] ARRAY_2D = { {1.0,2.0,3.0,4.0,5.0},
	{6.0,7.0,8.0,9.0} };
	
	private DataNormalization create1D(double[] arrayOutput) {
		InputField a;
				
		NormalizationStorageArray1D target = new NormalizationStorageArray1D(arrayOutput);
		
		DataNormalization norm = new DataNormalization();
		norm.setReport(new NullStatusReportable());
		norm.setTarget(target);
		norm.addInputField(a = new InputFieldArray1D(false,ARRAY_1D));
		norm.addOutputField(new OutputFieldRangeMapped(a,0.1,0.9));
		return norm;
	}
	
	private DataNormalization create2D(double[][] arrayOutput) {
		InputField a,b;
				
		NormalizationStorageArray2D target = new NormalizationStorageArray2D(arrayOutput);
		
		DataNormalization norm = new DataNormalization();
		norm.setReport(new NullStatusReportable());
		norm.setTarget(target);
		norm.addInputField(a = new InputFieldArray2D(false,ARRAY_2D,0));
		norm.addInputField(b = new InputFieldArray2D(false,ARRAY_2D,1));
		norm.addOutputField(new OutputFieldRangeMapped(a,0.1,0.9));
		norm.addOutputField(new OutputFieldRangeMapped(b,0.1,0.9));
		return norm;
	}
	
	public void testArray1D()
	{
		double[] arrayOutput = new double[5];
		DataNormalization norm = create1D(arrayOutput);		
		norm.process();
		check1D(arrayOutput);
	}
	
	public void testArray2D()
	{
		double[][] arrayOutput = new double[2][2];
		DataNormalization norm = create2D(arrayOutput);
		norm.process();
		check2D(arrayOutput);		
	}
	
	public void testArray1DSerial() throws Exception
	{
		double[] arrayOutput = new double[5];
		DataNormalization norm = create1D(arrayOutput);	
		norm = (DataNormalization)SerializeRoundTrip.roundTrip(norm);
		arrayOutput = ((NormalizationStorageArray1D)norm.getStorage()).getArray();
		norm.process();
		check1D(arrayOutput);
	}
	
	public void testArray2DSerial() throws Exception
	{
		double[][] arrayOutput = new double[2][2];
		DataNormalization norm = create2D(arrayOutput);
		norm = (DataNormalization)SerializeRoundTrip.roundTrip(norm);
		arrayOutput = ((NormalizationStorageArray2D)norm.getStorage()).getArray();
		norm.process();
		check2D(arrayOutput);		
	}
	
	public void check1D(double[] arrayOutput) {
		Assert.assertEquals(arrayOutput[0],0.1,0.1);
		Assert.assertEquals(arrayOutput[1],0.3,0.1);
		Assert.assertEquals(arrayOutput[2],0.5,0.1);
		Assert.assertEquals(arrayOutput[3],0.7,0.1);
		Assert.assertEquals(arrayOutput[4],0.9,0.1);
	}
	
	public void check2D(double[][] arrayOutput) {
		Assert.assertEquals(arrayOutput[0][0],0.1,0.1);
		Assert.assertEquals(arrayOutput[1][0],0.9,0.1);
		Assert.assertEquals(arrayOutput[0][1],0.1,0.1);
		Assert.assertEquals(arrayOutput[1][1],0.9,0.1);	
	}
}

