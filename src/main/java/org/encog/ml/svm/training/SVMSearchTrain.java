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
package org.encog.ml.svm.training;

import org.encog.Encog;
import org.encog.EncogError;
import org.encog.ml.MLMethod;
import org.encog.ml.TrainingImplementationType;
import org.encog.ml.data.MLDataSet;
import org.encog.ml.svm.KernelType;
import org.encog.ml.svm.SVM;
import org.encog.ml.train.BasicTraining;
import org.encog.neural.networks.training.propagation.TrainingContinuation;

/**
 * Provides training for Support Vector Machine networks.
 */
public class SVMSearchTrain extends BasicTraining {

	/**
	 * The default starting number for C.
	 */
	public static final double DEFAULT_CONST_BEGIN = 1;

	/**
	 * The default ending number for C.
	 */
	public static final double DEFAULT_CONST_END = 15;

	/**
	 * The default step for C.
	 */
	public static final double DEFAULT_CONST_STEP = 2;

	/**
	 * The default gamma begin.
	 */
	public static final double DEFAULT_GAMMA_BEGIN = 1;

	/**
	 * The default gamma end.
	 */
	public static final double DEFAULT_GAMMA_END = 10;

	/**
	 * The default gamma step.
	 */
	public static final double DEFAULT_GAMMA_STEP = 1;

	/**
	 * The network that is to be trained.
	 */
	private final SVM network;

	/**
	 * The number of folds.
	 */
	private int fold = 0;

	/**
	 * The beginning value for C.
	 */
	private double constBegin = SVMSearchTrain.DEFAULT_CONST_BEGIN;

	/**
	 * The step value for C.
	 */
	private double constStep = SVMSearchTrain.DEFAULT_CONST_STEP;

	/**
	 * The ending value for C.
	 */
	private double constEnd = SVMSearchTrain.DEFAULT_CONST_END;

	/**
	 * The beginning value for gamma.
	 */
	private double gammaBegin = SVMSearchTrain.DEFAULT_GAMMA_BEGIN;

	/**
	 * The ending value for gamma.
	 */
	private double gammaEnd = SVMSearchTrain.DEFAULT_GAMMA_END;

	/**
	 * The step value for gamma.
	 */
	private double gammaStep = SVMSearchTrain.DEFAULT_GAMMA_STEP;

	/**
	 * The best values found for C.
	 */
	private double bestConst;

	/**
	 * The best values found for gamma.
	 */
	private double bestGamma;

	/**
	 * The best error.
	 */
	private double bestError;

	/**
	 * The current C.
	 */
	private double currentConst;

	/**
	 * The current gamma.
	 */
	private double currentGamma;

	/**
	 * Is the network setup.
	 */
	private boolean isSetup;

	/**
	 * Is the training done.
	 */
	private boolean trainingDone;

	/**
	 * The internal training object, used for the search.
	 */
	private final SVMTrain internalTrain;

