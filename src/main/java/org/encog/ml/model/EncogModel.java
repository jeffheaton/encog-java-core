package org.encog.ml.model;

import java.util.ArrayList;
import java.util.List;

import org.encog.mathutil.randomize.generate.MersenneTwisterGenerateRandom;
import org.encog.ml.MLMethod;
import org.encog.ml.data.MLDataSet;
import org.encog.ml.data.cross.DataFold;
import org.encog.ml.data.cross.KFoldCrossvalidation;
import org.encog.ml.data.versatile.ColumnDefinition;
import org.encog.ml.data.versatile.DataDivision;
import org.encog.ml.data.versatile.MatrixMLDataSet;
import org.encog.ml.data.versatile.VersatileMLDataSet;
import org.encog.ml.train.strategy.end.SimpleEarlyStoppingStrategy;
import org.encog.neural.networks.ContainsFlat;
import org.encog.neural.networks.training.propagation.resilient.ResilientPropagation;
import org.encog.util.simple.EncogUtility;

public class EncogModel {
	
	private final VersatileMLDataSet dataset;
	private final List<ColumnDefinition> inputFeatures = new ArrayList<ColumnDefinition>();
	private final List<ColumnDefinition> predictedFeatures = new ArrayList<ColumnDefinition>();
	private MatrixMLDataSet trainingDataset;
	private MatrixMLDataSet validationDataset;
	private MLMethod method;
	
	public EncogModel(VersatileMLDataSet theDataset) {
		this.dataset = theDataset;
	}

	/**
	 * @return the dataset
	 */
	public VersatileMLDataSet getDataset() {
		return dataset;
	}

	/**
	 * @return the inputFeatures
	 */
	public List<ColumnDefinition> getInputFeatures() {
		return inputFeatures;
	}

	/**
	 * @return the predictedFeatures
	 */
	public List<ColumnDefinition> getPredictedFeatures() {
		return predictedFeatures;
	}
	
	
	public void createModel() {
		this.dataset.normalize();
		int modelInputCount = this.dataset.getInputSize();
		int modelOutputCount = this.dataset.getIdealSize();
		this.method = EncogUtility.simpleFeedForward(modelInputCount, 10, 0, modelOutputCount, true);
	}
	
	public void holdBackValidation(double validationPercent,boolean shuffle, int seed) {
		List<DataDivision> dataDivisionList = new ArrayList<DataDivision>();
		dataDivisionList.add(new DataDivision(1.0-validationPercent));// Training
		dataDivisionList.add(new DataDivision(validationPercent));// Validation
		this.dataset.divide(dataDivisionList,shuffle,new MersenneTwisterGenerateRandom(seed));
		this.trainingDataset = dataDivisionList.get(0).getDataset();
		this.validationDataset = dataDivisionList.get(1).getDataset();
	}
	
	public void fitFold(DataFold fold, MLMethod method) {
		ResilientPropagation rprop = new ResilientPropagation((ContainsFlat)method,fold.getTraining());
		SimpleEarlyStoppingStrategy earlyStop = new SimpleEarlyStoppingStrategy(fold.getValidation());
		rprop.addStrategy(earlyStop);
		
		while(!rprop.isTrainingDone()) {
			rprop.iteration();
			System.out.println("Iteration #"+ rprop.getIteration() + ", Training Error: " + rprop.getError()+", Validation Error:" + earlyStop.getValidationError() );
		}
		fold.setScore(earlyStop.getValidationError());
		fold.setMethod(method);
	}
	
	public MLMethod crossvalidate(int k, boolean shuffle) {
		int modelInputCount = this.dataset.getInputSize();
		int modelOutputCount = this.dataset.getIdealSize();
		
		KFoldCrossvalidation cross = new KFoldCrossvalidation(this.trainingDataset,k);
		cross.process(shuffle);
		
		int foldNumber = 0;
		double sumValidationError = 0;
		for(DataFold fold:cross.getFolds()) {
			foldNumber++;
			System.out.println("Fold #" + foldNumber);
			MLMethod model = EncogUtility.simpleFeedForward(modelInputCount, 10, 0, modelOutputCount, true);
			fitFold(fold, model);
		}
		
		double sum = 0;
		double bestScore = Double.POSITIVE_INFINITY;
		MLMethod bestMethod = null;
		for(DataFold fold: cross.getFolds()) {
			sum+=fold.getScore();
			if( fold.getScore()<bestScore) {
				bestScore = fold.getScore();
				bestMethod = fold.getMethod();
			}
		}
		sum=sum/cross.getFolds().size();
		System.out.println("Cross-validated score:" + sum);
		return bestMethod;
	}

	/**
	 * @return the trainingDataset
	 */
	public MatrixMLDataSet getTrainingDataset() {
		return trainingDataset;
	}

	/**
	 * @param trainingDataset the trainingDataset to set
	 */
	public void setTrainingDataset(MatrixMLDataSet trainingDataset) {
		this.trainingDataset = trainingDataset;
	}

	/**
	 * @return the validationDataset
	 */
	public MatrixMLDataSet getValidationDataset() {
		return validationDataset;
	}

	/**
	 * @param validationDataset the validationDataset to set
	 */
	public void setValidationDataset(MatrixMLDataSet validationDataset) {
		this.validationDataset = validationDataset;
	}
	
	
	
}
