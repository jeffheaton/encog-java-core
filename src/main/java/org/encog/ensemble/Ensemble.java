package org.encog.ensemble;

import java.util.ArrayList;


import org.encog.ensemble.data.EnsembleDataSet;
import org.encog.ensemble.data.factories.EnsembleDataSetFactory;
import org.encog.ml.data.MLData;
import org.encog.ml.data.MLDataPair;
import org.encog.ml.data.MLDataSet;
import org.encog.ml.data.basic.BasicMLData;

public abstract class Ensemble {

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
				addNewMember();
			}
			if(aggregator.needsTraining()) 
				aggregatorDataSet = dataSetFactory.getNewDataSet();
		}
	}
	
	/**
	 * Set the training method to use for this ensemble
	 * @param newTrain
	 */
	public void setTrainingMethod(EnsembleTrainFactory newTrainFactory) {
		this.trainFactory = newTrainFactory;
		initMembers();
	}
	
	/**
	 * Set which training data to base the training on
	 * @param trainingData
	 */
	public void setTrainingData(MLDataSet data) {
		dataSetFactory.setInputData(data);
		initMembers();
	}
	
	/**
	 * Set which dataSetFactory to use to create the correct tranining sets
	 * @param dataSetFactory
	 */
	public void setTrainingDataFactory(EnsembleDataSetFactory dataSetFactory) {
		this.dataSetFactory = dataSetFactory;
		initMembers();
	}

	public void trainMember(int index, double targetError, double selectionError, EnsembleDataSet selectionSet, boolean verbose) {
		EnsembleML current = members.get(index);
		do {
			mlFactory.reInit(current.getMl());
			current.train(targetError, verbose);
			if (verbose) {System.out.println("test MSE: " + current.getError(selectionSet));};
		} while (current.getError(selectionSet) > selectionError);
	}

	public void trainMember(EnsembleML current, double targetError, double selectionError, EnsembleDataSet selectionSet, boolean verbose) {
		do {
			mlFactory.reInit(current.getMl());
			current.train(targetError, verbose);
			if (verbose) {System.out.println("test MSE: " + current.getError(selectionSet));};
		} while (current.getError(selectionSet) > selectionError);
	}
	
	/**
	 * Train the ensemble to a target accuracy
	 * @param targetAccuracy
	 * @param verbose
	 * @param selectionSet 
	 * @return
	 */
	public void train(double targetError, double selectionError, EnsembleDataSet selectionSet, boolean verbose) {
		
		for (EnsembleML current : members)
		{
			trainMember(current, targetError, selectionError, selectionSet, verbose);
		}
		if(aggregator.needsTraining()) {
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
	}
	
	/**
	 * Train the ensemble to a target accuracy
	 * @param targetAccuracy
	 * @return
	 */
	public void train(double targetError, double selectionError, EnsembleDataSet testset) {
		train(targetError, selectionError, testset, false);
	}
	
	/**
	 * Extract a specific training set from the Ensemble
	 * @param setNumber
	 * @return
	 */
	public MLDataSet getTrainingSet(int setNumber) {
		return members.get(setNumber).getTrainingSet();
	}
	
	/**
	 * Extract a specific MLMethod
	 * @param memberNumber
	 * @return
	 */
	public EnsembleML getMember(int memberNumber) {
		return members.get(memberNumber);
	}
	
	/**
	 * Add a member to the ensemble
	 * @param newMember
	 * @return
	 * @throws NotPossibleInThisMethod 
	 */
	public void addMember(EnsembleML newMember) throws NotPossibleInThisMethod {
		members.add(newMember);
	}
	
	/**
	 * Compute the output for a specific input
	 * @param input
	 * @return
	 */
	public MLData compute(MLData input) {
		ArrayList<MLData> outputs = new ArrayList<MLData>();
		for(EnsembleML member: members) 
		{
			MLData computed = member.compute(input);
			outputs.add(computed);
		}
		return aggregator.evaluate(outputs);
	}
	
	/**
	 * Returns the ensemble aggregation method
	 * @return
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
	 * @return
	 */
	abstract public EnsembleTypes.ProblemType getProblemType();
	
}
