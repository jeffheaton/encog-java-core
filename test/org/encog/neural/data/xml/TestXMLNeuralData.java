/*
 * Encog(tm) Artificial Intelligence Framework v2.3
 * Java Version, Unit Tests
 * http://www.heatonresearch.com/encog/
 * http://code.google.com/p/encog-java/
 * 
 * Copyright 2008-2010 by Heaton Research Inc.
 * 
 * Released under the LGPL.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 * 
 * Encog and Heaton Research are Trademarks of Heaton Research, Inc.
 * For information on Heaton Research trademarks, visit:
 * 
 * http://www.heatonresearch.com/copyright.html
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
