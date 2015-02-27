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

import org.encog.mathutil.matrices.Matrix;
import org.encog.mathutil.matrices.MatrixError;
import org.encog.mathutil.matrices.MatrixMath;

public class TestMatrixMath extends TestCase {
	
	public void testInverse() throws Throwable
	{
		double matrixData1[][] = {{1,2,3,4}};
		double matrixData2[][] = {{1},
			{2},
			{3},
			{4}
		};
		
		Matrix matrix1 = new Matrix(matrixData1);
		Matrix checkMatrix = new Matrix(matrixData2);
		
		Matrix matrix2 = MatrixMath.transpose(matrix1);
		
		TestCase.assertTrue(matrix2.equals(checkMatrix));
	}
	
	public void testDotProduct() throws Throwable
	{
		double matrixData1[][] = {{1,2,3,4}};
		double matrixData2[][] = {{5},
			{6},
			{7},
			{8}
		};
		
		Matrix matrix1 = new Matrix(matrixData1);
		Matrix matrix2 = new Matrix(matrixData2);
		
		double dotProduct = MatrixMath.dotProduct(matrix1,matrix2);
		
		TestCase.assertEquals(dotProduct, 70.0);
		
		// test dot product errors
		double nonVectorData[][] = {{1.0,2.0},{3.0,4.0}};
		double differentLengthData[][] = {{1.0}};
		Matrix nonVector = new Matrix(nonVectorData);
		Matrix differentLength = new Matrix(differentLengthData);
		
		try
		{
			MatrixMath.dotProduct(matrix1, nonVector);
			TestCase.assertTrue(false);
		}
		catch(MatrixError e)
		{		
		}
		
		try
		{
			MatrixMath.dotProduct(nonVector, matrix2);
			TestCase.assertTrue(false);
		}
		catch(MatrixError e)
		{		
		}
		
		try
		{
			MatrixMath.dotProduct(matrix1, differentLength);
			TestCase.assertTrue(false);
		}
		catch(MatrixError e)
		{		
		}
		
		
	}
	
	public void testMultiply() throws Throwable
	{
		double matrixData1[][] = {{1,4},
				{2,5},
				{3,6}
			};
		double matrixData2[][] = {{7,8,9},
				{10,11,12}};

		
		double matrixData3[][] = {{47,52,57},
				{64,71,78},
				{81,90,99}
		};
		
		Matrix matrix1 = new Matrix(matrixData1);
		Matrix matrix2 = new Matrix(matrixData2);
		
		Matrix matrix3 = new Matrix(matrixData3);
		
		Matrix result = MatrixMath.multiply(matrix1,matrix2);
		
		TestCase.assertTrue(result.equals(matrix3));
	}
	
	public static void testVerifySame()
	{
		double dataBase[][] = {{1.0,2.0},{3.0,4.0}};
		double dataTooManyRows[][] = {{1.0,2.0},{3.0,4.0},{5.0,6.0}};
		double dataTooManyCols[][] = {{1.0,2.0,3.0},{4.0,5.0,6.0}};
		Matrix base = new Matrix(dataBase);
		Matrix tooManyRows = new Matrix(dataTooManyRows);
		Matrix tooManyCols = new Matrix(dataTooManyCols);
		MatrixMath.add(base, base);
		try
		{
			MatrixMath.add(base, tooManyRows);
			TestCase.assertFalse(true);
		}
		catch(MatrixError e)
		{			
		}
		try
		{
			MatrixMath.add(base, tooManyCols);
			TestCase.assertFalse(true);
		}
		catch(MatrixError e)
		{			
		}
	}
	
	public void testDivide() throws Throwable
	{
		double data[][] = {{2.0,4.0},{6.0,8.0}};
		Matrix matrix = new Matrix(data);
		Matrix result = MatrixMath.divide(matrix, 2.0);
		TestCase.assertEquals(1.0, result.get(0,0));
	}
	
	public void testIdentity() throws Throwable
	{
		try
		{
			MatrixMath.identity(0);
			TestCase.assertTrue(false);
		}
		catch(MatrixError e)
		{
			
		}
		
		double checkData[][] = {{1,0},{0,1}};
		Matrix check = new Matrix(checkData);
		Matrix matrix = MatrixMath.identity(2);
		TestCase.assertTrue(check.equals(matrix));
	}
	
	public void testMultiplyScalar() throws Throwable
	{
		double data[][] = {{2.0,4.0},{6.0,8.0}};
		Matrix matrix = new Matrix(data);
		Matrix result = MatrixMath.multiply(matrix, 2.0);
		TestCase.assertEquals(4.0, result.get(0,0));
	}
	
	public void testDeleteRow() throws Throwable
	{
		double origData[][] = {{1.0,2.0},{3.0,4.0}};
		double checkData[][] = {{3.0,4.0}};
		Matrix orig = new Matrix(origData);
		Matrix matrix = MatrixMath.deleteRow(orig, 0);
		Matrix check = new Matrix(checkData);
		TestCase.assertTrue(check.equals(matrix));
		
		try
		{
			MatrixMath.deleteRow(orig, 10);
			TestCase.assertTrue(false);
		}
		catch(MatrixError e)
		{			
		}
	}
	
	public void testDeleteCol() throws Throwable
	{		
		double origData[][] = {{1.0,2.0},{3.0,4.0}};
		double checkData[][] = {{2.0},{4.0}};
		Matrix orig = new Matrix(origData);
		Matrix matrix = MatrixMath.deleteCol(orig, 0);
		Matrix check = new Matrix(checkData);
		TestCase.assertTrue(check.equals(matrix));
		
		try
		{
			MatrixMath.deleteCol(orig, 10);
			TestCase.assertTrue(false);
		}
		catch(MatrixError e)
		{			
		}
	}
	
	public void testCopy()
	{
		double data[][] = {{1.0,2.0},{3.0,4.0}};
		Matrix source = new Matrix(data);
		Matrix target = new Matrix(2,2);
		MatrixMath.copy(source, target);
		TestCase.assertTrue(source.equals(target));
	}
}
