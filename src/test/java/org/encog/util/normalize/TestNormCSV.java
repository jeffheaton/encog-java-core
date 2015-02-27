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

import java.io.File;

import junit.framework.TestCase;

import org.encog.NullStatusReportable;
import org.encog.util.SerializeRoundTrip;
import org.encog.util.TempDir;
import org.encog.util.normalize.input.InputField;
import org.encog.util.normalize.input.InputFieldArray2D;
import org.encog.util.normalize.input.InputFieldCSV;
import org.encog.util.normalize.output.OutputFieldDirect;
import org.encog.util.normalize.output.OutputFieldRangeMapped;
import org.encog.util.normalize.target.NormalizationStorageArray2D;
import org.encog.util.normalize.target.NormalizationStorageCSV;
import org.junit.Assert;

public class TestNormCSV extends TestCase {

	public static final double[][] ARRAY_2D = { {1.0,2.0,3.0,4.0,5.0},
		{6.0,7.0,8.0,9.0,10.0} };
		
	public final static TempDir TEMP_DIR = new TempDir();
	public final static File FILENAME = TEMP_DIR.createFile("norm.csv");

	private void generate()
	{
		InputField a;
		InputField b;
		InputField c;
		InputField d;
		InputField e;
		
		DataNormalization norm = new DataNormalization();
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
	
	public DataNormalization create(double[][] outputArray) {
		InputField a;
		InputField b;
		InputField c;
		InputField d;
		InputField e;
		
		DataNormalization norm = new DataNormalization();
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
		return norm;
	}
	
	public void testGenerateAndLoad()
	{
		double[][] outputArray = new double[2][5];
		generate();
		DataNormalization norm = create(outputArray);
		norm.process();
		check(norm);
	}

	public void testGenerateAndLoadSerial() throws Exception
	{
		double[][] outputArray = new double[2][5];
		generate();
		DataNormalization norm = create(outputArray);
		norm = (DataNormalization)SerializeRoundTrip.roundTrip(norm);
		norm.process();
		check(norm);
	}
	
	private void check(DataNormalization norm) {
		InputField a = norm.getInputFields().get(0);
		InputField b = norm.getInputFields().get(1);
		
		Assert.assertEquals(1.0,a.getMin(),0.1);
		Assert.assertEquals(6.0,a.getMax(),0.1);
		Assert.assertEquals(2.0,b.getMin(),0.1);
		Assert.assertEquals(7.0,b.getMax(),0.1);
		
		double[][] outputArray = ((NormalizationStorageArray2D)norm.getStorage()).getArray();
		for(int i=0;i<5;i++)
		{
			Assert.assertEquals(0.1,outputArray[0][i],0.1);
			Assert.assertEquals(0.9,outputArray[1][i],0.1);
		}
	}
}
