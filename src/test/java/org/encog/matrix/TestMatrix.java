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

public class TestMatrix extends TestCase {
	
	public void testRowsAndCols() throws Throwable
	{
		Matrix matrix = new Matrix(6,3);
		TestCase.assertEquals(matrix.getRows(), 6);
		TestCase.assertEquals(matrix.getCols(), 3);
		
		matrix.set(1,2, 1.5);
		TestCase.assertEquals(matrix.get(1,2), 1.5 );
	}
	
	public void testRowAndColRangeUnder() throws Throwable
	{
		Matrix matrix = new Matrix(6,3);
		
		// make sure set registers error on under-bound row
		try {
			matrix.set(-1, 0, 1);
			TestCase.assertTrue(false); // should have thrown an exception
		}
		catch(MatrixError e)
		{
		}
		
		// make sure set registers error on under-bound col
		try {
			matrix.set(0, -1, 1);
			TestCase.assertTrue(false); // should have thrown an exception
		}
		catch(MatrixError e)
		{
		}
		
		// make sure get registers error on under-bound row
		try {
			matrix.get(-1, 0 );
			TestCase.assertTrue(false); // should have thrown an exception
		}
		catch(MatrixError e)
		{
		}
		
		// make sure set registers error on under-bound col
		try {
			matrix.get(0, -1 );
			TestCase.assertTrue(false); // should have thrown an exception
		}
		catch(MatrixError e)
		{
		}
	}
	
	public void testRowAndColRangeOver() throws Throwable
	{
		Matrix matrix = new Matrix(6,3);
		
		// make sure set registers error on under-bound row
		try {
			matrix.set(6, 0, 1);
			TestCase.assertTrue(false); // should have thrown an exception
		}
		catch(MatrixError e)
		{
		}
		
		// make sure set registers error on under-bound col
		try {
			matrix.set(0, 3, 1);
			TestCase.assertTrue(false); // should have thrown an exception
		}
		catch(MatrixError e)
		{
		}
		
		// make sure get registers error on under-bound row
		try {
			matrix.get(6, 0 );
			TestCase.assertTrue(false); // should have thrown an exception
		}
		catch(MatrixError e)
		{
		}
		
		// make sure set registers error on under-bound col
		try {
			matrix.get(0, 3 );
			TestCase.assertTrue(false); // should have thrown an exception
		}
		catch(MatrixError e)
		{
		}
	}
	
	public void testMatrixConstruct() throws Throwable
	{
		double m[][] = {
				{1,2,3,4},
				{5,6,7,8},
				{9,10,11,12},
				{13,14,15,16} };
		Matrix matrix = new Matrix(m);
		TestCase.assertEquals(matrix.getRows(), 4);
		TestCase.assertEquals(matrix.getCols(), 4);
	}
	
	public void testMatrixEquals() throws Throwable
	{
		double m1[][] = {
				{1,2},
				{3,4} };
		
		double m2[][] = {
				{0,2},
				{3,4} };	
	
		Matrix matrix1 = new Matrix(m1);
		Matrix matrix2 = new Matrix(m1);
		
		TestCase.assertTrue(matrix1.equals(matrix2));
		
		matrix2 = new Matrix(m2);
		
		TestCase.assertFalse(matrix1.equals(matrix2));
	}
	
	public void testMatrixEqualsPrecision() throws Throwable
	{
		double m1[][] = {
				{1.1234,2.123},
				{3.123,4.123} };
		
		double m2[][] = {
				{1.123,2.123},
				{3.123,4.123} };
		
		Matrix matrix1 = new Matrix(m1);
		Matrix matrix2 = new Matrix(m2);
		
		TestCase.assertTrue(matrix1.equals(matrix2,3));
		TestCase.assertFalse(matrix1.equals(matrix2,4));
		
		double m3[][] = {
				{1.1,2.1},
				{3.1,4.1} };
		
		double m4[][] = {
				{1.2,2.1},
				{3.1,4.1} };
		
		Matrix matrix3 = new Matrix(m3);
		Matrix matrix4 = new Matrix(m4);
		TestCase.assertTrue(matrix3.equals(matrix4,0));
		TestCase.assertFalse(matrix3.equals(matrix4,1));
		
		try
		{
			matrix3.equals(matrix4,-1);
			TestCase.assertTrue( false);
		}
		catch(MatrixError e)
		{
			
		}
		
		try
		{
			matrix3.equals(matrix4,19);
			TestCase.assertTrue( false);
		}
		catch(MatrixError e)
		{
			
		}
		
	}
	
