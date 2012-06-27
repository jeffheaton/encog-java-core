/**
 * 
 */
package org.encog.ensemble;

import org.encog.ml.MLClassification;
import org.encog.ml.MLMethod;
import org.encog.ml.MLRegression;
import org.encog.ml.data.MLDataSet;
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
	 * Get the dataset for this member
	 * @return
	 */
	public EnsembleDataSet getTrainingSet();

	/**
	 * Train the ML to a certain accuracy
	 * @param train
	 * @param targetAccuracy
	 */
	public void train(MLTrain train, double targetError);
	
	/**
	 * Train the ML to a certain accuracy
	 * @param train
	 * @param targetAccuracy
	 * @param verbose
	 */
	public void train(MLTrain train, double targetError, boolean verbose);
	
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
	
	/**
	 * Calculates the error in classification as 1 - accuracy
	 * @param testData
	 * 
	 */
	//public double classificationError(MLDataSet testData);

}
