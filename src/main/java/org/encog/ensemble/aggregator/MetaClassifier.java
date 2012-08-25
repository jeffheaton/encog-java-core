package org.encog.ensemble.aggregator;

import java.util.ArrayList;

import org.encog.ensemble.EnsembleAggregator;
import org.encog.ensemble.EnsembleML;
import org.encog.ensemble.data.EnsembleDataSet;
import org.encog.ml.data.MLData;
import org.encog.ml.data.basic.BasicMLData;

public class MetaClassifier implements EnsembleAggregator {

	EnsembleML classifier;
	double trainError;
	
	public MetaClassifier(double trainError, EnsembleML classifier) {
		this.classifier = classifier;
		this.trainError = trainError;
	}
	
	@Override
	public MLData evaluate(ArrayList<MLData> outputs) {
		BasicMLData merged_outputs = new BasicMLData(0);
		int index = 0;
		for(MLData output:outputs)
			for(double val:output.getData()) {
				merged_outputs.add(index++,val);
			}
		return classifier.compute(merged_outputs);
	}
	
	@Override
	public String toString() {
		return "metaclassifier:" + classifier.toString();
	}

	@Override
	public void train() {
		classifier.train(trainError);
	}
	
	@Override
	public void setTrainingSet(EnsembleDataSet trainingSet) {
		classifier.setTrainingSet(trainingSet);
	}

	@Override
	public boolean needsTraining() {
		return false;
	}
}
