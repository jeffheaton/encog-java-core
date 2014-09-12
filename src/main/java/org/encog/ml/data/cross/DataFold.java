package org.encog.ml.data.cross;

import org.encog.ml.MLMethod;
import org.encog.ml.data.versatile.MatrixMLDataSet;

public class DataFold {
	private final MatrixMLDataSet training;
	private final MatrixMLDataSet validation;
	private double score;
	private MLMethod method;
	
	public DataFold(MatrixMLDataSet theTraining,MatrixMLDataSet theValidation) {
		this.training = theTraining;
		this.validation = theValidation;
	}

	/**
	 * @return the score
	 */
	public double getScore() {
		return score;
	}

	/**
	 * @param score the score to set
	 */
	public void setScore(double score) {
		this.score = score;
	}

	/**
	 * @return the training
	 */
	public MatrixMLDataSet getTraining() {
		return training;
	}

	/**
	 * @return the validation
	 */
	public MatrixMLDataSet getValidation() {
		return validation;
	}

	/**
	 * @return the method
	 */
	public MLMethod getMethod() {
		return method;
	}

	/**
	 * @param method the method to set
	 */
	public void setMethod(MLMethod method) {
		this.method = method;
	}
	
	
	
}
