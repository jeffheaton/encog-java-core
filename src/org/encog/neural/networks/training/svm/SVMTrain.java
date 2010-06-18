package org.encog.neural.networks.training.svm;

import org.encog.mathutil.libsvm.svm;
import org.encog.mathutil.libsvm.svm_parameter;
import org.encog.mathutil.libsvm.svm_problem;
import org.encog.neural.data.Indexable;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.svm.EncodeSVMProblem;
import org.encog.neural.networks.svm.SVMNetwork;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SVMTrain {

	private static final transient Logger LOGGER = LoggerFactory
			.getLogger(SVMTrain.class);

	private SVMNetwork network;
	private Indexable indexable;
	private svm_problem[] problem;
	
	private int fold = 5;
	private double cBegin = -5;
	private double cStep = 2;
	private double cEnd = 15;
	private double gammaBegin = 3;
	private double gammaEnd = -15;
	private double gammaStep = -2;
	
	public SVMTrain(BasicNetwork network, Indexable indexable) {
		this.network = (SVMNetwork) network;
		this.indexable = indexable;

		for (int i = 0; i < this.network.getOutputCount(); i++) {
			this.problem = new svm_problem[this.network.getOutputCount()];
			this.problem[i] = EncodeSVMProblem.encode(indexable, i);
		}
	}
	
	public void train()
	{
		double gamma = 1.0/this.network.getInputCount();
		double c = 1.0;
		train(gamma,c);
	}
	
	public void train(double gamma, double c)
	{
		network.getParams().C = c;
		network.getParams().gamma = gamma;
	
		for (int i = 0; i < network.getOutputCount(); i++) {
			network.getModels()[i] = svm.svm_train(problem[i], network
					.getParams());	
		}
	}

	public double[] crossValidate(double gamma, double c) {

		double[] result = new double[network.getModels().length];
		
		for (int i = 0; i < network.getModels().length; i++) {
			double[] target = new double[this.problem[0].l];

			network.getParams().C = c;
			network.getParams().gamma = gamma;
			svm.svm_cross_validation(problem[i], network.getParams(), fold,
					target);
			result[i] = evaluate(network.getParams(),problem[i],target);
		}
		
		return result;
	}

	private double evaluate(svm_parameter param, svm_problem prob,
			double[] target) {
		int total_correct = 0;
		double total_error = 0;

		if (param.svm_type == svm_parameter.EPSILON_SVR
				|| param.svm_type == svm_parameter.NU_SVR) {
			for (int i = 0; i < prob.l; i++) {
				double y = prob.y[i];
				double v = target[i];
				total_error += (v - y) * (v - y);
			}
			return total_error / prob.l;
		} else {
			for (int i = 0; i < prob.l; i++)
				if (target[i] == prob.y[i])
					++total_correct;

			return 100.0 * total_correct / prob.l;
		}
	}

	public void iteration() {
		
		double gamma = this.gammaBegin;
		double c = this.cBegin;
		
		double[] e = this.crossValidate(gamma, c);
		System.out.println(e[0]);
		
		/*
		for (int i = 0; i < network.getOutputCount(); i++) {
			network.getModels()[i] = svm.svm_train(problem[i], network
					.getParams());
		}*/
	}

	public SVMNetwork getNetwork() {
		return network;
	}

	public Indexable getIndexable() {
		return indexable;
	}

	public svm_problem[] getProblem() {
		return problem;
	}

}
