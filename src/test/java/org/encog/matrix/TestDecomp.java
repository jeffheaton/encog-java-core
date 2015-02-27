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

import junit.framework.Assert;
import junit.framework.TestCase;

import org.encog.mathutil.matrices.Matrix;
import org.encog.mathutil.matrices.decomposition.CholeskyDecomposition;
import org.encog.mathutil.matrices.decomposition.EigenvalueDecomposition;
import org.encog.mathutil.matrices.decomposition.QRDecomposition;
import org.encog.mathutil.matrices.decomposition.SingularValueDecomposition;

public class TestDecomp extends TestCase {
	public void testSVD()
	{
		double m[][] = {
				{1,2,3,4},
				{5,6,7,8},
				{9,10,11,12},
				{13,14,15,16} };
		Matrix matrix = new Matrix(m);
		SingularValueDecomposition svd = new SingularValueDecomposition(matrix);
		Assert.assertEquals(2147483647, (int)(svd.cond()) );
		double[] d = svd.getSingularValues();
		
		Assert.assertEquals(4, d.length);

	}
	
	public void testCholesky()
	{
		double m1[][] = {
				{1,0,0,0},
				{0,1,0,0},
				{0,0,1,0},
				{0,0,0,1} };
		Matrix matrix1 = new Matrix(m1);
		
		double m2[][] = {
				{17,18,19,20},
				{21,22,23,24},
				{25,27,28,29},
				{37,33,31,30} };
		Matrix matrix2 = new Matrix(m2);
		
		CholeskyDecomposition c = new CholeskyDecomposition(matrix1);
		c.solve(matrix2);

		Matrix mx = c.getL();
		
		Assert.assertEquals(1.0, mx.get(0,0));
		Assert.assertEquals(1.0, mx.get(1,1));
		Assert.assertEquals(1.0, mx.get(2,2));
		Assert.assertEquals(4, mx.getRows());
		Assert.assertEquals(4, mx.getCols());
	}
	
	public void testEigenvalue()
	{
		double m1[][] = {
				{1,0,0,0},
				{0,1,0,0},
				{0,0,1,0},
				{0,0,0,1} };
		Matrix matrix1 = new Matrix(m1);
			
		EigenvalueDecomposition e = new EigenvalueDecomposition(matrix1);
		
		double[] d1 = e.getImagEigenvalues();
		double[] d2 = e.getRealEigenvalues();
		Matrix mx = e.getV();
		
		Assert.assertEquals(1.0, mx.get(0,0));
		Assert.assertEquals(1.0, mx.get(1,1));
		Assert.assertEquals(1.0, mx.get(2,2));
		Assert.assertEquals(4, mx.getRows());
		Assert.assertEquals(4, mx.getCols());
	}
	
	public void testEigenvalue2()
	{
		double m1[][] = {
				{1,0,0,0},
				{0,1,0,0},
				{0,0,1,0},
				{1,0,0,1} };
		Matrix matrix1 = new Matrix(m1);
			
		EigenvalueDecomposition e = new EigenvalueDecomposition(matrix1);
		
		double[] d1 = e.getImagEigenvalues();
		double[] d2 = e.getRealEigenvalues();
		Matrix mx = e.getV();
		
		Assert.assertEquals(0.0, mx.get(0,0));
		Assert.assertEquals(0.0, mx.get(1,1));
		Assert.assertEquals(1.0, mx.get(2,2));
		Assert.assertEquals(4, mx.getRows());
		Assert.assertEquals(4, mx.getCols());
	}

	public void testQR()
	{
		double m1[][] = {
				{1,0,0,0},
				{0,1,0,0},
				{0,0,1,0},
				{0,0,0,1} };
		Matrix matrix1 = new Matrix(m1);
		
		double m2[][] = {
				{17,18,19,20},
				{21,22,23,24},
				{25,27,28,29},
				{37,33,31,30} };
		Matrix matrix2 = new Matrix(m2);
		
		QRDecomposition c = new QRDecomposition(matrix1);
		Matrix mx = c.solve(matrix2);
		
		Assert.assertEquals(17.0, mx.get(0,0));
		Assert.assertEquals(22.0, mx.get(1,1));
		Assert.assertEquals(28.0, mx.get(2,2));
		Assert.assertEquals(4, mx.getRows());
		Assert.assertEquals(4, mx.getCols());
	}

	
	
	// QRDecomposition
}
