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
import org.encog.util.normalize.output.multiplicative.MultiplicativeGroup;
import org.encog.util.normalize.output.multiplicative.OutputFieldMultiplicative;
import org.encog.util.normalize.target.NormalizationStorageArray2D;
import org.junit.Assert;

public class TestMultiplicative extends TestCase {
	double[][] SAMPLE1 = {{-10,5,15},{-2,1,3}};
	
	public DataNormalization create(double[][] arrayOutput) {
		InputField a;
		InputField b;
		InputField c;
				
		NormalizationStorageArray2D target = new NormalizationStorageArray2D(arrayOutput);
		MultiplicativeGroup group = new MultiplicativeGroup();
		DataNormalization norm = new DataNormalization();
		norm.setReport(new NullStatusReportable());
		norm.setTarget(target);
		norm.addInputField(a = new InputFieldArray2D(false,SAMPLE1,0));
		norm.addInputField(b = new InputFieldArray2D(false,SAMPLE1,1));
		norm.addInputField(c = new InputFieldArray2D(false,SAMPLE1,2));
		norm.addOutputField(new OutputFieldMultiplicative(group,a));
		norm.addOutputField(new OutputFieldMultiplicative(group,b));
		norm.addOutputField(new OutputFieldMultiplicative(group,c));
		return norm;
	}
	
	public void testAbsolute()
	{
		double[][] arrayOutput = new double[2][3];
		DataNormalization norm = create(arrayOutput);
		norm.process();
		Assert.assertArrayEquals(arrayOutput[0], arrayOutput[1],0.01);
	}
	
	public void testAbsoluteSerial() throws Exception
	{
		double[][] arrayOutput = new double[2][3];
		DataNormalization norm = create(arrayOutput);
		norm = (DataNormalization)SerializeRoundTrip.roundTrip(norm);
		arrayOutput = ((NormalizationStorageArray2D)norm.getStorage()).getArray();
		norm.process();
		Assert.assertArrayEquals(arrayOutput[0], arrayOutput[1],0.01);
	}
}
