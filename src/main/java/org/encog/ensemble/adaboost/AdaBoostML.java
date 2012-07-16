package org.encog.ensemble.adaboost;

import org.encog.ensemble.EnsembleDataSet;
import org.encog.ensemble.EnsembleML;
import org.encog.ml.MLMethod;
import org.encog.ml.data.MLData;
import org.encog.ml.train.MLTrain;
import org.encog.neural.networks.BasicNetwork;

public class AdaBoostML implements EnsembleML {
	private BasicNetwork ml;
	
	public AdaBoostML(MLMethod fromML) {
		setMl(fromML);
	}

	@Override
	public int classify(MLData input) {
		return ml.classify(input);
	}

	@Override
	public int getInputCount() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getOutputCount() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public MLData compute(MLData input) {
		return ml.compute(input);
	}

	@Override
	public void setTrainingSet(EnsembleDataSet dataSet) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public EnsembleDataSet getTrainingSet() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void train(MLTrain train, double targetError) {
		train (train,targetError,false);
	}

	@Override
	public void train(MLTrain train, double targetError, boolean verbose) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setMl(MLMethod newMl) {
		this.ml = (BasicNetwork) newMl;
	}

	@Override
	public MLMethod getMl() {
		return ml;
	}

	public int winner(MLData input) {
		return ml.winner(input);
	}

}
