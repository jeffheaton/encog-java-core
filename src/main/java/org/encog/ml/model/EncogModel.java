/*
 * Encog(tm) Core v3.3 - Java Version
 * http://www.heatonresearch.com/encog/
 * https://github.com/encog/encog-java-core
 
 * Copyright 2008-2014 Heaton Research, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *   
 * For more information on Heaton Research copyrights, licenses 
 * and trademarks visit:
 * http://www.heatonresearch.com/copyright
 */
package org.encog.ml.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.encog.EncogError;
import org.encog.NullStatusReportable;
import org.encog.StatusReportable;
import org.encog.mathutil.randomize.generate.MersenneTwisterGenerateRandom;
import org.encog.ml.MLClassification;
import org.encog.ml.MLMethod;
import org.encog.ml.MLRegression;
import org.encog.ml.TrainingImplementationType;
import org.encog.ml.data.MLDataSet;
import org.encog.ml.data.cross.DataFold;
import org.encog.ml.data.cross.KFoldCrossvalidation;
import org.encog.ml.data.versatile.MatrixMLDataSet;
import org.encog.ml.data.versatile.VersatileMLDataSet;
import org.encog.ml.data.versatile.columns.ColumnDefinition;
import org.encog.ml.data.versatile.columns.ColumnType;
import org.encog.ml.data.versatile.division.DataDivision;
import org.encog.ml.factory.MLMethodFactory;
import org.encog.ml.factory.MLTrainFactory;
import org.encog.ml.model.config.FeedforwardConfig;
import org.encog.ml.model.config.MethodConfig;
import org.encog.ml.model.config.NEATConfig;
import org.encog.ml.model.config.PNNConfig;
import org.encog.ml.model.config.RBFNetworkConfig;
import org.encog.ml.model.config.SVMConfig;
import org.encog.ml.train.MLTrain;
import org.encog.ml.train.strategy.StopTrainingStrategy;
import org.encog.ml.train.strategy.end.SimpleEarlyStoppingStrategy;
import org.encog.util.Format;
import org.encog.util.simple.EncogUtility;

/**
 * Encog model is designed to allow you to easily swap between different model
 * types and automatically normalize data.  It is designed to work with a
 * VersatileMLDataSet only.
 */
public class EncogModel {

	/**
	 * The dataset to use.
	 */
	private final VersatileMLDataSet dataset;
	
	/**
	 * The input features.
	 */
	private final List<ColumnDefinition> inputFeatures = new ArrayList<ColumnDefinition>();
	
	/**
	 * The predicted features.
	 */
	private final List<ColumnDefinition> predictedFeatures = new ArrayList<ColumnDefinition>();
	
	/**
	 * The training dataset.
	 */
	private MatrixMLDataSet trainingDataset;
	
	/**
	 * The validation dataset.
	 */
	private MatrixMLDataSet validationDataset;
	
	/**
	 * The standard configrations for each method type.
	 */
	private final Map<String, MethodConfig> methodConfigurations = new HashMap<String, MethodConfig>();
	
	/**
	 * The current method configuration, determined by the selected model.
	 */
	private MethodConfig config;
	
	/**
	 * The selected method type.
	 */
	private String methodType;
	
	/**
	 * The method arguments for the selected method.
	 */
	private String methodArgs;
	
	/**
	 * The selected training type.
	 */
	private String trainingType;
	
	/**
	 * The training arguments for the selected training type.
	 */
	private String trainingArgs;
	
	/**
	 * The report.
	 */
	private StatusReportable report = new NullStatusReportable();

