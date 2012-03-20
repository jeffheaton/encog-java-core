package org.encog.ensembles;

import org.encog.ml.data.MLData;
import org.encog.ml.data.MLDataSet;

public interface Ensemble {
	
	/**
	 * Set the training method to use for this ensemble
	 * @param newTrain
	 */
	public void setTrainingMethod(EnsembleTrain newTrain);
	
	/**
	 * Set which training data to base the training on
	 * @param trainingData
	 */
	public void setTrainingData(MLDataSet trainingData);
	
	/**
	 * Set which dataSetFactory to use to create the correct tranining sets
	 * @param dataSetFactory
	 */
	public void setTrainingDataFactory(EnsembleDataSetFactory dataSetFactory);
	
	/**
	 * Train the ensemble to a target accuracy
	 * @param targetAccuracy
	 */
	public void train(double targetAccuracy);
	
	/**
	 * Extract a specific training set from the Ensemble
	 * @param setNumber
	 * @return
	 */
	public MLDataSet getTrainingSet(int setNumber);
	
	/**
	 * Compute the output for a specific input
	 * @param input
	 * @return
	 */
	public MLData compute(MLData input);
	
	/**
	 * Get the classification or regression error on the
	 * cross-validation set
	 * @return
	 */
	public double getCrossValidationError();
	
	/**
	 * Return what type of problem this Ensemble is solving
	 * @return
	 */
	public EnsembleTypes.ProblemType getProblemType();
	
}