	public void testMatrixMultiply() throws Throwable
	{
		double a[][] = {
				{1,0,2},
				{-1,3,1}
		};
		
		double b[][] = {
				{3,1},
				{2,1},
				{1,0}
		};
		
		double c[][] = {
				{5,1},
				{4,2}
		};
		
		Matrix matrixA = new Matrix(a);
		Matrix matrixB = new Matrix(b);
		Matrix matrixC = new Matrix(c);
		
		Matrix result = matrixA.clone();
		result = MatrixMath.multiply(matrixA,matrixB); 

		TestCase.assertTrue(result.equals(matrixC));
		
		double a2[][] = {
				{1,2,3,4},
				{5,6,7,8}
		};
		
		double b2[][] = {
				{1,2,3},
				{4,5,6},
				{7,8,9},
				{10,11,12}
		};
		
		double c2[][] = {
				{70,80,90},
				{158,184,210}
		};
		
		matrixA = new Matrix(a2);
		matrixB = new Matrix(b2);
		matrixC = new Matrix(c2);
		
		result = MatrixMath.multiply(matrixA, matrixB);
		TestCase.assertTrue(result.equals(matrixC));
		
		result = matrixB.clone();
		try
		{
			MatrixMath.multiply(matrixB,matrixA);
			TestCase.assertTrue(false);
		}
		catch(MatrixError e)
		{
			
		}	
	}
	
	public void testBoolean() throws Throwable
	{
		boolean matrixDataBoolean[][] = { 
				{true,false},
				{false,true}
		};
		
		double matrixDataDouble[][] = {
				{1.0,-1.0},
				{-1.0,1.0},
		};
		
		Matrix matrixBoolean = new Matrix(matrixDataBoolean);
		Matrix matrixDouble = new Matrix(matrixDataDouble);
		
		TestCase.assertTrue(matrixBoolean.equals(matrixDouble));
	}
	
	public void testGetRow() throws Throwable
	{
		double matrixData1[][] = {
				{1.0,2.0},
				{3.0,4.0}
		};
		double matrixData2[][] = {
				{3.0,4.0}
		};
		
		Matrix matrix1 = new Matrix(matrixData1);
		Matrix matrix2 = new Matrix(matrixData2);
		
		Matrix matrixRow = matrix1.getRow(1);
		TestCase.assertTrue(matrixRow.equals(matrix2));
		
		try
		{
			matrix1.getRow(3);
			TestCase.assertTrue(false);
		}
		catch(MatrixError e)
		{
			TestCase.assertTrue(true);
		}
	}
	
	public void testGetCol() throws Throwable
	{
		double matrixData1[][] = {
				{1.0,2.0},
				{3.0,4.0}
		};
		double matrixData2[][] = {
				{2.0},
				{4.0}
		};
		
		Matrix matrix1 = new Matrix(matrixData1);
		Matrix matrix2 = new Matrix(matrixData2);
		
		Matrix matrixCol = matrix1.getCol(1);
		TestCase.assertTrue(matrixCol.equals(matrix2));
		
		try
		{
			matrix1.getCol(3);
			TestCase.assertTrue(false);
		}
		catch(MatrixError e)
		{
			TestCase.assertTrue(true);
		}
	}
	
	public void testZero() throws Throwable
	{
		double doubleData[][] = { {0,0}, {0,0} };
		Matrix matrix = new Matrix(doubleData);
		TestCase.assertTrue(matrix.isZero());
	}
	
	public void testSum() throws Throwable
	{
		double doubleData[][] = { {1,2}, {3,4} };
		Matrix matrix = new Matrix(doubleData);
		TestCase.assertEquals((int)matrix.sum(), 1+2+3+4);
	}
	
	public void testRowMatrix() throws Throwable
	{
		double matrixData[] = {1.0,2.0,3.0,4.0};
		Matrix matrix = Matrix.createRowMatrix(matrixData);
		TestCase.assertEquals(matrix.get(0,0), 1.0);
		TestCase.assertEquals(matrix.get(0,1), 2.0);
		TestCase.assertEquals(matrix.get(0,2), 3.0);
		TestCase.assertEquals(matrix.get(0,3), 4.0);
	}
	
