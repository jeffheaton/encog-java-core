package org.encog.ensembles.bagging;

import java.util.ArrayList;

import org.encog.ensembles.Ensemble;
import org.encog.ensembles.EnsembleDataSet;
import org.encog.ensembles.EnsembleDataSetFactory;
import org.encog.ensembles.EnsembleML;
import org.encog.ensembles.EnsembleTrain;
import org.encog.ensembles.EnsembleTypes;
import org.encog.ensembles.EnsembleTypes.ProblemType;
import org.encog.ml.data.MLData;
import org.encog.ml.data.MLDataSet;

public class Bagging implements Ensemble {

	private BaggingDataSetFactory dataSetFactory;
	private EnsembleTrain train;
	private ArrayList<EnsembleML> members;
	private int splits;
	
	public Bagging(int splits, int dataSetSize)
	{
		this.dataSetFactory = new BaggingDataSetFactory();
		dataSetFactory.setDataSetSize(dataSetSize);
		this.splits = splits;
	}
	
	@Override
	public void setTrainingMethod(EnsembleTrain newTrain) {
		this.train = newTrain;
	}

	private void initMembers()
	{
		if ((this.dataSetFactory != null) && 
			(this.members.size() > 0) &&
			(this.dataSetFactory.hasSource()))
		{
			for (int i = 0; i < splits; i++)
			{
				BaggingML newML = new BaggingML();
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
		// TODO Auto-generated method stub

	}

	@Override
	public MLDataSet getTrainingSet(int setNumber) {
		return members.get(setNumber).getTrainingSet();
	}

	@Override
	public MLData compute(MLData input) {
		// TODO Auto-generated method stub
		return null;
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
		// TODO Auto-generated method stub
		
	}

}
