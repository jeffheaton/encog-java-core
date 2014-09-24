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
package org.encog.matrix;

import junit.framework.TestCase;

import org.encog.mathutil.matrices.BiPolarUtil;
import org.encog.mathutil.matrices.Matrix;

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
