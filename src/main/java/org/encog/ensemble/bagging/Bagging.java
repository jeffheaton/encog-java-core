package org.encog.ensemble.bagging;

import java.util.ArrayList;

import org.encog.ensemble.Ensemble;
import org.encog.ensemble.EnsembleDataSetFactory;
import org.encog.ensemble.EnsembleML;
import org.encog.ensemble.EnsembleMLMethodFactory;
import org.encog.ensemble.EnsembleTrainFactory;
import org.encog.ensemble.EnsembleTypes;
import org.encog.ensemble.ResamplingDataSetFactory;
import org.encog.ensemble.EnsembleTypes.ProblemType;
import org.encog.ensemble.aggregator.EnsembleAggregator;
import org.encog.ml.data.MLData;
import org.encog.ml.data.MLDataSet;
import org.encog.ml.train.MLTrain;
import org.encog.neural.networks.BasicNetwork;

public class Bagging extends Ensemble {

	private int splits;
	
	public Bagging(int splits, int dataSetSize, EnsembleMLMethodFactory mlFactory, EnsembleTrainFactory trainFactory, EnsembleAggregator aggregator)
	{
		this.dataSetFactory = new ResamplingDataSetFactory();
		((ResamplingDataSetFactory) dataSetFactory).setDataSetSize(dataSetSize);
		this.splits = splits;
		this.mlFactory = mlFactory;
		this.trainFactory = trainFactory;
		this.members = new ArrayList<EnsembleML>();
		this.aggregator = aggregator;
		initMembers();
	}
	
	@Override
	public void initMembers()
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


	public ProblemType getProblemType() {
		return EnsembleTypes.ProblemType.CLASSIFICATION;
	}

	@Override
	public EnsembleML getMember(int memberNumber) {
		return members.get(memberNumber);
	}


}
