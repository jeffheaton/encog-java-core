/*
 * Encog(tm) Core v2.5 
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

package org.encog.neural.data.buffer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;

import junit.framework.TestCase;

import org.encog.neural.data.basic.BasicNeuralData;
import org.encog.neural.networks.XOR;

public class TestBufferedNeuralDataSet extends TestCase {

	public static final String FILENAME = "xor.bin";
	
	
	public void testBufferData() throws Exception 
	{
		BufferedNeuralDataSet set = new BufferedNeuralDataSet(new File(FILENAME));
		set.beginLoad(2, 1);
		for(int i=0;i<XOR.XOR_INPUT.length;i++) {
			BasicNeuralData input = new BasicNeuralData(XOR.XOR_INPUT[i]);
			BasicNeuralData ideal = new BasicNeuralData(XOR.XOR_IDEAL[i]);
			set.add(input,ideal);
		}
		set.endLoad();
		
		XOR.testXORDataSet(set);
		
	}	
}
