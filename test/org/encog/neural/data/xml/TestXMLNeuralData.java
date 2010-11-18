/*
 * Encog(tm) Core v2.6 Unit Test - Java Version
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
package org.encog.neural.data.xml;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;

import org.encog.neural.networks.XOR;

import junit.framework.TestCase;

public class TestXMLNeuralData extends TestCase {
	public static final String FILENAME = "test.xml";
	
	private void generateXML() throws FileNotFoundException
	{
		PrintStream ps = new PrintStream(new FileOutputStream(TestXMLNeuralData.FILENAME));
		ps.println("<DataSet>");
		ps.println("<pair><input><value>0</value><value>0</value></input><ideal><value>0</value></ideal></pair>");
		ps.println("<pair><input><value>1</value><value>0</value></input><ideal><value>1</value></ideal></pair>");
		ps.println("<pair><input><value>0</value><value>1</value></input><ideal><value>1</value></ideal></pair>");
		ps.println("<pair><input><value>1</value><value>1</value></input><ideal><value>0</value></ideal></pair>");
		ps.println("</DataSet>");
		ps.close();
	}
	
	public void testXMLNeuralData() throws Exception
	{
		generateXML();
		XMLNeuralDataSet set = new XMLNeuralDataSet(
				TestXMLNeuralData.FILENAME,2,1,
				"pair", 
				"input", 
				"ideal", 
				"value");
		
		TestCase.assertTrue(set.getInputSize()==2);
		TestCase.assertTrue(set.getIdealSize()==1);
		XOR.testXORDataSet(set);
		
		new File(TestXMLNeuralData.FILENAME).delete();
	}
}
