package org.encog.ensemble.bagging;

import java.util.ArrayList;

import org.encog.ensemble.Ensemble;
import org.encog.ensemble.EnsembleDataSetFactory;
import org.encog.ensemble.EnsembleML;
import org.encog.ensemble.EnsembleMLFactory;
import org.encog.ensemble.EnsembleTrain;
import org.encog.ensemble.EnsembleTrainFactory;
import org.encog.ensemble.EnsembleTypes;
import org.encog.ensemble.EnsembleTypes.ProblemType;
import org.encog.ensemble.aggregator.EnsembleAggregator;
import org.encog.ml.data.MLData;
import org.encog.ml.data.MLDataSet;
import org.encog.ml.data.basic.BasicMLData;
import org.encog.ml.factory.MLMethodFactory;
import org.encog.ml.train.MLTrain;

public class Bagging implements Ensemble {

	private BaggingDataSetFactory dataSetFactory;
	private EnsembleTrainFactory trainFactory;
	private EnsembleAggregator aggregator;
	private ArrayList<BaggingML> members;
	private EnsembleMLFactory mlFactory;
	private int splits;
	
	public Bagging(int splits, int dataSetSize, EnsembleMLFactory mlFactory, EnsembleTrainFactory trainFactory)
	{
		this.dataSetFactory = new BaggingDataSetFactory();
		dataSetFactory.setDataSetSize(dataSetSize);
		this.splits = splits;
		this.mlFactory = mlFactory;
		this.trainFactory = trainFactory;
		this.members = new ArrayList<BaggingML>();
	}
	
	@Override
	public void setTrainingMethod(EnsembleTrainFactory newTrainFactory) {
		this.trainFactory = newTrainFactory;
	}

	private void initMembers()
	{
		if ((this.dataSetFactory != null) && 
			(this.members.size() > 0) &&
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

	@Override
	public void train(double targetAccuracy) {
		for (EnsembleML current : members)
		{
			EnsembleTrain train = trainFactory.getTraining(current, current.getTrainingSet());
			current.train(train, targetAccuracy);
		}
	}

	@Override
	public MLDataSet getTrainingSet(int setNumber) {
		return members.get(setNumber).getTrainingSet();
	}

	@Override
	public MLData compute(MLData input) {
		ArrayList<MLData> outputs = new ArrayList<MLData>();
		for(BaggingML member: members) 
		{
			outputs.add(member.compute(input));
		}
		return aggregator.evaluate(outputs);
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