	/**
	 * Construct a model for the specified dataset.
	 * @param theDataset The dataset.
	 */
	public EncogModel(VersatileMLDataSet theDataset) {
		this.dataset = theDataset;
		this.methodConfigurations.put(MLMethodFactory.TYPE_FEEDFORWARD,
				new FeedforwardConfig());
		this.methodConfigurations
				.put(MLMethodFactory.TYPE_SVM, new SVMConfig());
		this.methodConfigurations.put(MLMethodFactory.TYPE_RBFNETWORK,
				new RBFNetworkConfig());
		this.methodConfigurations.put(MLMethodFactory.TYPE_NEAT,
				new NEATConfig());
		this.methodConfigurations
				.put(MLMethodFactory.TYPE_PNN, new PNNConfig());
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

	/**
	 * Specify a validation set to hold back.
	 * @param validationPercent The percent to use for validation.
	 * @param shuffle True to shuffle.
	 * @param seed The seed for random generation.
	 */
	public void holdBackValidation(double validationPercent, boolean shuffle,
			int seed) {
		List<DataDivision> dataDivisionList = new ArrayList<DataDivision>();
		dataDivisionList.add(new DataDivision(1.0 - validationPercent));// Training
		dataDivisionList.add(new DataDivision(validationPercent));// Validation
		this.dataset.divide(dataDivisionList, shuffle,
				new MersenneTwisterGenerateRandom(seed));
		this.trainingDataset = dataDivisionList.get(0).getDataset();
		this.validationDataset = dataDivisionList.get(1).getDataset();
	}

	/**
	 * Fit the model using cross validation.
	 * @param k The number of folds total.
	 * @param foldNum The current fold.
	 * @param fold The current fold.
	 */
	private void fitFold(int k, int foldNum, DataFold fold) {
		MLMethod method = this.createMethod();
		MLTrain train = this.createTrainer(method, fold.getTraining());

		if (train.getImplementationType() == TrainingImplementationType.Iterative) {
			SimpleEarlyStoppingStrategy earlyStop = new SimpleEarlyStoppingStrategy(
					fold.getValidation());
			train.addStrategy(earlyStop);

			StringBuilder line = new StringBuilder();
			while (!train.isTrainingDone()) {
				train.iteration();
				line.setLength(0);
				line.append("Fold #");
				line.append(foldNum);
				line.append("/");
				line.append(k);
				line.append(": Iteration #");
				line.append(train.getIteration());
				line.append(", Training Error: ");
				line.append(Format.formatDouble(train.getError(), 8));
				line.append(", Validation Error: ");
				line.append(Format.formatDouble(earlyStop.getValidationError(),
						8));
				report.report(k, foldNum, line.toString());
			}
			fold.setScore(earlyStop.getValidationError());
			fold.setMethod(method);
		} else if (train.getImplementationType() == TrainingImplementationType.OnePass) {
			train.iteration();
			double validationError = calculateError(method,
					fold.getValidation());
			this.report.report(k, k,
					"Trained, Training Error: " + train.getError()
							+ ", Validatoin Error: " + validationError);
			fold.setScore(validationError);
			fold.setMethod(method);
		} else {
			throw new EncogError("Unsupported training type for EncogModel: "
					+ train.getImplementationType());
		}
	}

	/**
	 * Calculate the error for the given method and dataset.
	 * @param method The method to use.
	 * @param data The data to use.
	 * @return The error.
	 */
	public double calculateError(MLMethod method, MLDataSet data) {
		if (this.dataset.getNormHelper().getOutputColumns().size() == 1) {
			ColumnDefinition cd = this.dataset.getNormHelper()
					.getOutputColumns().get(0);
			if (cd.getDataType() == ColumnType.nominal) {
				return EncogUtility.calculateClassificationError(
						(MLClassification) method, data);
			}
		}

		return EncogUtility.calculateRegressionError((MLRegression) method,
				data);
	}

	/**
	 * Create a trainer.
	 * @param method The method to train.
	 * @param dataset The dataset.
	 * @return The trainer.
	 */
	private MLTrain createTrainer(MLMethod method, MLDataSet dataset) {

		if (this.trainingType == null) {
			throw new EncogError(
					"Please call selectTraining first to choose how to train.");
		}
		MLTrainFactory trainFactory = new MLTrainFactory();
		MLTrain train = trainFactory.create(method, dataset, this.trainingType,
				this.trainingArgs);
		return train;
	}

	/**
	 * Crossvalidate and fit.
	 * @param k The number of folds.
	 * @param shuffle True if we should shuffle.
	 * @return The trained method.
	 */
	public MLMethod crossvalidate(int k, boolean shuffle) {
		KFoldCrossvalidation cross = new KFoldCrossvalidation(
				this.trainingDataset, k);
		cross.process(shuffle);

		int foldNumber = 0;
		for (DataFold fold : cross.getFolds()) {
			foldNumber++;
			report.report(k, foldNumber, "Fold #" + foldNumber);
			fitFold(k, foldNumber, fold);
		}

		double sum = 0;
		double bestScore = Double.POSITIVE_INFINITY;
		MLMethod bestMethod = null;
		for (DataFold fold : cross.getFolds()) {
			sum += fold.getScore();
			if (fold.getScore() < bestScore) {
				bestScore = fold.getScore();
				bestMethod = fold.getMethod();
			}
		}
		sum = sum / cross.getFolds().size();
		report.report(k, k, "Cross-validated score:" + sum);
		return bestMethod;
	}

	/**
	 * @return the trainingDataset
	 */
	public MatrixMLDataSet getTrainingDataset() {
		return trainingDataset;
	}

	/**
	 * @param trainingDataset
	 *            the trainingDataset to set
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
	 * @param validationDataset
	 *            the validationDataset to set
	 */
	public void setValidationDataset(MatrixMLDataSet validationDataset) {
		this.validationDataset = validationDataset;
	}

	/**
	 * Select the method to use.
	 * @param dataset The dataset.
	 * @param methodType The type of method.
	 * @param methodArgs The method arguments.
	 * @param trainingType The training type.
	 * @param trainingArgs The training arguments.
	 */
	public void selectMethod(VersatileMLDataSet dataset, String methodType,
			String methodArgs, String trainingType, String trainingArgs) {

		if (!this.methodConfigurations.containsKey(methodType)) {
			throw new EncogError("Don't know how to autoconfig method: "
					+ methodType);
		}
		this.methodType = methodType;
		this.methodArgs = methodArgs;
		this.config = this.methodConfigurations.get(methodType);
		dataset.getNormHelper().setStrategy(
				this.methodConfigurations.get(methodType)
						.suggestNormalizationStrategy(dataset, methodArgs));

	}

	/**
	 * Create the selected method.
	 * @return The created method.
	 */
	public MLMethod createMethod() {
		if (this.methodType == null) {
			throw new EncogError(
					"Please call selectMethod first to choose what type of method you wish to use.");
		}
		MLMethodFactory methodFactory = new MLMethodFactory();
		MLMethod method = methodFactory.create(methodType, methodArgs, dataset
				.getNormHelper().calculateNormalizedInputCount(), this.config
				.determineOutputCount(dataset));
		return method;
	}

	/**
	 * Select the method to create.
	 * @param dataset The dataset.
	 * @param methodType The method type.
	 */
	public void selectMethod(VersatileMLDataSet dataset, String methodType) {
		if (!this.methodConfigurations.containsKey(methodType)) {
			throw new EncogError("Don't know how to autoconfig method: "
					+ methodType);
		}

		this.config = this.methodConfigurations.get(methodType);
		this.methodType = methodType;
		this.methodArgs = this.config.suggestModelArchitecture(dataset);
		dataset.getNormHelper().setStrategy(
				this.config.suggestNormalizationStrategy(dataset, methodArgs));

	}

	/**
	 * Select the training type.
	 * @param dataset The dataset.
	 */
	public void selectTrainingType(VersatileMLDataSet dataset) {
		if (this.methodType == null) {
			throw new EncogError(
					"Please select your training method, before your training type.");
		}
		MethodConfig config = this.methodConfigurations.get(methodType);
		selectTraining(dataset, config.suggestTrainingType(),
				config.suggestTrainingArgs(trainingType));
	}

	/**
	 * Select the training to use.
	 * @param dataset The dataset.
	 * @param trainingType The type of training.
	 * @param trainingArgs The training arguments.
	 */
	public void selectTraining(VersatileMLDataSet dataset, String trainingType,
			String trainingArgs) {
		if (this.methodType == null) {
			throw new EncogError(
					"Please select your training method, before your training type.");
		}

		this.trainingType = trainingType;
		this.trainingArgs = trainingArgs;
	}

	/**
	 * @return the methodConfigurations
	 */
	public Map<String, MethodConfig> getMethodConfigurations() {
		return methodConfigurations;
	}

	/**
	 * @return the report
	 */
	public StatusReportable getReport() {
		return report;
	}

	/**
	 * @param report
	 *            the report to set
	 */
	public void setReport(StatusReportable report) {
		this.report = report;
	}

	public MLRegression train(boolean shuffle) {
		MLMethod method = this.createMethod();
		MLTrain train = this.createTrainer(method, this.trainingDataset);

		if (train.getImplementationType() == TrainingImplementationType.Iterative) {
			StopTrainingStrategy earlyStop = new StopTrainingStrategy(0.01,10);
			train.addStrategy(earlyStop);

			StringBuilder line = new StringBuilder();
			while (!train.isTrainingDone()) {
				train.iteration();
				line.setLength(0);
				line.append("Iteration #");
				line.append(train.getIteration());
				line.append(", Training Error: ");
				line.append(Format.formatDouble(train.getError(), 8));
				report.report(0, 0, line.toString());
			}
		} else if (train.getImplementationType() == TrainingImplementationType.OnePass) {
			train.iteration();
			this.report.report(0, 0,
					"Trained, Training Error: " + train.getError());
		} else {
			throw new EncogError("Unsupported training type for EncogModel: "
					+ train.getImplementationType());
		}
		return (MLRegression)method;
	}

	public MLRegression train(int iterations, boolean shuffle) {
		MLMethod method = this.createMethod();
		MLTrain train = this.createTrainer(method, this.trainingDataset);

		if (train.getImplementationType() == TrainingImplementationType.Iterative) {
			StopTrainingStrategy earlyStop = new StopTrainingStrategy(0.01,10);
			train.addStrategy(earlyStop);

			StringBuilder line = new StringBuilder();
			for(int i=1;i<=iterations;i++) {
				train.iteration();
				line.setLength(0);
				line.append("Iteration #");
				line.append(train.getIteration());
				line.append("/");
				line.append(iterations);
				line.append(", Training Error: ");
				line.append(Format.formatDouble(train.getError(), 8));
				report.report(0, 0, line.toString());
			}
		} else if (train.getImplementationType() == TrainingImplementationType.OnePass) {
			train.iteration();
			this.report.report(0, 0,
					"Trained, Training Error: " + train.getError());
		} else {
			throw new EncogError("Unsupported training type for EncogModel: "
					+ train.getImplementationType());
		}
		return (MLRegression)method;
	}

}
