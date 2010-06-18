package org.encog.neural.networks.training.svm;

import java.util.List;

import org.encog.cloud.EncogCloud;
import org.encog.mathutil.libsvm.svm;
import org.encog.mathutil.libsvm.svm_parameter;
import org.encog.mathutil.libsvm.svm_problem;
import org.encog.neural.data.Indexable;
import org.encog.neural.data.NeuralDataSet;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.svm.SVMNetwork;
import org.encog.neural.networks.training.BasicTraining;
import org.encog.neural.networks.training.Strategy;
import org.encog.neural.networks.training.Train;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.encog.mathutil.error.ErrorCalculation;

public class SVMTrain extends BasicTraining {

	private static final transient Logger LOGGER = LoggerFactory
			.getLogger(SVMTrain.class);

	private SVMNetwork network;
	private svm_problem[] problem;

	private int fold = 5;
	private double constBegin = -5;
	private double constStep = 2;
	private double constEnd = 15;
	private double gammaBegin = -10;
	private double gammaEnd = 10;
	private double gammaStep = 1;
	private double bestConst;
	private double bestGamma;
	private double bestError;
	private double currentConst;
	private double currentGamma;
	private boolean isSetup;
	private boolean trainingDone;

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

	public void train() {
		double gamma = 1.0 / this.network.getInputCount();
		double c = 1.0;

		for (int i = 0; i < network.getOutputCount(); i++)
			train(i, gamma, c);
	}

	public void train(int index, double gamma, double c) {
		network.getParams()[index].C = c;
		network.getParams()[index].gamma = gamma;

		network.getModels()[index] = svm.svm_train(problem[index], network
				.getParams()[index]);
	}

	public double[] crossValidate(double gamma, double c) {

		double[] result = new double[network.getModels().length];

		for (int i = 0; i < network.getModels().length; i++) {
			double[] target = new double[this.problem[0].l];

			network.getParams()[i].C = c;
			network.getParams()[i].gamma = gamma / this.network.getInputCount();
			svm.svm_cross_validation(problem[i], network.getParams()[i], fold,
					target);
			result[i] = evaluate(network.getParams()[i], problem[i], target);
		}

		return result;
	}

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

	private void setup() {
		bestError = Double.POSITIVE_INFINITY;
		this.currentConst = this.constBegin;
		this.currentGamma = this.gammaBegin;
		this.isSetup = true;
	}

	public void iteration() {

		if (!trainingDone) {
			if (!isSetup)
				setup();

			preIteration();

			double[] e = this.crossValidate(this.currentGamma, currentConst);

			if (e[0] < bestError) {
				this.bestConst = this.currentConst;
				this.bestGamma = this.currentGamma;
				this.bestError = e[0];
			}

			this.currentConst += this.constStep;
			if (this.currentConst > this.constEnd) {
				this.currentConst = this.constBegin;
				this.currentGamma += this.gammaStep;
				if (this.currentGamma > this.gammaEnd)
					this.trainingDone = true;
			}

			setError(this.bestError);
			
			postIteration();
		}
	}

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

	public void finishTraining() {
		for (int i = 0; i < network.getOutputCount(); i++) {
			train(i, this.bestGamma, this.bestConst);
		}
	}

	@Override
	public BasicNetwork getNetwork() {
		return this.network;
	}
	
	public boolean isTrainingDone()
	{
		return this.trainingDone;
	}

}
