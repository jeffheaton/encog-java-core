/*
 * Encog(tm) Core v2.5 - Java Version
 * http://www.heatonresearch.com/encog/
 * http://code.google.com/p/encog-java/
 
 * Copyright 2008-2010 Heaton Research, Inc.
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

package org.encog.neural.networks.training.svm;

import org.encog.Encog;
import org.encog.engine.util.ErrorCalculation;
import org.encog.mathutil.libsvm.svm;
import org.encog.mathutil.libsvm.svm_parameter;
import org.encog.mathutil.libsvm.svm_problem;
import org.encog.neural.data.NeuralDataSet;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.svm.KernelType;
import org.encog.neural.networks.svm.SVMNetwork;
import org.encog.neural.networks.training.BasicTraining;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Provides training for Support Vector Machine networks.
 */
public class SVMTrain extends BasicTraining {

	/**
	 * The logger.
	 */
	private static final transient Logger LOGGER = LoggerFactory
			.getLogger(SVMTrain.class);

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
	private SVMNetwork network;
	
	/**
	 * The problem to train for.
	 */
	private svm_problem[] problem;

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
	private double[] bestConst;
	
	/**
	 * The best values found for gamma.
	 */
	private double[] bestGamma;
	
	/**
	 * The best error.
	 */
	private double[] bestError;
	
	/**
	 * The current C.
	 */
	private double[] currentConst;
	
	/**
	 * The current gamma.
	 */
	private double[] currentGamma;
	
	/**
	 * Is the network setup.
	 */
	private boolean isSetup;
	
	/**
	 * Is the training done.
	 */
	private boolean trainingDone;

	/**
	 * Construct a trainer for an SVM network.
	 * @param network The network to train.
	 * @param training The training data for this network.
	 */
	public SVMTrain(BasicNetwork network, NeuralDataSet training) {
		this.network = (SVMNetwork) network;
		this.setTraining(training);
		this.isSetup = false;
		this.trainingDone = false;

		for (int i = 0; i < this.network.getOutputCount(); i++) {
			this.problem = new svm_problem[this.network.getOutputCount()];
			this.problem[i] = EncodeSVMProblem.encode(training, i);
		}
	}

	/**
	 * Quickly train all outputs with a C of 1.0 and a gamma equal to 1/(num inputs).
	 */
	public void train() {
		double gamma = 1.0 / this.network.getInputCount();
		double c = 1.0;

		for (int i = 0; i < network.getOutputCount(); i++)
			train(i, gamma, c);
	}

	/**
	 * Quickly train one output with the specified gamma and C.
	 * @param index The output to train.
	 * @param gamma The gamma to train with.
	 * @param c The C to train with.
	 */
	public void train(int index, double gamma, double c) {
		network.getParams()[index].C = c;
		
		if( gamma>Encog.DEFAULT_DOUBLE_EQUAL )
		{
			network.getParams()[index].gamma = 1.0 / this.network.getInputCount();
		}
		else
		{
			network.getParams()[index].gamma = gamma;
		}
		
		network.getModels()[index] = svm.svm_train(problem[index], network
				.getParams()[index]);
	}

	/**
	 * Cross validate and check the specified index/gamma.
	 * @param index The output index to cross validate.
	 * @param gamma The gamma to check.
	 * @param c The C to check.
	 * @return The calculated error.
	 */
	public double crossValidate(int index, double gamma, double c) {

		double[] target = new double[this.problem[0].l];

		network.getParams()[index].C = c;
		network.getParams()[index].gamma = gamma;
		svm.svm_cross_validation(problem[index], network.getParams()[index], fold,
				target);
		return evaluate(network.getParams()[index], problem[index], target);
	}

	/**
	 * Evaluate the error for the specified model.
	 * @param param The params for the SVN.
	 * @param prob The problem to evaluate.
	 * @param target The output values from the SVN.
	 * @return The calculated error.
	 */
	private double evaluate(svm_parameter param, svm_problem prob,
			double[] target) {
		int total_correct = 0;

		ErrorCalculation error = new ErrorCalculation();

		if (param.svm_type == svm_parameter.EPSILON_SVR
				|| param.svm_type == svm_parameter.NU_SVR) {
			for (int i = 0; i < prob.l; i++) {
				double ideal = prob.y[i];
				double actual = target[i];
				error.updateError(actual, ideal);
			}
			return error.calculate();
		} else {
			for (int i = 0; i < prob.l; i++)
				if (target[i] == prob.y[i])
					++total_correct;

			return 100.0 * total_correct / prob.l;
		}
	}

	/**
	 * Setup to train the SVM.
	 */
	private void setup() {
		this.currentConst = new double[this.network.getOutputCount()];
		this.currentGamma = new double[this.network.getOutputCount()];
		this.bestConst = new double[this.network.getOutputCount()];
		this.bestGamma = new double[this.network.getOutputCount()];
		this.bestError = new double[this.network.getOutputCount()];


		for (int i = 0; i < this.network.getOutputCount(); i++) {
			this.currentConst[i] = this.constBegin;
			this.currentGamma[i] = this.gammaBegin;
			this.bestError[i] = Double.POSITIVE_INFINITY;
		}
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
				
				for (int i = 0; i < this.network.getOutputCount(); i++) {
					double e = this.crossValidate(i, this.currentGamma[i],
							currentConst[i]);

					if (e < bestError[i]) {
						this.bestConst[i] = this.currentConst[i];
						this.bestGamma[i] = this.currentGamma[i];
						this.bestError[i] = e;
					}

					this.currentConst[i] += this.constStep;
					if (this.currentConst[i] > this.constEnd) {
						this.currentConst[i] = this.constBegin;
						this.currentGamma[i] += this.gammaStep;
						if (this.currentGamma[i] > this.gammaEnd)
							this.trainingDone = true;
					}
					
					totalError += this.bestError[i];
				}

				setError(totalError/this.network.getOutputCount());
			} else {
				train();
			}

			postIteration();
		}
	}

	/**
	 * @return The problem being trained.
	 */
	public svm_problem[] getProblem() {
		return problem;
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
		for (int i = 0; i < network.getOutputCount(); i++) {
			train(i, this.bestGamma[i], this.bestConst[i]);
		}
	}

	/**
	 * @return The trained network.
	 */
	@Override
	public BasicNetwork getNetwork() {
		return this.network;
	}

	/**
	 * @return True if the training is done.
	 */
	public boolean isTrainingDone() {
		return this.trainingDone;
	}

	/**
	 * Quickly train the network with a fixed gamma and C.
	 * @param gamma The gamma to use.
	 * @param c The C to use.
	 */
	public void train(double gamma, double c) {
		for(int i=0;i<this.network.getOutputCount();i++)
		{
			train(i,gamma,c);
		}
		
	}

}
