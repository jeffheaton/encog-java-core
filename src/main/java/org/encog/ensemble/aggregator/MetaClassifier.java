package org.encog.ensemble.aggregator;

import java.util.ArrayList;

import org.encog.ensemble.EnsembleAggregator;
import org.encog.ensemble.EnsembleML;
import org.encog.ensemble.EnsembleMLMethodFactory;
import org.encog.ensemble.EnsembleTrainFactory;
import org.encog.ensemble.GenericEnsembleML;
import org.encog.ensemble.data.EnsembleDataSet;
import org.encog.ml.data.MLData;
import org.encog.ml.data.basic.BasicMLData;

public class MetaClassifier implements EnsembleAggregator {

	EnsembleML classifier;
	EnsembleMLMethodFactory mlFact;
	EnsembleTrainFactory etFact;
	double trainError;

	public MetaClassifier(double trainError, EnsembleMLMethodFactory mlFact, EnsembleTrainFactory etFact) {
		this.trainError = trainError;
		this.mlFact = mlFact;
		this.etFact = etFact;
	}

	@Override
	public MLData evaluate(ArrayList<MLData> outputs) {
		BasicMLData merged_outputs = new BasicMLData(classifier.getInputCount());
		int index = 0;
		for(MLData output:outputs)
			for(double val:output.getData()) {
				merged_outputs.add(index++,val);
			}
		return classifier.compute(merged_outputs);
	}

	@Override
	public String getLabel() {
		return "metaclassifier-" + mlFact.getLabel() + "-" + trainError;
	}

	@Override
	public void train() {
		if (classifier != null)
			classifier.train(trainError);
		else
			System.err.println("Trying to train a null classifier in MetaClassifier");
	}

	@Override
	public void setTrainingSet(EnsembleDataSet trainingSet) {
		classifier = new GenericEnsembleML(mlFact.createML(trainingSet.getInputSize(), trainingSet.getIdealSize()),mlFact.getLabel());
		classifier.setTraining(etFact.getTraining(classifier.getMl(), trainingSet));
		classifier.setTrainingSet(trainingSet);
	}

	@Override
	public boolean needsTraining() {
		return true;
	}
}
