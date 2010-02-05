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
