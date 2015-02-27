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
import org.encog.util.normalize.input.InputField;
import org.encog.util.normalize.input.InputFieldArray2D;
import org.encog.util.normalize.output.OutputFieldRangeMapped;
import org.encog.util.normalize.segregate.IntegerBalanceSegregator;
import org.encog.util.normalize.segregate.RangeSegregator;
import org.encog.util.normalize.segregate.Segregator;
import org.encog.util.normalize.segregate.index.IndexRangeSegregator;
import org.encog.util.normalize.segregate.index.IndexSampleSegregator;
import org.encog.util.normalize.target.NormalizationStorageArray2D;
import org.junit.Assert;

public class TestSegregate extends TestCase {
	public static final double[][] ARRAY_2D = { {1.0,2.0,3.0,4.0,5.0},
		{1.0,2.0,3.0,4.0,5.0},
		{1.0,2.0,3.0,4.0,5.0},
		{1.0,2.0,3.0,4.0,5.0},
		{1.0,2.0,3.0,4.0,5.0},
		{2.0,2.0,3.0,4.0,5.0} };
	
		private DataNormalization createIntegerBalance() {
			InputField a,b;
			double[][] arrayOutput = new double[3][2];
			
			
			
			NormalizationStorageArray2D target = new NormalizationStorageArray2D(arrayOutput);
			
			DataNormalization norm = new DataNormalization();
			norm.setReport(new NullStatusReportable());
			norm.setTarget(target);
			norm.addInputField(a = new InputFieldArray2D(false,ARRAY_2D,0));
			norm.addInputField(b = new InputFieldArray2D(false,ARRAY_2D,1));
			norm.addOutputField(new OutputFieldRangeMapped(a,0.1,0.9));
			norm.addOutputField(new OutputFieldRangeMapped(b,0.1,0.9));
			norm.addSegregator(new IntegerBalanceSegregator(a,2));
			return norm;
		}
		
		private void check(DataNormalization norm, int req) {
			Segregator s = norm.getSegregators().get(0);
			double[][] arrayOutput = ((NormalizationStorageArray2D)norm.getStorage()).getArray();
			Assert.assertEquals(req, arrayOutput.length);
		}
		
		public void testIntegerBalance()
		{
			DataNormalization norm = createIntegerBalance();
			norm.process();
			check(norm,3);
		}
		
		private DataNormalization createRangeSegregate()
		{
			InputField a,b;
			double[][] arrayOutput = new double[1][2];
			
			RangeSegregator s;
			
			NormalizationStorageArray2D target = new NormalizationStorageArray2D(arrayOutput);
			
			DataNormalization norm = new DataNormalization();
			norm.setReport(new NullStatusReportable());
			norm.setTarget(target);
			norm.addInputField(a = new InputFieldArray2D(false,ARRAY_2D,0));
			norm.addInputField(b = new InputFieldArray2D(false,ARRAY_2D,1));
			norm.addOutputField(new OutputFieldRangeMapped(a,0.1,0.9));
			norm.addOutputField(new OutputFieldRangeMapped(b,0.1,0.9));
			norm.addSegregator(s = new RangeSegregator(a,false));
			s.addRange(2, 2, true);
			return norm;
		}
		
		public void testRangeSegregate()
		{
			DataNormalization norm = createRangeSegregate();
			norm.process();
			check(norm,1);
		}
		
		private DataNormalization createSampleSegregate()
		{
			InputField a,b;
			double[][] arrayOutput = new double[6][2];
			
			NormalizationStorageArray2D target = new NormalizationStorageArray2D(arrayOutput);
			
			DataNormalization norm = new DataNormalization();
			norm.setReport(new NullStatusReportable());
			norm.setTarget(target);
			norm.addInputField(a = new InputFieldArray2D(false,ARRAY_2D,0));
			norm.addInputField(b = new InputFieldArray2D(false,ARRAY_2D,1));
			norm.addOutputField(new OutputFieldRangeMapped(a,0.1,0.9));
			norm.addOutputField(new OutputFieldRangeMapped(b,0.1,0.9));
			norm.addSegregator(new IndexSampleSegregator(0,3,2));
			return norm;
		}
		
		public void testSampleSegregate()
		{
			DataNormalization norm = createSampleSegregate();
			norm.process();
			check(norm,6);
		}
		
		public DataNormalization createIndexSegregate()
		{
			InputField a,b;
			double[][] arrayOutput = new double[6][2];
			
			NormalizationStorageArray2D target = new NormalizationStorageArray2D(arrayOutput);
			
			DataNormalization norm = new DataNormalization();
			norm.setReport(new NullStatusReportable());
			norm.setTarget(target);
			norm.addInputField(a = new InputFieldArray2D(false,ARRAY_2D,0));
			norm.addInputField(b = new InputFieldArray2D(false,ARRAY_2D,1));
			norm.addOutputField(new OutputFieldRangeMapped(a,0.1,0.9));
			norm.addOutputField(new OutputFieldRangeMapped(b,0.1,0.9));
			norm.addSegregator(new IndexRangeSegregator(0,3));
			return norm;
		}
		
		public void testIndexSegregate()
		{
			DataNormalization norm = createIndexSegregate();
			norm.process();
			check(norm,6);
		}

}
