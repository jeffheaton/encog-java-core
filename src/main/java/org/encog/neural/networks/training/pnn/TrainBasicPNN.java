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
package org.encog.neural.networks.training.pnn;

import org.encog.ml.MLMethod;
import org.encog.ml.TrainingImplementationType;
import org.encog.ml.data.MLData;
import org.encog.ml.data.MLDataPair;
import org.encog.ml.data.MLDataSet;
import org.encog.ml.data.basic.BasicMLData;
import org.encog.ml.data.basic.BasicMLDataPair;
import org.encog.ml.data.basic.BasicMLDataSet;
import org.encog.ml.train.BasicTraining;
import org.encog.neural.networks.training.propagation.TrainingContinuation;
import org.encog.neural.pnn.BasicPNN;
import org.encog.neural.pnn.PNNKernelType;
import org.encog.neural.pnn.PNNOutputMode;
import org.encog.util.EngineArray;

/**
 * Train a PNN.
 */
public class TrainBasicPNN extends BasicTraining implements CalculationCriteria {

	/**
	 * The default max error.
	 */
	public static final double DEFAULT_MAX_ERROR = 0.05;

	/**
	 * The default minimum improvement before stop.
	 */
	public static final double DEFAULT_MIN_IMPROVEMENT = 0.0001;

	/**
	 * THe default sigma low value.
	 */
	public static final double DEFAULT_SIGMA_LOW = 0.0001;

	/**
	 * The default sigma high value.
	 */
	public static final double DEFAULT_SIGMA_HIGH = 10.0;

	/**
	 * The default number of sigmas to evaluate between the low and high.
	 */
	public static final int DEFAULT_NUM_SIGMAS = 10;

	/**
	 * Temp storage for derivative computation.
	 */
	private double[] v;

	/**
	 * Temp storage for derivative computation.
	 */
	private double[] w;

	/**
	 * Temp storage for derivative computation.
	 */
	private double[] dsqr;

	/**
	 * The network to train.
	 */
	private final BasicPNN network;

	/**
	 * The training data.
	 */
	private final MLDataSet training;

	/**
	 * The maximum error to allow.
	 */
	private double maxError;

	/**
	 * The minimum improvement allowed.
	 */
	private double minImprovement;

	/**
	 * The low value for the sigma search.
	 */
	private double sigmaLow;

	/**
	 * The high value for the sigma search.
	 */
	private double sigmaHigh;

	/**
	 * The number of sigmas to evaluate between the low and high.
	 */
	private int numSigmas;

	/**
	 * Have the samples been loaded.
	 */
	private boolean samplesLoaded;

	/**
	 * Train a BasicPNN.
	 * 
	 * @param network
	 *            The network to train.
	 * @param training
	 *            The training data.
	 */
	public TrainBasicPNN(final BasicPNN network, final MLDataSet training) {
		super(TrainingImplementationType.OnePass);
		this.network = network;
		this.training = training;

		this.maxError = TrainBasicPNN.DEFAULT_MAX_ERROR;
		this.minImprovement = TrainBasicPNN.DEFAULT_MIN_IMPROVEMENT;
		this.sigmaLow = TrainBasicPNN.DEFAULT_SIGMA_LOW;
		this.sigmaHigh = TrainBasicPNN.DEFAULT_SIGMA_HIGH;
		this.numSigmas = TrainBasicPNN.DEFAULT_NUM_SIGMAS;
		this.samplesLoaded = false;
	}

	/**
	 * Calculate the error with multiple sigmas.
	 * 
	 * @param x
	 *            The data.
	 * @param der1
	 *            The first derivative.
	 * @param der2
	 *            The 2nd derivatives.
	 * @param der
	 *            Calculate the derivative.
	 * @return The error.
	 */
	@Override
	public final double calcErrorWithMultipleSigma(final double[] x,
			final double[] der1, final double[] der2, final boolean der) {
		int ivar;
		double err;

		for (ivar = 0; ivar < this.network.getInputCount(); ivar++) {
			this.network.getSigma()[ivar] = x[ivar];
		}

		if (!der) {
			return calculateError(this.network.getSamples(), false);
		}

		err = calculateError(this.network.getSamples(), true);

		for (ivar = 0; ivar < this.network.getInputCount(); ivar++) {
			der1[ivar] = this.network.getDeriv()[ivar];
			der2[ivar] = this.network.getDeriv2()[ivar];
		}

		return err;
	}

