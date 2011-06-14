package org.encog.ml.svm.training.search;

import org.encog.mathutil.libsvm.svm_problem;
import org.encog.ml.svm.SVM;

public class SVMJobPackage {
	private final SVM svm;
	private final svm_problem problem;
	private final double c;
	private final double gamma;
	private int fold;
		
	public SVMJobPackage(SVM svm, svm_problem problem, double c, double gamma,
			int fold) {
		super();
		this.svm = svm;
		this.problem = problem;
		this.c = c;
		this.gamma = gamma;
		this.fold = fold;
	}

	/**
	 * @return the fold
	 */
	public final int getFold() {
		return fold;
	}


	/**
	 * @param fold the fold to set
	 */
	public final void setFold(int fold) {
		this.fold = fold;
	}


	/**
	 * @return the svm
	 */
	public final SVM getSvm() {
		return svm;
	}


	/**
	 * @return the problem
	 */
	public final svm_problem getProblem() {
		return problem;
	}


	/**
	 * @return the c
	 */
	public final double getC() {
		return c;
	}


	/**
	 * @return the gamma
	 */
	public final double getGamma() {
		return gamma;
	}	
}
