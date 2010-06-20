package org.encog.neural.networks.training.svm;

import java.util.List;

import org.encog.Encog;
import org.encog.cloud.EncogCloud;
import org.encog.mathutil.libsvm.svm;
import org.encog.mathutil.libsvm.svm_parameter;
import org.encog.mathutil.libsvm.svm_problem;
import org.encog.neural.data.Indexable;
import org.encog.neural.data.NeuralDataSet;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.svm.KernelType;
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
	private double[] bestConst;
	private double[] bestGamma;
	private double[] bestError;
	private double[] currentConst;
	private double[] currentGamma;
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

	public double crossValidate(int index, double gamma, double c) {

		double[] target = new double[this.problem[0].l];

		network.getParams()[index].C = c;
		network.getParams()[index].gamma = gamma / this.network.getInputCount();
		svm.svm_cross_validation(problem[index], network.getParams()[index], fold,
				target);
		return evaluate(network.getParams()[index], problem[index], target);
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
			train(i, this.bestGamma[i], this.bestConst[i]);
		}
	}

	@Override
	public BasicNetwork getNetwork() {
		return this.network;
	}

	public boolean isTrainingDone() {
		return this.trainingDone;
	}

	public void train(double gamma, double c) {
		for(int i=0;i<this.network.getOutputCount();i++)
		{
			train(i,gamma,c);
		}
		
	}

}
