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
import org.encog.util.normalize.input.InputFieldArray2D;
import org.encog.util.normalize.output.mapped.OutputFieldEncode;
import org.encog.util.normalize.target.NormalizationStorageArray2D;
import org.junit.Assert;

public class TestMapped extends TestCase {
	
	public static final double[][] ARRAY_2D = { {1.0,2.0,3.0,4.0,5.0},
	{6.0,7.0,8.0,9.0} };
	
	private DataNormalization create(double[][] arrayOutput) {
		InputField a,b;
		
		
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
		
		return norm;
	}
	
	public void check(double[][] arrayOutput) {
		Assert.assertEquals(arrayOutput[0][0],0.1,0.1);
		Assert.assertEquals(arrayOutput[1][0],0.0,0.1);
		Assert.assertEquals(arrayOutput[0][1],0.1,0.1);
		Assert.assertEquals(arrayOutput[1][1],0.2,0.1);	
	}
	
	public void testOutputFieldEncode()
	{
		double[][] arrayOutput = new double[2][2];
		DataNormalization norm = create(arrayOutput);
		norm.process();
		check(arrayOutput);
	}
	
	public void testOutputFieldEncodeSerialize() throws Exception
	{
		double[][] arrayOutput = new double[2][2];
		DataNormalization norm = create(arrayOutput);
		norm = (DataNormalization)SerializeRoundTrip.roundTrip(norm);
		arrayOutput = ((NormalizationStorageArray2D)norm.getStorage()).getArray();
		norm.process();
		check(arrayOutput);
	}
}
