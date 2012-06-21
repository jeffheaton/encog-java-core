/**
 * 
 */
package org.encog.ensemble;

import org.encog.ml.MLClassification;
import org.encog.ml.MLMethod;
import org.encog.ml.MLRegression;

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
	public void train(EnsembleTrain train, double targetAccuracy);
	
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

}
