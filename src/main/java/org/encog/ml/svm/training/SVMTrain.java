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
import org.encog.mathutil.error.ErrorCalculation;
import org.encog.mathutil.libsvm.svm;
import org.encog.mathutil.libsvm.svm_parameter;
import org.encog.mathutil.libsvm.svm_problem;
import org.encog.ml.MLMethod;
import org.encog.ml.TrainingImplementationType;
import org.encog.ml.data.MLDataSet;
import org.encog.ml.svm.SVM;
import org.encog.ml.train.BasicTraining;
import org.encog.neural.networks.training.propagation.TrainingContinuation;
import org.encog.util.Format;
import org.encog.util.logging.EncogLogging;

/**
 * Provides training for Support Vector Machine networks.
 */
public class SVMTrain extends BasicTraining {


	/**
	 * The network that is to be trained.
	 */
	private final SVM network;

	/**
	 * The problem to train for.
	 */
	private svm_problem problem;

	/**
	 * The number of folds.
	 */
	private int fold = 0;

	/**
	 * Is the training done.
	 */
	private boolean trainingDone;

	/**
	 * The gamma value.
	 */
	private double gamma;

	/**
	 * The const c value.
	 */
	private double c;

	/**
	 * Construct a trainer for an SVM network.
	 * 
	 * @param method
	 *            The network to train.
	 * @param dataSet
	 *            The training data for this network.
	 */
	public SVMTrain(final SVM method, final MLDataSet dataSet) {
		super(TrainingImplementationType.OnePass);
		this.network = method;
		setTraining(dataSet);
		this.trainingDone = false;

		this.problem = EncodeSVMProblem.encode(dataSet, 0);
		this.gamma = 1.0 / this.network.getInputCount();
		this.c = 1.0;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final boolean canContinue() {
		return false;
	}

	/**
	 * Evaluate the error for the specified model.
	 * 
	 * @param param
	 *            The params for the SVN.
	 * @param prob
	 *            The problem to evaluate.
	 * @param target
	 *            The output values from the SVN.
	 * @return The calculated error.
	 */
	private double evaluate(final svm_parameter param, final svm_problem prob,
			final double[] target) {
		int totalCorrect = 0;

		final ErrorCalculation error = new ErrorCalculation();

		if ((param.svm_type == svm_parameter.EPSILON_SVR)
				|| (param.svm_type == svm_parameter.NU_SVR)) {
			for (int i = 0; i < prob.l; i++) {
				final double ideal = prob.y[i];
				final double actual = target[i];
				error.updateError(actual, ideal);
			}
			return error.calculate();
		} else {
			for (int i = 0; i < prob.l; i++) {
				if (target[i] == prob.y[i]) {
					++totalCorrect;
				}
			}

			return Format.HUNDRED_PERCENT * totalCorrect / prob.l;
		}
	}

	/**
	 * @return The constant C.
	 */
	public final double getC() {
		return this.c;
	}

	/**
	 * @return the fold
	 */
	public final int getFold() {
		return this.fold;
	}

	/**
	 * @return The gamma.
	 */
	public final double getGamma() {
		return this.gamma;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final MLMethod getMethod() {
		return this.network;
	}

	/**
	 * @return The problem being trained.
	 */
	public final svm_problem getProblem() {
		return this.problem;
	}

	/**
	 * @return True if the training is done.
	 */
	@Override
	public final boolean isTrainingDone() {
		return this.trainingDone;
	}

	/**
	 * Perform either a train or a cross validation.  If the folds property is 
	 * greater than 1 then cross validation will be done.  Cross validation does 
	 * not produce a usable model, but it does set the error. 
	 * 
	 * If you are cross validating try C and Gamma values until you have a good 
	 * error rate.  Then use those values to train, producing the final model.
	 */
	@Override
	public final void iteration() {

		this.network.getParams().C = this.c;
		this.network.getParams().gamma = this.gamma;
		EncogLogging.log(EncogLogging.LEVEL_INFO, "Training with parameters C = " + c + ", gamma = " + gamma);

		if (this.fold > 1) {
			// cross validate
			final double[] target = new double[this.problem.l];

			svm.svm_cross_validation(this.problem, this.network.getParams(),
					this.fold, target);
			this.network.setModel(null);

			setError(evaluate(this.network.getParams(), this.problem, target));
		} else {
			// train
			this.network.setModel(svm.svm_train(this.problem,
					this.network.getParams()));

			setError(this.network.calculateError(getTraining()));
		}

		this.trainingDone = true;
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
	 * Set the constant C.
	 * 
	 * @param theC
	 *            The constant C.
	 */
	public final void setC(final double theC) {
		this.c = theC;
		
		if( this.c<=0 || this.c<Encog.DEFAULT_DOUBLE_EQUAL ) {
			throw new EncogError("SVM training cannot use a c value less than zero.");
		}
		
	}

	/**
	 * Set the number of folds.
	 * 
	 * @param theFold
	 *            the fold to set.
	 */
	public final void setFold(final int theFold) {
		this.fold = theFold;
	}

	/**
	 * Set the gamma.
	 * @param theGamma The new gamma.
	 */
	public final void setGamma(final double theGamma) {
		this.gamma = theGamma;
		
		if( this.gamma<=0 || this.gamma<Encog.DEFAULT_DOUBLE_EQUAL ) {
			throw new EncogError("SVM training cannot use a gamma value less than zero.");
		}
		
	}

}
