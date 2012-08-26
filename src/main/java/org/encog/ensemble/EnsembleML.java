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
	 * @param dataSet
	 */
	public void setTrainingSet(EnsembleDataSet dataSet);

	/**
	 * Set the training for this member
	 * @param dataSet
	 */
	public void setTraining(MLTrain train);

	/**
	 * Get the dataset for this member
	 * @return
	 */
	public EnsembleDataSet getTrainingSet();

	/**
	 * Get the dataset for this member
	 * @return
	 */
	public MLTrain getTraining();

	/**
	 * Train the ML to a certain accuracy
	 * @param train
	 * @param targetAccuracy
	 */
	public void train(double targetError);
	
	/**
	 * Train the ML to a certain accuracy
	 * @param train
	 * @param targetAccuracy
	 * @param verbose
	 */
	public void train(double targetError, boolean verbose);

	/**
	 * Set the MLMethod to run
	 * @param newMlMethod
	 */
	public void setMl(MLMethod newMl);
	
	/**
	 * Returns the current MLMethod
	 * @return
	 */
	public MLMethod getMl();

	public void trainStep();

	public String getLabel();
	
	/**
	 * Calculates the error in classification as 1 - accuracy
	 * @param testData
	 * 
	 */
	//public double classificationError(MLDataSet testData);

}
