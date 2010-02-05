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

package org.encog.matrix;

import junit.framework.TestCase;

public class TestBiPolarUtil extends TestCase {

	public void testBipolar2double() throws Throwable
	{
		// test a 1x4
		boolean[] booleanData1 = { true, false, true, false };
		double[] checkData1 = {1,-1,1,-1};
		Matrix matrix1 = Matrix.createRowMatrix(BiPolarUtil.bipolar2double(booleanData1));
		Matrix checkMatrix1 = Matrix.createRowMatrix(checkData1);
		TestCase.assertTrue( matrix1.equals(checkMatrix1));
		
		// test a 2x2
		boolean booleanData2[][] = {{true,false},{false,true}};
		double checkData2[][] = { {1,-1}, {-1,1} };
		Matrix matrix2 = new Matrix(BiPolarUtil.bipolar2double(booleanData2));
		Matrix checkMatrix2 = new Matrix(checkData2);
		TestCase.assertTrue(matrix2.equals(checkMatrix2));
	}
	
	public void testDouble2bipolar() throws Throwable
	{
		// test a 1x4
		double doubleData1[] = { 1, -1, 1, -1 };
		boolean checkData1[] = { true,false, true, false };
		boolean result1[] = BiPolarUtil.double2bipolar(doubleData1);
		for(int i=0;i<checkData1.length;i++)
		{
			TestCase.assertEquals(checkData1[i], result1[i]);
		}
		
		// test a 2x2
		double doubleData2[][] = { {1,-1}, {-1,1} };
		boolean checkData2[][] = { {true,false}, {false,true} };
		boolean result2[][] = BiPolarUtil.double2bipolar(doubleData2);
		
		for(int r = 0; r< doubleData2.length; r++ )
		{
			for(int c = 0; c<doubleData2[0].length;c ++)
			{
				TestCase.assertEquals(result2[r][c], checkData2[r][c]);
			}
		}
		
	}
	
	public void testBinary() throws Throwable
	{
		TestCase.assertEquals(0.0,BiPolarUtil.normalizeBinary(-1));
		TestCase.assertEquals(1.0,BiPolarUtil.normalizeBinary(2));
		TestCase.assertEquals(1.0,BiPolarUtil.toBinary(1));
		TestCase.assertEquals(-1.0,BiPolarUtil.toBiPolar(0));
		TestCase.assertEquals(1.0,BiPolarUtil.toNormalizedBinary(10));
	}
}
