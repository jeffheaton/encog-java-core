package org.encog.ensemble;

import java.util.ArrayList;


import org.encog.ensemble.aggregator.EnsembleAggregator;
import org.encog.ensemble.data.factories.EnsembleDataSetFactory;
import org.encog.ml.data.MLData;
import org.encog.ml.data.MLDataSet;
import org.encog.ml.train.MLTrain;
import org.encog.neural.networks.BasicNetwork;
public abstract class Ensemble {

	protected EnsembleDataSetFactory dataSetFactory;
	protected EnsembleTrainFactory trainFactory;
	protected EnsembleAggregator aggregator;
	protected ArrayList<EnsembleML> members;
	protected EnsembleMLMethodFactory mlFactory;

	public class NotPossibleInThisMethod extends Exception {

		/**
		 * This means you the current feature is not applicable in the specified method
		 */
		private static final long serialVersionUID = 5118253806179408868L;
		
	}
	
	/**
	 * Initialise ensemble components
	 */
	abstract public void initMembers();
	
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
	
	/**
	 * Train the ensemble to a target accuracy
	 * @param targetAccuracy
	 * @param verbose
	 * @return
	 */
	public int train(double targetError, boolean verbose) {
		int iteration = 0;
		for (EnsembleML current : members)
		{
			iteration++;
			MLTrain train = trainFactory.getTraining((BasicNetwork)current.getMl(), current.getTrainingSet());
			if(verbose) System.out.println("Training: " + current.toString());
			current.train(train, targetError, verbose);
		}
		return iteration;
	}
	
	/**
	 * Train the ensemble to a target accuracy
	 * @param targetAccuracy
	 * @return
	 */
	public int train(double targetError) {
		return train(targetError, false);
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
