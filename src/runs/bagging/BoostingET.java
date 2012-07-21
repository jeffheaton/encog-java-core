package bagging;

import org.encog.ensemble.EnsembleMLMethodFactory;
import org.encog.ensemble.EnsembleTrainFactory;
import org.encog.ensemble.adaboost.AdaBoost;
import org.encog.ensemble.aggregator.EnsembleAggregator;
import org.encog.ml.data.MLData;

import helpers.DataLoader;
import helpers.EvaluationTechnique;

public class BoostingET extends EvaluationTechnique {

	private AdaBoost boosting;
	private int T;
	
	public BoostingET(int T, String label, EnsembleMLMethodFactory mlMethod, EnsembleTrainFactory trainFactory, EnsembleAggregator aggregator) {
		this.T = T;
		this.label = label;
		this.mlMethod = mlMethod;
		this.trainFactory = trainFactory;
		this.aggregator = aggregator;
	}

	@Override
	public int train(double trainToError, boolean verbose) {
		return boosting.train(trainToError,verbose);
	}

	@Override
	public void init(DataLoader dataLoader) {
		boosting = new AdaBoost(T,mlMethod,trainFactory,aggregator);
		setTrainingSet(dataLoader.getTrainingSet());
		setTestSet(dataLoader.getTestSet());
		boosting.setTrainingData(trainingSet);
	}

	@Override
	public MLData compute(MLData input) {
		return boosting.compute(input);
	}
	
}
