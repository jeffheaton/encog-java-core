package org.encog.ensemble.bagging;

import java.util.ArrayList;

import org.encog.ensemble.Ensemble;
import org.encog.ensemble.EnsembleDataSetFactory;
import org.encog.ensemble.EnsembleML;
import org.encog.ensemble.EnsembleMLMethodFactory;
import org.encog.ensemble.EnsembleTrainFactory;
import org.encog.ensemble.EnsembleTypes;
import org.encog.ensemble.EnsembleTypes.ProblemType;
import org.encog.ensemble.aggregator.EnsembleAggregator;
import org.encog.ml.data.MLData;
import org.encog.ml.data.MLDataSet;
import org.encog.ml.train.MLTrain;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.training.Train;

public class Bagging implements Ensemble {

	private BaggingDataSetFactory dataSetFactory;
	private EnsembleTrainFactory trainFactory;
	private EnsembleAggregator aggregator;
	private ArrayList<BaggingML> members;
	private EnsembleMLMethodFactory mlFactory;
	private int splits;
	
	public Bagging(int splits, int dataSetSize, EnsembleMLMethodFactory mlFactory, EnsembleTrainFactory trainFactory, EnsembleAggregator aggregator)
	{
		this.dataSetFactory = new BaggingDataSetFactory();
		dataSetFactory.setDataSetSize(dataSetSize);
		this.splits = splits;
		this.mlFactory = mlFactory;
		this.trainFactory = trainFactory;
		this.members = new ArrayList<BaggingML>();
		this.aggregator = aggregator;
		initMembers();
	}
	
	@Override
	public void setTrainingMethod(EnsembleTrainFactory newTrainFactory) {
		this.trainFactory = newTrainFactory;
		initMembers();
	}

	private void initMembers()
	{
		if ((this.dataSetFactory != null) && 
			(this.splits > 0) &&
			(this.dataSetFactory.hasSource()))
		{
			for (int i = 0; i < splits; i++)
			{
				BaggingML newML = new BaggingML(mlFactory.createML(this.dataSetFactory.getInputCount(), this.dataSetFactory.getOutputCount()));
				newML.setTrainingSet(dataSetFactory.getNewDataSet());
				members.add(newML);
			}
		}
	}
	
	@Override
	public void setTrainingData(MLDataSet data) {
		dataSetFactory.setInputData(data);
		initMembers();
	}

	@Override
	public void setTrainingDataFactory(EnsembleDataSetFactory dataSetFactory) {
		this.dataSetFactory = (BaggingDataSetFactory) dataSetFactory;
		initMembers();
	}

	public void train(double targetError, boolean verbose) {
		for (EnsembleML current : members)
		{
			Train train = trainFactory.getTraining((BasicNetwork)current.getMl(), current.getTrainingSet());
			//final Train train = new ResilientPropagation(network, trainingSet);
			System.out.println("Training: " + current.toString());
			current.train(train, targetError, verbose);
		}
	}

	@Override
	public void train(double targetError) {
		train(targetError, false);
	}
	

	@Override
	public MLDataSet getTrainingSet(int setNumber) {
		return members.get(setNumber).getTrainingSet();
	}

	@Override
	public MLData compute(MLData input) {
		/*ArrayList<MLData> outputs = new ArrayList<MLData>();
		for(BaggingML member: members) 
		{
			MLData computed = member.compute(input);
			outputs.add(computed);
		}
		return aggregator.evaluate(outputs);*/
		return members.get(0).compute(input);
	}

	@Override
	public double getCrossValidationError() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public ProblemType getProblemType() {
		return EnsembleTypes.ProblemType.CLASSIFICATION;
	}

	@Override
	public EnsembleML getMember(int memberNumber) {
		return members.get(memberNumber);
	}

	@Override
	public void addMember(EnsembleML newMember) {
		members.add((BaggingML) newMember);
	}

	public EnsembleAggregator getAggregator() {
		return aggregator;
	}

	public void setAggregator(EnsembleAggregator aggregator) {
		this.aggregator = aggregator;
	}

}
