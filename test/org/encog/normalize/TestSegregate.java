/*
 * Encog(tm) Unit Tests v2.5 - Java Version
 * http://www.heatonresearch.com/encog/
 * http://code.google.com/p/encog-java/
 
 * Copyright 2008-2010 Heaton Research, Inc.
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

import java.io.File;

import org.encog.NullStatusReportable;
import org.encog.neural.networks.BasicNetwork;
import org.encog.normalize.input.InputField;
import org.encog.normalize.input.InputFieldArray2D;
import org.encog.normalize.input.InputFieldCSV;
import org.encog.normalize.output.OutputFieldDirect;
import org.encog.normalize.output.OutputFieldRangeMapped;
import org.encog.normalize.output.mapped.OutputFieldEncode;
import org.encog.normalize.segregate.IntegerBalanceSegregator;
import org.encog.normalize.segregate.RangeSegregator;
import org.encog.normalize.segregate.Segregator;
import org.encog.normalize.segregate.index.IndexRangeSegregator;
import org.encog.normalize.segregate.index.IndexSampleSegregator;
import org.encog.normalize.target.NormalizationStorageArray2D;
import org.encog.normalize.target.NormalizationStorageCSV;
import org.encog.persist.EncogPersistedCollection;
import org.junit.Assert;

import junit.framework.TestCase;

public class TestSegregate extends TestCase {
	public static final double[][] ARRAY_2D = { {1.0,2.0,3.0,4.0,5.0},
		{1.0,2.0,3.0,4.0,5.0},
		{1.0,2.0,3.0,4.0,5.0},
		{1.0,2.0,3.0,4.0,5.0},
		{1.0,2.0,3.0,4.0,5.0},
		{2.0,2.0,3.0,4.0,5.0} };
		
		public void testIntegerBalance()
		{
			InputField a,b;
			double[][] arrayOutput = new double[3][2];
			
			IntegerBalanceSegregator s;
			
			NormalizationStorageArray2D target = new NormalizationStorageArray2D(arrayOutput);
			
			DataNormalization norm = new DataNormalization();
			norm.setReport(new NullStatusReportable());
			norm.setTarget(target);
			norm.addInputField(a = new InputFieldArray2D(false,ARRAY_2D,0));
			norm.addInputField(b = new InputFieldArray2D(false,ARRAY_2D,1));
			norm.addOutputField(new OutputFieldRangeMapped(a,0.1,0.9));
			norm.addOutputField(new OutputFieldRangeMapped(b,0.1,0.9));
			norm.addSegregator(s = new IntegerBalanceSegregator(a,2));
			norm.process();
			s.dumpCounts();
			Assert.assertEquals(3, arrayOutput.length);
			
		}
		
		public void testRangeSegregate()
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
			norm.process();
			
		}
		
		public void testSampleSegregate()
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
			norm.process();
			// would throw out of bounds if test failed.
		}
		
		public void testIndexSegregate()
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
			norm.process();
			// would throw out of bounds if test failed.
		}
		
		public void testPersist()
		{
			File file = new File("");
			InputField inputElevation;
			InputField inputAspect;
			InputField inputSlope;
			InputField hWater;
			InputField vWater;
			InputField roadway;
			InputField shade9;
			InputField shade12;
			InputField shade3;
			InputField firepoint;
			InputField[] wilderness = new InputField[4];
			InputField[] soilType = new InputField[40];
			InputField coverType;	
			
			DataNormalization norm = new DataNormalization();
			norm.setReport(new NullStatusReportable());
			norm.setTarget(new NormalizationStorageCSV(file));
			norm.addInputField(inputElevation = new InputFieldCSV(true,file,0));
			norm.addInputField(inputAspect = new InputFieldCSV(true,file,1));
			norm.addInputField(inputSlope = new InputFieldCSV(true,file,2));
			norm.addInputField(hWater = new InputFieldCSV(true,file,3));
			norm.addInputField(vWater = new InputFieldCSV(true,file,4));
			norm.addInputField(roadway = new InputFieldCSV(true,file,5));
			norm.addInputField(shade9 = new InputFieldCSV(true,file,6));
			norm.addInputField(shade12 = new InputFieldCSV(true,file,7));
			norm.addInputField(shade3 = new InputFieldCSV(true,file,8));
			norm.addInputField(firepoint = new InputFieldCSV(true,file,9));
			
			for(int i=0;i<4;i++)
			{
				norm.addInputField(wilderness[i]=new InputFieldCSV(true,file,10+i));
			}
			
			for(int i=0;i<40;i++)
			{
				norm.addInputField(soilType[i]=new InputFieldCSV(true,file,14+i));
			}
			
			norm.addInputField(coverType=new InputFieldCSV(false,file,54));
			
			norm.addOutputField(new OutputFieldRangeMapped(inputElevation,0.1,0.9));
			norm.addOutputField(new OutputFieldRangeMapped(inputAspect,0.1,0.9));
			norm.addOutputField(new OutputFieldRangeMapped(inputSlope,0.1,0.9));
			norm.addOutputField(new OutputFieldRangeMapped(hWater,0.1,0.9));
			norm.addOutputField(new OutputFieldRangeMapped(vWater,0.1,0.9));
			norm.addOutputField(new OutputFieldRangeMapped(roadway,0.1,0.9));
			norm.addOutputField(new OutputFieldRangeMapped(shade9,0.1,0.9));
			norm.addOutputField(new OutputFieldRangeMapped(shade12,0.1,0.9));
			norm.addOutputField(new OutputFieldRangeMapped(shade3,0.1,0.9));
			norm.addOutputField(new OutputFieldRangeMapped(firepoint,0.1,0.9));
			
			for(int i=0;i<40;i++)
			{
				norm.addOutputField(new OutputFieldDirect(soilType[i]));
			}
			
			EncogPersistedCollection encog = 
				new EncogPersistedCollection("encogtest.eg");
			encog.create();
			encog.add("test", norm);
			
			norm = (DataNormalization)encog.find("test");
			
			Assert.assertNotNull(norm);
			
		}
}
