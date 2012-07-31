package org.encog.ensemble.adaboost;

import org.encog.ensemble.EnsembleML;
import org.encog.ensemble.data.EnsembleDataSet;
import org.encog.ml.MLMethod;
import org.encog.ml.data.MLData;
import org.encog.ml.train.MLTrain;
import org.encog.neural.networks.BasicNetwork;
import org.encog.util.EngineArray;

public class AdaBoostML implements EnsembleML {
	private EnsembleDataSet trainingSet;
	private BasicNetwork ml;
	
	public AdaBoostML(MLMethod fromML) {
		setMl(fromML);
	}

	@Override
	public int classify(MLData input) {
		return ml.classify(input);
	}

	@Override
	public MLData compute(MLData input) {
		return ml.compute(input);
	}

	@Override
	public void setTrainingSet(EnsembleDataSet dataSet) {
		this.trainingSet = dataSet;
	}

	@Override
	public EnsembleDataSet getTrainingSet() {
		return trainingSet;
	}

	@Override
	public void train(MLTrain train, double targetError) {
		train (train,targetError,false);
	}

	@Override
	public void train(MLTrain train, double targetError, boolean verbose) {
		double error;
		int iteration=0;
		do {
			train.iteration();
			iteration++;
			error = train.getError();
			if (verbose) System.out.println(iteration + " " + error);
		} while ((error > targetError) && train.canContinue());
		train.finishTraining();
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
		return EngineArray.maxIndex(input.getData());
	}
	
	@Override
	public int getInputCount() {
		return ml.getInputCount();
	}

	@Override
	public int getOutputCount() {
		return ml.getOutputCount();
	}

}
