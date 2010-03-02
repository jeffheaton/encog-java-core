/*
 * Encog(tm) Core v2.4
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

package org.encog.neural.data.csv;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;

import junit.framework.TestCase;

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
