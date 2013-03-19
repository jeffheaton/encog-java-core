package org.encog.ensemble.stacking;

import java.util.ArrayList;

import org.encog.ensemble.Ensemble;
import org.encog.ensemble.EnsembleAggregator;
import org.encog.ensemble.EnsembleML;
import org.encog.ensemble.EnsembleMLMethodFactory;
import org.encog.ensemble.EnsembleTrainFactory;
import org.encog.ensemble.EnsembleTypes;
import org.encog.ensemble.EnsembleTypes.ProblemType;
import org.encog.ensemble.data.factories.WrappingNonResamplingDataSetFactory;

public class Stacking extends Ensemble {

	private int splits;

	public Stacking(int splits, int dataSetSize, EnsembleMLMethodFactory mlFactory, EnsembleTrainFactory trainFactory, EnsembleAggregator aggregator)
	{
		int dataSplits = aggregator.needsTraining() ? splits + 1 : splits;
		this.dataSetFactory = new WrappingNonResamplingDataSetFactory(dataSplits);
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
		this.initMembersBySplits(this.splits);
	}

	@Override
	public ProblemType getProblemType() {
		return EnsembleTypes.ProblemType.CLASSIFICATION;
	}

	@Override
	public EnsembleML getMember(int memberNumber) {
		return members.get(memberNumber);
	}

	public void trainStep() {
		for (EnsembleML current : members)
		{
			current.trainStep();
		}
	}


}