	/**
	 * Construct a trainer for an SVM network.
	 * 
	 * @param method
	 *            The method to train.
	 * @param training
	 *            The training data for this network.
	 */
	public SVMSearchTrain(final SVM method, final MLDataSet training) {
		super(TrainingImplementationType.Iterative);
		this.network = method;
		setTraining(training);
		this.isSetup = false;
		this.trainingDone = false;

		this.internalTrain = new SVMTrain(network, training);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final boolean canContinue() {
		return false;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final void finishTraining() {
		this.internalTrain.setGamma(this.bestGamma);
		this.internalTrain.setC(this.bestConst);
		this.internalTrain.iteration();

	}

	/**
	 * @return the constBegin
	 */
	public final double getConstBegin() {
		return this.constBegin;
	}

	/**
	 * @return the constEnd
	 */
	public final double getConstEnd() {
		return this.constEnd;
	}

	/**
	 * @return the constStep
	 */
	public final double getConstStep() {
		return this.constStep;
	}

	/**
	 * @return the fold
	 */
	public final int getFold() {
		return this.fold;
	}

	/**
	 * @return the gammaBegin
	 */
	public final double getGammaBegin() {
		return this.gammaBegin;
	}

	/**
	 * @return the gammaEnd
	 */
	public final double getGammaEnd() {
		return this.gammaEnd;
	}

	/**
	 * @return the gammaStep
	 */
	public final double getGammaStep() {
		return this.gammaStep;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final MLMethod getMethod() {
		return this.network;
	}

	/**
	 * @return True if the training is done.
	 */
	@Override
	public final boolean isTrainingDone() {
		return this.trainingDone;
	}

	/**
	 * Perform one training iteration.
	 */
	@Override
	public final void iteration() {
		
		if (!this.trainingDone) {
			if (!this.isSetup) {
				setup();
			}

			preIteration();

			this.internalTrain.setFold(this.fold);

			if (this.network.getKernelType() == KernelType.RadialBasisFunction) {

				this.internalTrain.setGamma(this.currentGamma);
				this.internalTrain.setC(this.currentConst);
				
				double e = 0;
				
				this.internalTrain.iteration();
				e = this.internalTrain.getError();
				

				//System.out.println(this.currentGamma + "," + this.currentConst
				//		+ "," + e);

				// new best error?
				if (!Double.isNaN(e)) {
					if (e < this.bestError) {
						this.bestConst = this.currentConst;
						this.bestGamma = this.currentGamma;
						this.bestError = e;
					}
				}

				// advance
				this.currentConst += this.constStep;
				if (this.currentConst > this.constEnd) {
					this.currentConst = this.constBegin;
					this.currentGamma += this.gammaStep;
					if (this.currentGamma > this.gammaEnd) {
						this.trainingDone = true;
					}

				}

				setError(this.bestError);
			} else {
				this.internalTrain.setGamma(this.currentGamma);
				this.internalTrain.setC(this.currentConst);
				this.internalTrain.iteration();
			}

			postIteration();
		}
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
	 * @param theConstBegin
	 *            the constBegin to set
	 */
	public final void setConstBegin(final double theConstBegin) {
		this.constBegin = theConstBegin;
	}

	/**
	 * @param theConstEnd
	 *            the constEnd to set
	 */
	public final void setConstEnd(final double theConstEnd) {
		this.constEnd = theConstEnd;
	}

	/**
	 * @param theConstStep
	 *            the constStep to set
	 */
	public final void setConstStep(final double theConstStep) {
		this.constStep = theConstStep;
	}

	/**
	 * @param theFold
	 *            the fold to set
	 */
	public final void setFold(final int theFold) {
		this.fold = theFold;
	}

	/**
	 * @param theGammaBegin
	 *            the gammaBegin to set
	 */
	public final void setGammaBegin(final double theGammaBegin) {
		this.gammaBegin = theGammaBegin;
	}

	/**
	 * @param theGammaEnd
	 *            the gammaEnd to set.
	 */
	public final void setGammaEnd(final double theGammaEnd) {
		this.gammaEnd = theGammaEnd;
	}

	/**
	 * @param theGammaStep
	 *            the gammaStep to set
	 */
	public final void setGammaStep(final double theGammaStep) {
		this.gammaStep = theGammaStep;
	}

	/**
	 * Setup to train the SVM.
	 */
	private void setup() {

		this.currentConst = this.constBegin;
		this.currentGamma = this.gammaBegin;
		this.bestError = Double.POSITIVE_INFINITY;
		this.isSetup = true;
		
		if( this.currentGamma<=0 || this.currentGamma<Encog.DEFAULT_DOUBLE_EQUAL ) {
			throw new EncogError("SVM search training cannot use a gamma value less than zero.");
		}
		
		if( this.currentConst<=0 || this.currentConst<Encog.DEFAULT_DOUBLE_EQUAL ) {
			throw new EncogError("SVM search training cannot use a const value less than zero.");
		}
		
		if( this.gammaStep<0 ) {
			throw new EncogError("SVM search gamma step cannot use a const value less than zero.");
		}
		
		if( this.constStep<0 ) {
			throw new EncogError("SVM search const step cannot use a const value less than zero.");
		}
	}

}
