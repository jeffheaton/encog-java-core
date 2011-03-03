/*
 * Encog(tm) Core v3.0 - Java Version
 * http://www.heatonresearch.com/encog/
 * http://code.google.com/p/encog-java/
 
 * Copyright 2008-2011 Heaton Research, Inc.
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

import org.encog.ml.MLMethod;
import org.encog.ml.TrainingImplementationType;
import org.encog.ml.svm.KernelType;
import org.encog.ml.svm.SVM;
import org.encog.neural.data.NeuralDataSet;
import org.encog.neural.networks.training.BasicTraining;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Provides training for Support Vector Machine networks.
 */
public class SVMSearchTrain extends BasicTraining {

	/**
	 * The logger.
	 */
	private static final transient Logger LOGGER = LoggerFactory
			.getLogger(SVMSearchTrain.class);

	/**
	 * The default starting number for C.
	 */
	public static final double DEFAULT_CONST_BEGIN = -5;
	
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
	public static final double DEFAULT_GAMMA_BEGIN = -10;
	
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
	private SVM network;
	
	/**
	 * The number of folds.
	 */
	private int fold = 5;
	
	/**
	 * The beginning value for C.
	 */
	private double constBegin = DEFAULT_CONST_BEGIN;
	
	/**
	 * The step value for C.
	 */
	private double constStep = DEFAULT_CONST_END;
	
	/**
	 * The ending value for C.
	 */
	private double constEnd = DEFAULT_CONST_STEP;
	
	/**
	 * The beginning value for gamma.
	 */
	private double gammaBegin = DEFAULT_GAMMA_BEGIN;
	
	/**
	 * The ending value for gamma.
	 */
	private double gammaEnd = DEFAULT_GAMMA_END;
	
	/**
	 * The step value for gamma.
	 */
	private double gammaStep = DEFAULT_GAMMA_STEP;
	
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
	
	private SVMTrain internalTrain;

	/**
	 * Construct a trainer for an SVM network.
	 * @param network The network to train.
	 * @param training The training data for this network.
	 */
	public SVMSearchTrain(SVM network, NeuralDataSet training) {
		super(TrainingImplementationType.Iterative);
		this.network = (SVM) network;
		this.setTraining(training);
		this.isSetup = false;
		this.trainingDone = false;
		
		this.internalTrain = new SVMTrain(network,training);
	}


	/**
	 * Setup to train the SVM.
	 */
	private void setup() {

			this.currentConst = this.constBegin;
			this.currentGamma = this.gammaBegin;
			this.bestError = Double.POSITIVE_INFINITY;
		this.isSetup = true;
	}

	/**
	 * Perform one training iteration.
	 */
	public void iteration() {

		if (!trainingDone) {
			if (!isSetup)
				setup();

			preIteration();

			if (network.getKernelType() == KernelType.RadialBasisFunction) {

				double totalError = 0;
				
				
					double e = this.internalTrain.crossValidate(this.currentGamma,
							currentConst);

					if (e < bestError) {
						this.bestConst = this.currentConst;
						this.bestGamma = this.currentGamma;
						this.bestError = e;
					}

					this.currentConst += this.constStep;
					if (this.currentConst > this.constEnd) {
						this.currentConst = this.constBegin;
						this.currentGamma += this.gammaStep;
						if (this.currentGamma > this.gammaEnd)
							this.trainingDone = true;
					}
					
					totalError += this.bestError;
				

				setError(totalError/this.network.getOutputCount());
			} else {
				this.internalTrain.setGamma(this.currentGamma);
				this.internalTrain.setC(this.currentConst);
				this.internalTrain.iteration();
			}

			postIteration();
		}
	}

	/**
	 * @return the fold
	 */
	public int getFold() {
		return fold;
	}

	/**
	 * @param fold
	 *            the fold to set
	 */
	public void setFold(int fold) {
		this.fold = fold;
	}

	/**
	 * @return the constBegin
	 */
	public double getConstBegin() {
		return constBegin;
	}

	/**
	 * @param constBegin
	 *            the constBegin to set
	 */
	public void setConstBegin(double constBegin) {
		this.constBegin = constBegin;
	}

	/**
	 * @return the constStep
	 */
	public double getConstStep() {
		return constStep;
	}

	/**
	 * @param constStep
	 *            the constStep to set
	 */
	public void setConstStep(double constStep) {
		this.constStep = constStep;
	}

	/**
	 * @return the constEnd
	 */
	public double getConstEnd() {
		return constEnd;
	}

	/**
	 * @param constEnd
	 *            the constEnd to set
	 */
	public void setConstEnd(double constEnd) {
		this.constEnd = constEnd;
	}

	/**
	 * @return the gammaBegin
	 */
	public double getGammaBegin() {
		return gammaBegin;
	}

	/**
	 * @param gammaBegin
	 *            the gammaBegin to set
	 */
	public void setGammaBegin(double gammaBegin) {
		this.gammaBegin = gammaBegin;
	}

	/**
	 * @return the gammaEnd
	 */
	public double getGammaEnd() {
		return gammaEnd;
	}

	/**
	 * @param gammaEnd
	 *            the gammaEnd to set
	 */
	public void setGammaEnd(double gammaEnd) {
		this.gammaEnd = gammaEnd;
	}

	/**
	 * @return the gammaStep
	 */
	public double getGammaStep() {
		return gammaStep;
	}

	/**
	 * @param gammaStep
	 *            the gammaStep to set
	 */
	public void setGammaStep(double gammaStep) {
		this.gammaStep = gammaStep;
	}

	/**
	 * Called to finish training.
	 */
	public void finishTraining() {
		this.internalTrain.setGamma(this.bestGamma);
		this.internalTrain.setC(this.bestConst);
		this.internalTrain.iteration();
		
	}

	/**
	 * @return The trained network.
	 */
	@Override
	public MLMethod getNetwork() {
		return this.network;
	}

	/**
	 * @return True if the training is done.
	 */
	public boolean isTrainingDone() {
		return this.trainingDone;
	}

}
