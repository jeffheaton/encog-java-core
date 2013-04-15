/**
 *
 */
package org.encog.ensemble;

import org.encog.ensemble.data.EnsembleDataSet;
import org.encog.ml.MLClassification;
import org.encog.ml.MLMethod;
import org.encog.ml.MLRegression;
import org.encog.ml.train.MLTrain;

/**
 * @author nitbix
 *
 */
public interface EnsembleML extends MLMethod, MLClassification, MLRegression {

	/**
	 * Set the dataset for this member
	 * @param dataSet The data set.
	 */
	public void setTrainingSet(EnsembleDataSet dataSet);

	/**
	 * Set the training for this member
	 * @param train The trainer.
	 */
	public void setTraining(MLTrain train);

	/**
	 * @return Get the dataset for this member
	 */
	public EnsembleDataSet getTrainingSet();

	/**
	 * @return Get the dataset for this member.
	 */
	public MLTrain getTraining();

	/**
	 * Train the ML to a certain accuracy.
	 * @param targetError The target error.
	 */
	public void train(double targetError);

	/**
	 * Train the ML to a certain accuracy.
	 * @param targetError Target error.
	 * @param verbose Verbose mode.
	 */
	public void train(double targetError, boolean verbose);

	/**
	 * Get the error for this ML on the dataset
	 */
	public double getError(EnsembleDataSet testset);

	/**
	 * Set the MLMethod to run
	 * @param newMl The new ML.
	 */
	public void setMl(MLMethod newMl);

	/**
	 * @return Returns the current MLMethod
	 */
	public MLMethod getMl();

	public void trainStep();

	public String getLabel();
}
