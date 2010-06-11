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
