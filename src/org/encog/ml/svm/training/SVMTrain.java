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

import org.encog.Encog;
import org.encog.engine.util.ErrorCalculation;
import org.encog.mathutil.libsvm.svm;
import org.encog.mathutil.libsvm.svm_parameter;
import org.encog.mathutil.libsvm.svm_problem;
import org.encog.ml.MLMethod;
import org.encog.ml.MLRegression;
import org.encog.ml.TrainingImplementationType;
import org.encog.ml.svm.KernelType;
import org.encog.ml.svm.SVM;
import org.encog.neural.data.NeuralDataSet;
import org.encog.neural.networks.training.BasicTraining;
import org.encog.neural.networks.training.propagation.TrainingContinuation;
import org.encog.util.simple.EncogUtility;

/**
 * Provides training for Support Vector Machine networks.
 */
public class SVMTrain extends BasicTraining {
	
	/**
	 * The network that is to be trained.
	 */
	private SVM network;
	
	/**
	 * The problem to train for.
	 */
	private svm_problem problem;

	/**
	 * The number of folds.
	 */
	private int fold = 5;
			
	/**
	 * Is the training done.
	 */
	private boolean trainingDone;
	
	private double gamma;
	private double c;

	/**
	 * Construct a trainer for an SVM network.
	 * @param network The network to train.
	 * @param training The training data for this network.
	 */
	public SVMTrain(SVM network, NeuralDataSet training) {
		super(TrainingImplementationType.OnePass);
		this.network = (SVM) network;
		this.setTraining(training);
		this.trainingDone = false;

		this.problem = new svm_problem();
		
		this.problem = EncodeSVMProblem.encode(training, 0);
		this.gamma = 1.0 / this.network.getInputCount();
		this.c = 1.0;
	}

	/**
	 * Quickly train all outputs with a C of 1.0 and a gamma equal to 1/(num inputs).
	 */
	public void iteration() {

		network.getParams().C = c;
		network.getParams().gamma = gamma;
		
		network.setModel( svm.svm_train(problem, network
				.getParams()) );
		
		this.setError(this.network.calculateError(this.getTraining()));
		this.trainingDone = true;
	}
	

	/**
	 * Cross validate and check the specified index/gamma.
	 * @param index The output index to cross validate.
	 * @param gamma The gamma to check.
	 * @param c The C to check.
	 * @return The calculated error.
	 */
	public double crossValidate(double gamma, double c) {

		double[] target = new double[this.problem.l];

		network.getParams().C = c;
		network.getParams().gamma = gamma;
		svm.svm_cross_validation(problem, network.getParams(), fold,
				target);
		return evaluate(network.getParams(), problem, target);
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
	 * @return The problem being trained.
	 */
	public svm_problem getProblem() {
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

	public double getGamma() {
		return gamma;
	}

	public void setGamma(double gamma) {
		this.gamma = gamma;
	}

	public double getC() {
		return c;
	}

	public void setC(double c) {
		this.c = c;
	}
	
	@Override
	public boolean canContinue() {
		return false;
	}

	@Override
	public TrainingContinuation pause() {
		return null;
	}

	@Override
	public void resume(TrainingContinuation state) {
		
	}


}
