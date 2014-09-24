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
package org.encog.mathutil.matrices.decomposition;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.encog.mathutil.matrices.Matrix;
import org.junit.Test;

/**
 * Simple tests for the EigenvalueDecomposition object. 
 */
public class EigenvalueDecompositionTest {

	private Matrix matrix1 = new Matrix(
			new double[][] {
					{ 2.0, 5.1 },
					{ -0.5, 0.9 }
			});

	private Matrix matrix2 = new Matrix(
			new double[][] {
					{ 2.0, 5.5 },
					{ 5.5, -1.0 }
			});

	@Test
	public void testEigenvalueDecomposition() {
		new EigenvalueDecomposition(matrix1);
	}

	@Test
	public void testGetD_asymentric() {
		EigenvalueDecomposition decomposition =
				new EigenvalueDecomposition(matrix1);
		Matrix d = decomposition.getD();
		assertEquals(2, d.getCols());
		assertEquals(2, d.getRows());
		assertEquals(1.45, d.get(0, 0), 1e-10);
		assertEquals(1.4991664351, d.get(0, 1), 1e-10);
		assertEquals(-1.4991664351, d.get(1, 0), 1e-10);
		assertEquals(1.45, d.get(1, 1), 1e-10);
	}

	@Test
	public void testGetD_symentric() {
		EigenvalueDecomposition decomposition =
				new EigenvalueDecomposition(matrix2);
		Matrix d = decomposition.getD();
		assertEquals(2, d.getCols());
		assertEquals(2, d.getRows());
		assertEquals(-5.2008771255, d.get(0, 0), 1e-10);
		assertEquals(0.0, d.get(0, 1), 1e-10);
		assertEquals(0.0, d.get(1, 0), 1e-10);
		assertEquals(6.2008771255, d.get(1, 1), 1e-10);
	}

	@Test
	public void testGetImagEigenvalues_asymentric() {
		EigenvalueDecomposition decomposition =
				new EigenvalueDecomposition(matrix1);
		double[] d = decomposition.getImagEigenvalues();
		assertEquals(2, d.length);
		assertEquals(1.4991664351, d[0], 1e-10);
		assertEquals(-1.4991664351, d[1], 1e-10);
	}

	@Test
	public void testGetImagEigenvalues_symentric() {
		EigenvalueDecomposition decomposition =
				new EigenvalueDecomposition(matrix2);
		double[] d = decomposition.getImagEigenvalues();
		assertEquals(2, d.length);
		assertEquals(0.0, d[0], 1e-10);
		assertEquals(0.0, d[1], 1e-10);
	}

	@Test
	public void testGetRealEigenvalues_asymentric() {
		EigenvalueDecomposition decomposition =
				new EigenvalueDecomposition(matrix1);
		double[] d = decomposition.getRealEigenvalues();
		assertEquals(2, d.length);
		assertEquals(1.45, d[0], 1e-10);
		assertEquals(1.45, d[1], 1e-10);
	}

	@Test
	public void testGetRealEigenvalues_symentric() {
		EigenvalueDecomposition decomposition =
				new EigenvalueDecomposition(matrix2);
		double[] d = decomposition.getRealEigenvalues();
		assertEquals(2, d.length);
		assertEquals(-5.2008771255, d[0], 1e-10);
		assertEquals(6.2008771255, d[1], 1e-10);
	}

	@Test
	public void testGetV_asymentric() {
		EigenvalueDecomposition decomposition =
				new EigenvalueDecomposition(matrix1);
		Matrix d = decomposition.getV();
		assertEquals(2, d.getCols());
		assertEquals(2, d.getRows());
		assertEquals(2.9983328701, d.get(0, 0), 1e-10);
		assertEquals(-1.1, d.get(0, 1), 1e-10);
		assertEquals(0.0, d.get(1, 0), 1e-10);
		assertEquals(1.0, d.get(1, 1), 1e-10);
	}

	@Test
	public void testIsSymmetric() {
		assertTrue(
				"An empty matrix should be symmetric",
				EigenvalueDecomposition.isSymmetric(new double[][] { }));
		assertFalse(
				"An matrix with a row but no values should not be symmetric",
				EigenvalueDecomposition.isSymmetric(new double[][] { { } }));
		assertTrue(
				"A singleton matrix should be symmetric",
				EigenvalueDecomposition.isSymmetric(new double[][] { { 0 } }));
		assertFalse(
				"A malformed matrix should not be symmetric",
				EigenvalueDecomposition.isSymmetric(new double[][] {
						{ 0, 1 }
				}));
		assertTrue(
				"This simple matrix should be symmetric",
				EigenvalueDecomposition.isSymmetric(new double[][] {
						{ 0, 1 },
						{ 1, 0 },
				}));
		assertFalse(
				"This simple matrix should not be symmetric",
				EigenvalueDecomposition.isSymmetric(new double[][] {
						{ 0, 1 },
						{ 2, 0 },
				}));
		assertTrue(
				"This matrix should be symmetric",
				EigenvalueDecomposition.isSymmetric(new double[][] {
						{ 0, 1, 3, 8 },
						{ 1, 5, 7, 1 },
						{ 3, 7, 9, 0 },
						{ 8, 1, 0, 6 },
				}));
		assertFalse(
				"This matrix should not be symmetric",
				EigenvalueDecomposition.isSymmetric(new double[][] {
						{ 8, 1, 0, 6 },
						{ 3, 7, 9, 0 },
						{ 1, 5, 7, 1 },
						{ 0, 1, 3, 8 },
				}));
	}
}
