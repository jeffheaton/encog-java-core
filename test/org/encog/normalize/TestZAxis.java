/*
 * Encog(tm) Core v3.0 Unit Test - Java Version
 * http://www.heatonresearch.com/encog/
 * http://code.google.com/p/encog-java/
 
 * Copyright 2008-2011 Heaton Research, Inc.
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
package org.encog.normalize;

import org.encog.NullStatusReportable;
import org.encog.normalize.input.InputField;
import org.encog.normalize.input.InputFieldArray2D;
import org.encog.normalize.output.multiplicative.MultiplicativeGroup;
import org.encog.normalize.output.multiplicative.OutputFieldMultiplicative;
import org.encog.normalize.output.zaxis.OutputFieldZAxis;
import org.encog.normalize.output.zaxis.OutputFieldZAxisSynthetic;
import org.encog.normalize.output.zaxis.ZAxisGroup;
import org.encog.normalize.target.NormalizationStorageArray2D;
import org.junit.Assert;

import junit.framework.TestCase;

public class TestZAxis extends TestCase {
	double[][] SAMPLE1 = {{-10,5,15},{-2,1,3}};
	
	public void testAbsolute()
	{
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
		norm.process();
		System.out.println(arrayOutput[0][0]);
		System.out.println(arrayOutput[0][1]);
		System.out.println(arrayOutput[0][2]);
		System.out.println(arrayOutput[0][3]);
		
		System.out.println(arrayOutput[1][0]);
		System.out.println(arrayOutput[1][1]);
		System.out.println(arrayOutput[1][2]);
		System.out.println(arrayOutput[1][3]);

	}
}
