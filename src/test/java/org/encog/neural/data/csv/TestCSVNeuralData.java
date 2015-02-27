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
package org.encog.neural.data.csv;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;

import junit.framework.TestCase;

import org.encog.ml.data.specific.CSVNeuralDataSet;
import org.encog.neural.networks.XOR;

public class TestCSVNeuralData extends TestCase {

	public static final String FILENAME = "xor.csv";
	
	private void generateCSV() throws FileNotFoundException
	{
		PrintStream ps = new PrintStream(new FileOutputStream(TestCSVNeuralData.FILENAME));
		for(int count = 0; count<XOR.XOR_INPUT.length; count++)
		{
			StringBuilder builder = new StringBuilder();
			
			for(int i=0;i<XOR.XOR_INPUT[0].length;i++ )
			{
				if( builder.length()>0 )
					builder.append(',');
				builder.append(XOR.XOR_INPUT[count][i]);
			}
			
			for(int i=0;i<XOR.XOR_IDEAL[0].length;i++ )
			{
				if( builder.length()>0 )
					builder.append(',');
				builder.append(XOR.XOR_IDEAL[count][i]);
			}
			ps.println(builder.toString());
		}		
		ps.close();
	}
	
	public void testCSVData() throws Exception 
	{
		generateCSV();
		
		CSVNeuralDataSet set = new CSVNeuralDataSet("xor.csv",2,1,false);
		
		XOR.testXORDataSet(set);
		
		set.close();
		new File(TestCSVNeuralData.FILENAME).delete();
	}	
}
