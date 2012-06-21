package org.encog.ensemble.bagging;

import org.encog.ensemble.EnsembleDataSet;
import org.encog.ensemble.EnsembleML;
import org.encog.ensemble.EnsembleTrain;
import org.encog.ml.MLMethod;
import org.encog.ml.data.MLData;
import org.encog.neural.networks.BasicNetwork;

public class BaggingML implements EnsembleML {

	private EnsembleDataSet trainingSet;
	//TODO: this needs to become a generic type
	private BasicNetwork ml;
	
	public BaggingML(MLMethod fromML) {
		setMl(fromML);
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
	public void train(EnsembleTrain train, double targetError) {
		double error = ml.calculateError(getTrainingSet());
		do {
			train.iteration();
			error = ml.calculateError(getTrainingSet());
		} while (error > targetError);
	}

	@Override
	public void setMl(MLMethod newMl) {
		ml = (BasicNetwork) newMl;
	}

	@Override
	public MLMethod getMl() {
		return ml;
	}

	public int classify(MLData input) {
		return ml.classify(input);
	}
	
	public MLData compute(MLData input) {
		return ml.compute(input);
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
