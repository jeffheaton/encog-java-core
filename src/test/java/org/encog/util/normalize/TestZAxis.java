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

import junit.framework.Assert;
import junit.framework.TestCase;

import org.encog.NullStatusReportable;
import org.encog.util.SerializeRoundTrip;
import org.encog.util.normalize.input.InputField;
import org.encog.util.normalize.input.InputFieldArray2D;
import org.encog.util.normalize.output.zaxis.OutputFieldZAxis;
import org.encog.util.normalize.output.zaxis.OutputFieldZAxisSynthetic;
import org.encog.util.normalize.output.zaxis.ZAxisGroup;
import org.encog.util.normalize.target.NormalizationStorageArray2D;

public class TestZAxis extends TestCase {
	double[][] SAMPLE1 = {{-10,5,15},{-2,1,3}};
	
	public DataNormalization create() {
		InputField a;
		InputField b;
		InputField c;
		double[][] arrayOutput = new double[2][4];
		
		NormalizationStorageArray2D target = new NormalizationStorageArray2D(arrayOutput);
		ZAxisGroup group = new ZAxisGroup();
		DataNormalization norm = new DataNormalization();
		norm.setReport(new NullStatusReportable());
		norm.setTarget(target);
		norm.addInputField(a = new InputFieldArray2D(false,SAMPLE1,0));
		norm.addInputField(b = new InputFieldArray2D(false,SAMPLE1,1));
		norm.addInputField(c = new InputFieldArray2D(false,SAMPLE1,2));
		norm.addOutputField(new OutputFieldZAxis(group,a));
		norm.addOutputField(new OutputFieldZAxis(group,b));
		norm.addOutputField(new OutputFieldZAxis(group,c));
		norm.addOutputField(new OutputFieldZAxisSynthetic(group));
		return norm;
	}
	
	private void check(DataNormalization norm) {
		double[][] arrayOutput = ((NormalizationStorageArray2D)norm.getStorage()).getArray();

		Assert.assertEquals(-5.0,arrayOutput[0][0]);
		Assert.assertEquals(2.5,arrayOutput[0][1]);
		Assert.assertEquals(7.5,arrayOutput[0][2]);
		Assert.assertEquals(0.0,arrayOutput[0][3]);
		Assert.assertEquals(-1.0,arrayOutput[1][0]);
		Assert.assertEquals(0.5,arrayOutput[1][1]);
		Assert.assertEquals(1.5,arrayOutput[1][2]);
		Assert.assertEquals(0.0,arrayOutput[1][3]);
	}
	
	public void testAbsolute()
	{
		DataNormalization norm = create();
		norm.process();
		check(norm);
	}
	
	public void testAbsoluteSerial() throws Exception
	{
		DataNormalization norm = create();
		norm = (DataNormalization)SerializeRoundTrip.roundTrip(norm);
		norm.process();
		check(norm);
	}
}
