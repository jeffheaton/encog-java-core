/*
 * Encog(tm) Core v3.1 - Java Version
 * http://www.heatonresearch.com/encog/
 * http://code.google.com/p/encog-java/
 
 * Copyright 2008-2012 Heaton Research, Inc.
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
package org.encog.ml.hmm.distributions;

import java.util.Arrays;
import java.util.Random;

import org.encog.mathutil.matrices.Matrix;
import org.encog.mathutil.matrices.MatrixMath;
import org.encog.mathutil.matrices.decomposition.CholeskyDecomposition;
import org.encog.ml.data.MLDataPair;
import org.encog.ml.data.MLDataSet;
import org.encog.ml.data.basic.BasicMLData;
import org.encog.ml.data.basic.BasicMLDataPair;
import org.encog.util.EngineArray;

/**
 * A continuous distribution represents an infinite range of choices between two
 * real numbers. A gaussian distribution is used to distribute the probability.
 * 
 */
public class ContinousDistribution implements StateDistribution {
	
	/**
	 * The serial id.
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * The dimensions.
	 */
	final private int dimension;
	
	/**
	 * The means for each dimension.
	 */
	final private double[] mean;
	
	/**
	 * The covariance matrix.
	 */
	final private Matrix covariance;
	
	/**
	 * The covariance left side.
	 */
	private Matrix covarianceL = null;
	
	/**
	 * The covariance inverse.
	 */
	private Matrix covarianceInv = null;
	
	/**
	 * The covariance determinant.
	 */
	private double covarianceDet;
	
	/**
	 * Random number generator.
	 */
	private final static Random randomGenerator = new Random();
	
	/**
	 * Used to perform a decomposition.
	 */
	private CholeskyDecomposition cd;

	/**
	 * Construct a continuous distribution.
	 * @param mean The mean.
	 * @param covariance The covariance.
	 */
	public ContinousDistribution(final double[] mean,
			final double[][] covariance) {
		this.dimension = covariance.length;
		this.mean = EngineArray.arrayCopy(mean);
		this.covariance = new Matrix(covariance);
		update(covariance);
	}

	/**
	 * Construct a continuous distribution with the specified number of dimensions.
	 * @param dimension The dimensions.
	 */
	public ContinousDistribution(final int dimension) {
		if (dimension <= 0) {
			throw new IllegalArgumentException();
		}

		this.dimension = dimension;
		this.mean = new double[dimension];
		this.covariance = new Matrix(dimension, dimension);

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ContinousDistribution clone() {
		try {
			return (ContinousDistribution) super.clone();
		} catch (final CloneNotSupportedException e) {
			throw new AssertionError(e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void fit(final MLDataSet co) {
		final double[] weights = new double[co.size()];
		Arrays.fill(weights, 1. / co.size());

		fit(co, weights);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void fit(final MLDataSet co, final double[] weights) {
		if ((co.size() < 1) || (co.size() != weights.length)) {
			throw new IllegalArgumentException();
		}

		// Compute mean
		final double[] mean = new double[this.dimension];
		for (int r = 0; r < this.dimension; r++) {
			int i = 0;

			for (final MLDataPair o : co) {
				mean[r] += o.getInput().getData(r) * weights[i++];
			}
		}

		// Compute covariance
		final double[][] covariance = new double[this.dimension][this.dimension];
		int i = 0;
		for (final MLDataPair o : co) {
			final double[] obs = o.getInput().getData();
			final double[] omm = new double[obs.length];

			for (int j = 0; j < obs.length; j++) {
				omm[j] = obs[j] - mean[j];
			}

			for (int r = 0; r < this.dimension; r++) {
				for (int c = 0; c < this.dimension; c++) {
					covariance[r][c] += omm[r] * omm[c] * weights[i];
				}
			}

			i++;
		}

		update(covariance);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public MLDataPair generate() {
		final double[] d = new double[this.dimension];

		for (int i = 0; i < this.dimension; i++) {
			d[i] = ContinousDistribution.randomGenerator.nextGaussian();
		}

		final double[] d2 = MatrixMath.multiply(this.covarianceL, d);
		return new BasicMLDataPair(new BasicMLData(EngineArray.add(d2,
				this.mean)));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public double probability(final MLDataPair o) {
		final double[] v = o.getInputArray();
		final Matrix vmm = Matrix.createColumnMatrix(EngineArray.subtract(v,
				this.mean));
		final Matrix t = MatrixMath.multiply(this.covarianceInv, vmm);
		final double expArg = MatrixMath.multiply(MatrixMath.transpose(vmm), t)
				.get(0, 0) * -0.5;
		return Math.exp(expArg)
				/ (Math.pow(2.0 * Math.PI, this.dimension / 2.0) * Math.pow(
						this.covarianceDet, 0.5));
	}

	/**
	 * Update the covariance. 
	 * @param covariance The new covariance.
	 */
	public void update(final double[][] covariance) {
		this.cd = new CholeskyDecomposition(new Matrix(covariance));
		this.covarianceL = this.cd.getL();
		this.covarianceInv = this.cd.inverseCholesky();
		this.covarianceDet = this.cd.getDeterminant();
	}

	/**
	 * @return The mean for the dimensions of the gaussian curve.
	 */
	public double[] getMean() {
		return this.mean;
	}

	/**
	 * @return The covariance matrix.
	 */
	public Matrix getCovariance() {
		return this.covariance;
	}
}