	public void testColumnMatrix() throws Throwable
	{
		double matrixData[] = {1.0,2.0,3.0,4.0};
		Matrix matrix = Matrix.createColumnMatrix(matrixData);
		TestCase.assertEquals(matrix.get(0,0), 1.0);
		TestCase.assertEquals(matrix.get(1,0), 2.0);
		TestCase.assertEquals(matrix.get(2,0), 3.0);
		TestCase.assertEquals(matrix.get(3,0), 4.0);
	}
	
	public void testAdd() throws Throwable
	{
		double matrixData[] = {1.0,2.0,3.0,4.0};
		Matrix matrix = Matrix.createColumnMatrix(matrixData);
		matrix.add(0, 0, 1);
		TestCase.assertEquals(matrix.get(0, 0), 2.0);
	}
	
	public void testClear() throws Throwable
	{
		double matrixData[] = {1.0,2.0,3.0,4.0};
		Matrix matrix = Matrix.createColumnMatrix(matrixData);
		matrix.clear();
		TestCase.assertEquals(matrix.get(0, 0), 0.0);
		TestCase.assertEquals(matrix.get(1, 0), 0.0);
		TestCase.assertEquals(matrix.get(2, 0), 0.0);
		TestCase.assertEquals(matrix.get(3, 0), 0.0);
	}
	
	public void testIsVector() throws Throwable
	{
		double matrixData[] = {1.0,2.0,3.0,4.0};
		Matrix matrixCol = Matrix.createColumnMatrix(matrixData);
		Matrix matrixRow = Matrix.createRowMatrix(matrixData);
		TestCase.assertTrue(matrixCol.isVector());
		TestCase.assertTrue(matrixRow.isVector());
		double matrixData2[][] = {{1.0,2.0},{3.0,4.0}};
		Matrix matrix = new Matrix(matrixData2);
		TestCase.assertFalse(matrix.isVector());
	}
	
	public void testIsZero() throws Throwable
	{
		double matrixData[] = {1.0,2.0,3.0,4.0};
		Matrix matrix = Matrix.createColumnMatrix(matrixData);
		TestCase.assertFalse(matrix.isZero());
		double matrixData2[] = {0.0,0.0,0.0,0.0};
		Matrix matrix2 = Matrix.createColumnMatrix(matrixData2);
		TestCase.assertTrue(matrix2.isZero());

	}
	
	public void testPackedArray() throws Throwable
	{
		double matrixData[][] = {{1.0,2.0},{3.0,4.0}};
		Matrix matrix = new Matrix(matrixData);
		double matrixData2[] = matrix.toPackedArray();
		TestCase.assertEquals(4, matrixData2.length);
		TestCase.assertEquals(1.0,matrix.get(0, 0));
		TestCase.assertEquals(2.0,matrix.get(0, 1));
		TestCase.assertEquals(3.0,matrix.get(1, 0));
		TestCase.assertEquals(4.0,matrix.get(1, 1));
		
		Matrix matrix2 = new Matrix(2,2);
		matrix2.fromPackedArray(matrixData2, 0);
		TestCase.assertTrue(matrix.equals(matrix2));
	}
	
	public void testPackedArray2() throws Throwable
	{
		double data[] = {1.0,2.0,3.0,4.0};
		Matrix matrix = new Matrix(1,4);
		matrix.fromPackedArray(data, 0);
		TestCase.assertEquals(1.0, matrix.get(0, 0));
		TestCase.assertEquals(2.0, matrix.get(0, 1));
		TestCase.assertEquals(3.0, matrix.get(0, 2));
	}
	
	public void testSize() throws Throwable
	{
		double data[][] = {{1.0,2.0},{3.0,4.0}};
		Matrix matrix = new Matrix(data);
		TestCase.assertEquals(4, matrix.size());
	}
	
	public void testVectorLength() throws Throwable
	{
		double vectorData[] = {1.0,2.0,3.0,4.0};
		Matrix vector = Matrix.createRowMatrix(vectorData);
		TestCase.assertEquals(5, (int)MatrixMath.vectorLength(vector));
		
		Matrix nonVector = new Matrix(2,2);
		try
		{
			MatrixMath.vectorLength(nonVector);
			TestCase.assertTrue(false);
		}
		catch(MatrixError e)
		{
			
		}
	}

}
