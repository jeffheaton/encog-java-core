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

package org.encog.ensemble;

import java.util.ArrayList;

import org.encog.ensemble.data.EnsembleDataSet;
import org.encog.ensemble.data.factories.EnsembleDataSetFactory;
import org.encog.ml.data.MLData;
import org.encog.ml.data.MLDataPair;
import org.encog.ml.data.MLDataSet;
import org.encog.ml.data.basic.BasicMLData;
import org.encog.ensemble.aggregator.WeightedAveraging.WeightMismatchException;

public abstract class Ensemble {

	private final int DEFAULT_MAX_ITERATIONS = 2000;
	protected EnsembleDataSetFactory dataSetFactory;
	protected EnsembleTrainFactory trainFactory;
	protected EnsembleAggregator aggregator;
	protected ArrayList<EnsembleML> members;
	protected EnsembleMLMethodFactory mlFactory;
	protected MLDataSet aggregatorDataSet;

	public class NotPossibleInThisMethod extends Exception {

		/**
		 * This means the current feature is not applicable in the specified method
		 */
		private static final long serialVersionUID = 5118253806179408868L;

	}

	public class TrainingAborted extends Exception {

		public TrainingAborted(String string) {
			super(string);
		}

		/**
		 * This means we tried training this ensemble and failed too many times in a row
		 */
		private static final long serialVersionUID = -5074472788684621859L;
		
	}

	/**
	 * Initialise ensemble components
	 */
	abstract public void initMembers();

	public EnsembleML generateNewMember() {
		GenericEnsembleML newML = new GenericEnsembleML(mlFactory.createML(this.dataSetFactory.getInputCount(), this.dataSetFactory.getOutputCount()),mlFactory.getLabel());
		newML.setTrainingSet(dataSetFactory.getNewDataSet());
		newML.setTraining(trainFactory.getTraining(newML.getMl(), newML.getTrainingSet()));
		return newML;
	}
	
	public void addNewMember() {
		members.add(generateNewMember());		
	}
	
	public void initMembersBySplits(int splits)
	{
		if ((this.dataSetFactory != null) &&
			(splits > 0) &&
			(this.dataSetFactory.hasSource()))
		{
			for (int i = 0; i < splits; i++)
			{
				GenericEnsembleML newML = new GenericEnsembleML(mlFactory.createML(this.dataSetFactory.getInputCount(), this.dataSetFactory.getOutputCount()),mlFactory.getLabel());
				newML.setTrainingSet(dataSetFactory.getNewDataSet());
				newML.setTraining(trainFactory.getTraining(newML.getMl(), newML.getTrainingSet()));
				members.add(newML);
			}
			if(aggregator.needsTraining())
				aggregatorDataSet = dataSetFactory.getNewDataSet();
		}
	}

	/**
	 * Set the training method to use for this ensemble
	 * @param newTrainFactory The training factory.
	 */
	public void setTrainingMethod(EnsembleTrainFactory newTrainFactory) {
		this.trainFactory = newTrainFactory;
		initMembers();
	}

	/**
	 * Set which training data to base the training on
	 * @param data The training data.
	 */
	public void setTrainingData(MLDataSet data) {
		dataSetFactory.setInputData(data);
		initMembers();
	}

	/**
	 * Set which dataSetFactory to use to create the correct tranining sets
	 * @param dataSetFactory The data set factory.
	 */
	public void setTrainingDataFactory(EnsembleDataSetFactory dataSetFactory) {
		this.dataSetFactory = dataSetFactory;
		initMembers();
	}

	public void trainMember(int index, double targetError, double selectionError, int maxIterations, EnsembleDataSet selectionSet, boolean verbose) throws TrainingAborted {
		EnsembleML current = members.get(index);
		trainMember(current,targetError,selectionError,maxIterations,DEFAULT_MAX_ITERATIONS,selectionSet,verbose);
	}

	public void trainMember(EnsembleML current, double targetError, double selectionError, int maxIterations, int maxLoops, EnsembleDataSet selectionSet, boolean verbose) throws TrainingAborted {
		int attempt = 0;
		do {
			long startTime = System.nanoTime();
			mlFactory.reInit(current.getMl());
			current.train(targetError, maxIterations, verbose);
			long endTime = System.nanoTime();
			if (verbose)
			{
				System.out.println("training took " + ((double)(endTime - startTime) / 1000000000.0));
				System.out.println("test MSE: " + current.getError(selectionSet) + " on " + selectionSet.size() + " data points");
			};
			attempt++;
			if (attempt > maxLoops) {
				throw new TrainingAborted("Too many attempts at training ensemble member");
			}
		} while (current.getError(selectionSet) > selectionError);
	}
	