	/**
	 * Calculate the error using a common sigma.
	 * 
	 * @param sig
	 *            The sigma to use.
	 * @return The training error.
	 */
	@Override
	public final double calcErrorWithSingleSigma(final double sig) {
		int ivar;

		for (ivar = 0; ivar < this.network.getInputCount(); ivar++) {
			this.network.getSigma()[ivar] = sig;
		}

		return calculateError(this.network.getSamples(), false);
	}

	/**
	 * Calculate the error for the entire training set.
	 * 
	 * @param training
	 *            Training set to use.
	 * @param deriv
	 *            Should we find the derivative.
	 * @return The error.
	 */
	public final double calculateError(final MLDataSet training, 
			final boolean deriv) {

		double err, totErr;
		double diff;
		totErr = 0.0;

		if (deriv) {
			final int num = (this.network.isSeparateClass()) ? this.network
					.getInputCount() * this.network.getOutputCount()
					: this.network.getInputCount();
			for (int i = 0; i < num; i++) {
				this.network.getDeriv()[i] = 0.0;
				this.network.getDeriv2()[i] = 0.0;
			}
		}

		this.network.setExclude((int) training.getRecordCount());

		final MLDataPair pair = BasicMLDataPair.createPair(
				training.getInputSize(), training.getIdealSize());

		final double[] out = new double[this.network.getOutputCount()];

		for (int r = 0; r < training.getRecordCount(); r++) {
			training.getRecord(r, pair);
			this.network.setExclude(this.network.getExclude() - 1);

			err = 0.0;

			final MLData input = pair.getInput();
			final MLData target = pair.getIdeal();

			if (this.network.getOutputMode() == PNNOutputMode.Unsupervised) {
				if (deriv) {
					final MLData output = computeDeriv(input, target);
					for (int z = 0; z < this.network.getOutputCount(); z++) {
						out[z] = output.getData(z);
					}
				} else {
					final MLData output = this.network.compute(input);
					for (int z = 0; z < this.network.getOutputCount(); z++) {
						out[z] = output.getData(z);
					}
				}
				for (int i = 0; i < this.network.getOutputCount(); i++) {
					diff = input.getData(i) - out[i];
					err += diff * diff;
				}
			} else if (this.network.getOutputMode() == PNNOutputMode.Classification) {
				final int tclass = (int) target.getData(0);
				MLData output;

				if (deriv) {
					output = computeDeriv(input, pair.getIdeal());
				} else {
					output = this.network.compute(input);
				}

				EngineArray.arrayCopy(output.getData(),out);

				for (int i = 0; i < out.length; i++) {
					if (i == tclass) {
						diff = 1.0 - out[i];
						err += diff * diff;
					} else {
						err += out[i] * out[i];
					}
				}
			}

			else if (this.network.getOutputMode() == PNNOutputMode.Regression) {
				if (deriv) {
					final MLData output = this.network.compute(input);
					for (int z = 0; z < this.network.getOutputCount(); z++) {
						out[z] = output.getData(z);
					}
				} else {
					final MLData output = this.network.compute(input);
					for (int z = 0; z < this.network.getOutputCount(); z++) {
						out[z] = output.getData(z);
					}
				}
				for (int i = 0; i < this.network.getOutputCount(); i++) {
					diff = target.getData(i) - out[i];
					err += diff * diff;
				}
			}

			totErr += err;
		}

		this.network.setExclude(-1);

		this.network.setError(totErr / training.getRecordCount());
		if (deriv) {
			for (int i = 0; i < this.network.getDeriv().length; i++) {
				this.network.getDeriv()[i] /= training.getRecordCount();
				this.network.getDeriv2()[i] /= training.getRecordCount();
			}
		}

		if ((this.network.getOutputMode() == PNNOutputMode.Unsupervised)
				|| (this.network.getOutputMode() == PNNOutputMode.Regression)) {
			this.network.setError(this.network.getError()
					/ this.network.getOutputCount());
			if (deriv) {
				for (int i = 0; i < this.network.getInputCount(); i++) {
					this.network.getDeriv()[i] /= this.network.getOutputCount();
					this.network.getDeriv2()[i] /= this.network
							.getOutputCount();
				}
			}
		}

		return this.network.getError();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final boolean canContinue() {
		return false;
	}

	/**
	 * Compute the derivative for target data.
	 * 
	 * @param input
	 *            The input.
	 * @param target
	 *            The target data.
	 * @return The output.
	 */
	public final MLData computeDeriv(final MLData input, 
			final MLData target) {
		int pop, ivar;
		final int ibest = 0;
		int outvar;
		double diff, dist, truedist;
		double vtot, wtot;
		double temp, der1, der2, psum;
		int vptr, wptr, vsptr = 0, wsptr = 0;

		final double[] out = new double[this.network.getOutputCount()];

		for (pop = 0; pop < this.network.getOutputCount(); pop++) {
			out[pop] = 0.0;
			for (ivar = 0; ivar < this.network.getInputCount(); ivar++) {
				this.v[pop * this.network.getInputCount() + ivar] = 0.0;
				this.w[pop * this.network.getInputCount() + ivar] = 0.0;
			}
		}

		psum = 0.0;

		if (this.network.getOutputMode() != PNNOutputMode.Classification) {
			vsptr = this.network.getOutputCount()
					* this.network.getInputCount();
			wsptr = this.network.getOutputCount()
					* this.network.getInputCount();
			for (ivar = 0; ivar < this.network.getInputCount(); ivar++) {
				this.v[vsptr + ivar] = 0.0;
				this.w[wsptr + ivar] = 0.0;
			}
		}

		final MLDataPair pair = BasicMLDataPair.createPair(this.network
				.getSamples().getInputSize(), this.network.getSamples()
				.getIdealSize());

		for (int r = 0; r < this.network.getSamples().getRecordCount(); r++) {

			this.network.getSamples().getRecord(r, pair);

			if (r == this.network.getExclude()) {
				continue;
			}

			dist = 0.0;
			for (ivar = 0; ivar < this.network.getInputCount(); ivar++) {
				diff = input.getData(ivar) - pair.getInput().getData(ivar);
				diff /= this.network.getSigma()[ivar];
				this.dsqr[ivar] = diff * diff;
				dist += this.dsqr[ivar];
			}

			if (this.network.getKernel() == PNNKernelType.Gaussian) {
				dist = Math.exp(-dist);
			} else if (this.network.getKernel() == PNNKernelType.Reciprocal) {
				dist = 1.0 / (1.0 + dist);
			}

			truedist = dist;
			if (dist < 1.e-40) {
				dist = 1.e-40;
			}

			if (this.network.getOutputMode() == PNNOutputMode.Classification) {
				pop = (int) pair.getIdeal().getData(0);
				out[pop] += dist;
				vptr = pop * this.network.getInputCount();
				wptr = pop * this.network.getInputCount();
				for (ivar = 0; ivar < this.network.getInputCount(); ivar++) {
					temp = truedist * this.dsqr[ivar];
					this.v[vptr + ivar] += temp;
					this.w[wptr + ivar] += temp * (2.0 * this.dsqr[ivar] - 3.0);
				}
			}

			else if (this.network.getOutputMode() == PNNOutputMode.Unsupervised) {
				for (ivar = 0; ivar < this.network.getInputCount(); ivar++) {
					out[ivar] += dist * pair.getInput().getData(ivar);
					temp = truedist * this.dsqr[ivar];
					this.v[vsptr + ivar] += temp;
					this.w[wsptr + ivar] += temp
							* (2.0 * this.dsqr[ivar] - 3.0);
				}
				vptr = 0;
				wptr = 0;
				for (outvar = 0; outvar < this.network.getOutputCount(); outvar++) {
					for (ivar = 0; ivar < this.network.getInputCount(); ivar++) {
						temp = truedist * this.dsqr[ivar]
								* pair.getInput().getData(ivar);
						this.v[vptr++] += temp;
						this.w[wptr++] += temp * (2.0 * this.dsqr[ivar] - 3.0);
					}
				}
				psum += dist;
			} else if (this.network.getOutputMode() == PNNOutputMode.Regression) {

				for (ivar = 0; ivar < this.network.getOutputCount(); ivar++) {
					out[ivar] += dist * pair.getIdeal().getData(ivar);
				}
				vptr = 0;
				wptr = 0;
				for (outvar = 0; outvar < this.network.getOutputCount(); outvar++) {
					for (ivar = 0; ivar < this.network.getInputCount(); ivar++) {
						temp = truedist * this.dsqr[ivar]
								* pair.getIdeal().getData(outvar);
						this.v[vptr++] += temp;
						this.w[wptr++] += temp * (2.0 * this.dsqr[ivar] - 3.0);
					}
				}
				for (ivar = 0; ivar < this.network.getInputCount(); ivar++) {
					temp = truedist * this.dsqr[ivar];
					this.v[vsptr + ivar] += temp;
					this.w[wsptr + ivar] += temp
							* (2.0 * this.dsqr[ivar] - 3.0);
				}
				psum += dist;
			}
		}

		if (this.network.getOutputMode() == PNNOutputMode.Classification) {
			psum = 0.0;
			for (pop = 0; pop < this.network.getOutputCount(); pop++) {
				if (this.network.getPriors()[pop] >= 0.0) {
					out[pop] *= this.network.getPriors()[pop]
							/ this.network.getCountPer()[pop];
				}
				psum += out[pop];
			}

			if (psum < 1.e-40) {
				psum = 1.e-40;
			}
		}

		for (pop = 0; pop < this.network.getOutputCount(); pop++) {
			out[pop] /= psum;
		}

		for (ivar = 0; ivar < this.network.getInputCount(); ivar++) {
			if (this.network.getOutputMode() == PNNOutputMode.Classification) {
				vtot = wtot = 0.0;
			} else {
				vtot = this.v[vsptr + ivar] * 2.0
						/ (psum * this.network.getSigma()[ivar]);
				wtot = this.w[wsptr + ivar]
						* 2.0
						/ (psum * this.network.getSigma()[ivar] * this.network
								.getSigma()[ivar]);
			}

			for (outvar = 0; outvar < this.network.getOutputCount(); outvar++) {
				if ((this.network.getOutputMode() == PNNOutputMode.Classification)
						&& (this.network.getPriors()[outvar] >= 0.0)) {
					this.v[outvar * this.network.getInputCount() + ivar] *= this.network
							.getPriors()[outvar]
							/ this.network.getCountPer()[outvar];
					this.w[outvar * this.network.getInputCount() + ivar] *= this.network
							.getPriors()[outvar]
							/ this.network.getCountPer()[outvar];
				}
				this.v[outvar * this.network.getInputCount() + ivar] *= 2.0 / (psum * this.network
						.getSigma()[ivar]);

				this.w[outvar * this.network.getInputCount() + ivar] *= 2.0 / (psum
						* this.network.getSigma()[ivar] * this.network
						.getSigma()[ivar]);
				if (this.network.getOutputMode() == PNNOutputMode.Classification) {

					vtot += this.v[outvar * this.network.getInputCount() + ivar];
					wtot += this.w[outvar * this.network.getInputCount() + ivar];

				}
			}

			for (outvar = 0; outvar < this.network.getOutputCount(); outvar++) {
				der1 = this.v[outvar * this.network.getInputCount() + ivar]
						- out[outvar] * vtot;
				der2 = this.w[outvar * this.network.getInputCount() + ivar]
						+ 2.0 * out[outvar] * vtot * vtot - 2.0
						* this.v[outvar * this.network.getInputCount() + ivar]
						* vtot - out[outvar] * wtot;
				if (this.network.getOutputMode() == PNNOutputMode.Classification) {

					if (outvar == (int) target.getData(0)) {
						temp = 2.0 * (out[outvar] - 1.0);
					} else {
						temp = 2.0 * out[outvar];
					}
				} else {
					temp = 2.0 * (out[outvar] - target.getData(outvar));
				}
				this.network.getDeriv()[ivar] += temp * der1;
				this.network.getDeriv2()[ivar] += temp * der2 + 2.0 * der1
						* der1;
			}
		}

		return new BasicMLData(out);
	}

	/**
	 * @return the maxError
	 */
	public final double getMaxError() {
		return this.maxError;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final MLMethod getMethod() {
		return this.network;
	}

	/**
	 * @return the minImprovement
	 */
	public final double getMinImprovement() {
		return this.minImprovement;
	}

	/**
	 * @return the numSigmas
	 */
	public final int getNumSigmas() {
		return this.numSigmas;
	}

	/**
	 * @return the sigmaHigh
	 */
	public final double getSigmaHigh() {
		return this.sigmaHigh;
	}

	/**
	 * @return the sigmaLow
	 */
	public final double getSigmaLow() {
		return this.sigmaLow;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final void iteration() {

		preIteration();
		
		if (!this.samplesLoaded) {
			this.network.setSamples(new BasicMLDataSet(this.training));
			this.samplesLoaded = true;
		}

		final GlobalMinimumSearch globalMinimum = new GlobalMinimumSearch();
		final DeriveMinimum dermin = new DeriveMinimum();

		int k;

		if (this.network.getOutputMode() == PNNOutputMode.Classification) {
			k = this.network.getOutputCount();
		} else {
			k = this.network.getOutputCount() + 1;

		}

		this.dsqr = new double[this.network.getInputCount()];
		this.v = new double[this.network.getInputCount() * k];
		this.w = new double[this.network.getInputCount() * k];

		final double[] x = new double[this.network.getInputCount()];
		final double[] base = new double[this.network.getInputCount()];
		final double[] direc = new double[this.network.getInputCount()];
		final double[] g = new double[this.network.getInputCount()];
		final double[] h = new double[this.network.getInputCount()];
		final double[] dwk2 = new double[this.network.getInputCount()];

		if (this.network.isTrained()) {
			k = 0;
			for (int i = 0; i < this.network.getInputCount(); i++) {
				x[i] = this.network.getSigma()[i];
			}
			globalMinimum.setY2(1.e30);
		} else {
			globalMinimum.findBestRange(this.sigmaLow, this.sigmaHigh,
					this.numSigmas, true, this.maxError, this);

			for (int i = 0; i < this.network.getInputCount(); i++) {
				x[i] = globalMinimum.getX2();
			}
		}

		final double d = dermin.calculate(32767, this.maxError, 1.e-8,
				this.minImprovement, this, this.network.getInputCount(), x,
				globalMinimum.getY2(), base, direc, g, h, dwk2);
		globalMinimum.setY2(d);

		for (int i = 0; i < this.network.getInputCount(); i++) {
			this.network.getSigma()[i] = x[i];
		}

		this.network.setError(Math.abs(globalMinimum.getY2()));
		this.network.setTrained(true); // Tell other routines net is trained
		this.setError(network.getError());
		
		postIteration();

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final TrainingContinuation pause() {
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void resume(final TrainingContinuation state) {
	}

	/**
	 * @param maxError
	 *            the maxError to set
	 */
	public final void setMaxError(final double maxError) {
		this.maxError = maxError;
	}

	/**
	 * @param minImprovement
	 *            the minImprovement to set
	 */
	public final void setMinImprovement(final double minImprovement) {
		this.minImprovement = minImprovement;
	}

	/**
	 * @param numSigmas
	 *            the numSigmas to set
	 */
	public final void setNumSigmas(final int numSigmas) {
		this.numSigmas = numSigmas;
	}

	/**
	 * @param sigmaHigh
	 *            the sigmaHigh to set
	 */
	public final void setSigmaHigh(final double sigmaHigh) {
		this.sigmaHigh = sigmaHigh;
	}

	/**
	 * @param sigmaLow
	 *            the sigmaLow to set
	 */
	public final void setSigmaLow(final double sigmaLow) {
		this.sigmaLow = sigmaLow;
	}

}