	public void trainMember(EnsembleML current, double targetError, double selectionError, EnsembleDataSet selectionSet, boolean verbose) throws TrainingAborted {
		trainMember(current, targetError, selectionError, DEFAULT_MAX_ITERATIONS, DEFAULT_MAX_ITERATIONS, selectionSet, verbose);
	}

	public void trainMember(int index, double targetError, double selectionError, EnsembleDataSet selectionSet, boolean verbose) throws TrainingAborted {
		trainMember(index, targetError, selectionError, DEFAULT_MAX_ITERATIONS, selectionSet, verbose);
	}
	
	public void retrainAggregator() {
		EnsembleDataSet aggTrainingSet = new EnsembleDataSet(members.size() * aggregatorDataSet.getIdealSize(),aggregatorDataSet.getIdealSize());
		for (MLDataPair trainingInput:aggregatorDataSet) {
			BasicMLData trainingInstance = new BasicMLData(members.size() * aggregatorDataSet.getIdealSize());
			int index = 0;
			for(EnsembleML member:members){
				for(double val:member.compute(trainingInput.getInput()).getData()) {
					trainingInstance.add(index++, val);
				}
			}
			aggTrainingSet.add(trainingInstance,trainingInput.getIdeal());
		}
		aggregator.setTrainingSet(aggTrainingSet);
		aggregator.train();
	}
	
	/**
	 * Train the ensemble to a target accuracy
	 * @param targetError The target error.
	 * @param selectionError The selection error.
	 * @param maxIterations
	 * @param maxLoops
	 * @param selectionSet
	 * @param verbose
     * @throws TrainingAborted
     */
	public void train(double targetError, double selectionError, int maxIterations, int maxLoops, EnsembleDataSet selectionSet, boolean verbose) throws TrainingAborted {
		
		for (EnsembleML current : members)
		{
			trainMember(current, targetError, selectionError, maxIterations, maxLoops, selectionSet, verbose);
		}
		if(aggregator.needsTraining()) {
			retrainAggregator();
		}
	}
	public void train(double targetError, double selectionError, EnsembleDataSet selectionSet, boolean verbose) throws TrainingAborted {
		train(targetError, selectionError, DEFAULT_MAX_ITERATIONS, DEFAULT_MAX_ITERATIONS, selectionSet, verbose);
	}

	/**
	 * Train the ensemble to a target accuracy
	 * @param targetError The target error.
	 * @param selectionError The selection error.
	 * @param testset The test set.
	 * @throws TrainingAborted
	 */
	public void train(double targetError, double selectionError, EnsembleDataSet testset) throws TrainingAborted {
		train(targetError, selectionError, testset, false);
	}

	public void train(double targetError, double selectionError, int maxIterations, EnsembleDataSet testset) throws TrainingAborted {
		train(targetError, selectionError, maxIterations, DEFAULT_MAX_ITERATIONS, testset, false);
	}

	/**
	 * Extract a specific training set from the Ensemble
	 * @param setNumber
	 * @return The training set.
	 */
	public MLDataSet getTrainingSet(int setNumber) {
		return members.get(setNumber).getTrainingSet();
	}

	/**
	 * Extract a specific MLMethod
	 * @param memberNumber
	 * @return The MLMethod.
	 */
	public EnsembleML getMember(int memberNumber) {
		return members.get(memberNumber);
	}

	/**
	 * Add a member to the ensemble
	 * @param newMember
	 * @throws NotPossibleInThisMethod
	 */
	public void addMember(EnsembleML newMember) throws NotPossibleInThisMethod {
		members.add(newMember);
	}

	/**
	 * Compute the output for a specific input
	 * @param input
	 * @return The data.
	 * @throws WeightMismatchException 
	 */
	public MLData compute(MLData input) throws WeightMismatchException {
		ArrayList<MLData> outputs = new ArrayList<MLData>();
		for(EnsembleML member: members)
		{
			MLData computed = member.compute(input);
			outputs.add(computed);
		}
		return aggregator.evaluate(outputs);
	}

	/**
	 * @return Returns the ensemble aggregation method
	 */
	public EnsembleAggregator getAggregator() {
		return aggregator;
	}

	/**
	 * Sets the ensemble aggregation method
	 * @param aggregator
	 */
	public void setAggregator(EnsembleAggregator aggregator) {
		this.aggregator = aggregator;
	}

	/**
	 * Return what type of problem this Ensemble is solving
	 * @return The problem type.
	 */
	abstract public EnsembleTypes.ProblemType getProblemType();

}